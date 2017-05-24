package org.vidogram.ui;

import a.a.a.a.c.a;
import a.a.a.a.i;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;
import itman.Vidofilm.a.v;
import itman.Vidofilm.b;
import itman.Vidofilm.d.d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.BuildVars;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NativeCrashManager;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.SendMessagesHelper;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.UserObject;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.browser.Browser;
import org.vidogram.messenger.query.DraftQuery;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatInvite;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.vidogram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputGameShortName;
import org.vidogram.tgnet.TLRPC.TL_inputMediaGame;
import org.vidogram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.vidogram.tgnet.TLRPC.TL_messages_checkChatInvite;
import org.vidogram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.vidogram.tgnet.TLRPC.Updates;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBarLayout;
import org.vidogram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.DrawerLayoutContainer;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Adapters.DrawerLayoutAdapter;
import org.vidogram.ui.Components.EmbedBottomSheet;
import org.vidogram.ui.Components.JoinGroupAlert;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.PasscodeView;
import org.vidogram.ui.Components.PasscodeView.PasscodeViewDelegate;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.StickersAlert;
import org.vidogram.ui.Components.ThemeEditorView;

public class LaunchActivity extends Activity
  implements NotificationCenter.NotificationCenterDelegate, ActionBarLayout.ActionBarLayoutDelegate, DialogsActivity.DialogsActivityDelegate
{
  private static ArrayList<BaseFragment> layerFragmentsStack;
  private static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList();
  private static ArrayList<BaseFragment> rightFragmentsStack;
  private ActionBarLayout actionBarLayout;
  private View backgroundTablet;
  private ArrayList<TLRPC.User> contactsToSend;
  private int currentConnectionState;
  private String documentsMimeType;
  private ArrayList<String> documentsOriginalPathsArray;
  private ArrayList<String> documentsPathsArray;
  private ArrayList<Uri> documentsUrisArray;
  private DrawerLayoutAdapter drawerLayoutAdapter;
  protected DrawerLayoutContainer drawerLayoutContainer;
  private boolean finished;
  private ActionBarLayout layersActionBarLayout;
  private Runnable lockRunnable;
  private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
  private Intent passcodeSaveIntent;
  private boolean passcodeSaveIntentIsNew;
  private boolean passcodeSaveIntentIsRestore;
  private PasscodeView passcodeView;
  private ArrayList<Uri> photoPathsArray;
  private ActionBarLayout rightActionBarLayout;
  private String sendingText;
  private FrameLayout shadowTablet;
  private FrameLayout shadowTabletSide;
  private RecyclerListView sideMenu;
  private boolean tabletFullSize;
  private String videoPath;
  private AlertDialog visibleDialog;

  static
  {
    layerFragmentsStack = new ArrayList();
    rightFragmentsStack = new ArrayList();
  }

  private void checkLayout()
  {
    int i = 8;
    int j = 0;
    if ((!AndroidUtilities.isTablet()) || (this.rightActionBarLayout == null))
      return;
    if ((!AndroidUtilities.isInMultiwindow) && ((!AndroidUtilities.isSmallTablet()) || (getResources().getConfiguration().orientation == 2)))
    {
      this.tabletFullSize = false;
      if (this.actionBarLayout.fragmentsStack.size() >= 2)
      {
        while (1 < this.actionBarLayout.fragmentsStack.size())
        {
          localObject = (BaseFragment)this.actionBarLayout.fragmentsStack.get(1);
          if ((localObject instanceof ChatActivity))
            ((ChatActivity)localObject).setIgnoreAttachOnPause(true);
          ((BaseFragment)localObject).onPause();
          this.actionBarLayout.fragmentsStack.remove(1);
          this.rightActionBarLayout.fragmentsStack.add(localObject);
        }
        if (this.passcodeView.getVisibility() != 0)
        {
          this.actionBarLayout.showLastFragment();
          this.rightActionBarLayout.showLastFragment();
        }
      }
      localObject = this.rightActionBarLayout;
      if (this.rightActionBarLayout.fragmentsStack.isEmpty())
      {
        i = 8;
        ((ActionBarLayout)localObject).setVisibility(i);
        localObject = this.backgroundTablet;
        if (!this.rightActionBarLayout.fragmentsStack.isEmpty())
          break label245;
        i = 0;
        label209: ((View)localObject).setVisibility(i);
        localObject = this.shadowTabletSide;
        if (this.actionBarLayout.fragmentsStack.isEmpty())
          break label251;
      }
      label245: label251: for (i = j; ; i = 8)
      {
        ((FrameLayout)localObject).setVisibility(i);
        return;
        i = 0;
        break;
        i = 8;
        break label209;
      }
    }
    this.tabletFullSize = true;
    if (!this.rightActionBarLayout.fragmentsStack.isEmpty())
    {
      while (this.rightActionBarLayout.fragmentsStack.size() > 0)
      {
        localObject = (BaseFragment)this.rightActionBarLayout.fragmentsStack.get(0);
        if ((localObject instanceof ChatActivity))
          ((ChatActivity)localObject).setIgnoreAttachOnPause(true);
        ((BaseFragment)localObject).onPause();
        this.rightActionBarLayout.fragmentsStack.remove(0);
        this.actionBarLayout.fragmentsStack.add(localObject);
      }
      if (this.passcodeView.getVisibility() != 0)
        this.actionBarLayout.showLastFragment();
    }
    this.shadowTabletSide.setVisibility(8);
    this.rightActionBarLayout.setVisibility(8);
    Object localObject = this.backgroundTablet;
    if (!this.actionBarLayout.fragmentsStack.isEmpty());
    while (true)
    {
      ((View)localObject).setVisibility(i);
      return;
      i = 0;
    }
  }

  // ERROR //
  private boolean handleIntent(Intent paramIntent, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 291	android/content/Intent:getFlags	()I
    //   4: istore 5
    //   6: iload 4
    //   8: ifne +41 -> 49
    //   11: iconst_1
    //   12: invokestatic 295	org/vidogram/messenger/AndroidUtilities:needShowPasscode	(Z)Z
    //   15: ifne +9 -> 24
    //   18: getstatic 300	org/vidogram/messenger/UserConfig:isWaitingForPasscodeEnter	Z
    //   21: ifeq +28 -> 49
    //   24: aload_0
    //   25: invokespecial 170	org/vidogram/ui/LaunchActivity:showPasscodeActivity	()V
    //   28: aload_0
    //   29: aload_1
    //   30: putfield 195	org/vidogram/ui/LaunchActivity:passcodeSaveIntent	Landroid/content/Intent;
    //   33: aload_0
    //   34: iload_2
    //   35: putfield 200	org/vidogram/ui/LaunchActivity:passcodeSaveIntentIsNew	Z
    //   38: aload_0
    //   39: iload_3
    //   40: putfield 141	org/vidogram/ui/LaunchActivity:passcodeSaveIntentIsRestore	Z
    //   43: iconst_0
    //   44: invokestatic 303	org/vidogram/messenger/UserConfig:saveConfig	(Z)V
    //   47: iconst_0
    //   48: ireturn
    //   49: iconst_0
    //   50: istore 8
    //   52: iconst_0
    //   53: istore 4
    //   55: iconst_0
    //   56: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   59: astore 13
    //   61: iconst_0
    //   62: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   65: astore 14
    //   67: iconst_0
    //   68: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   71: astore 28
    //   73: iconst_0
    //   74: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   77: astore 15
    //   79: iconst_0
    //   80: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   83: astore 27
    //   85: iconst_0
    //   86: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   89: astore 26
    //   91: aload_1
    //   92: ifnull +5740 -> 5832
    //   95: aload_1
    //   96: invokevirtual 313	android/content/Intent:getExtras	()Landroid/os/Bundle;
    //   99: ifnull +5733 -> 5832
    //   102: aload_1
    //   103: invokevirtual 313	android/content/Intent:getExtras	()Landroid/os/Bundle;
    //   106: ldc_w 315
    //   109: lconst_0
    //   110: invokevirtual 321	android/os/Bundle:getLong	(Ljava/lang/String;J)J
    //   113: lstore 11
    //   115: iconst_0
    //   116: istore 7
    //   118: aload_0
    //   119: aconst_null
    //   120: putfield 323	org/vidogram/ui/LaunchActivity:photoPathsArray	Ljava/util/ArrayList;
    //   123: aload_0
    //   124: aconst_null
    //   125: putfield 325	org/vidogram/ui/LaunchActivity:videoPath	Ljava/lang/String;
    //   128: aload_0
    //   129: aconst_null
    //   130: putfield 327	org/vidogram/ui/LaunchActivity:sendingText	Ljava/lang/String;
    //   133: aload_0
    //   134: aconst_null
    //   135: putfield 329	org/vidogram/ui/LaunchActivity:documentsPathsArray	Ljava/util/ArrayList;
    //   138: aload_0
    //   139: aconst_null
    //   140: putfield 331	org/vidogram/ui/LaunchActivity:documentsOriginalPathsArray	Ljava/util/ArrayList;
    //   143: aload_0
    //   144: aconst_null
    //   145: putfield 333	org/vidogram/ui/LaunchActivity:documentsMimeType	Ljava/lang/String;
    //   148: aload_0
    //   149: aconst_null
    //   150: putfield 335	org/vidogram/ui/LaunchActivity:documentsUrisArray	Ljava/util/ArrayList;
    //   153: aload_0
    //   154: aconst_null
    //   155: putfield 337	org/vidogram/ui/LaunchActivity:contactsToSend	Ljava/util/ArrayList;
    //   158: invokestatic 340	org/vidogram/messenger/UserConfig:isClientActivated	()Z
    //   161: ifeq +5371 -> 5532
    //   164: ldc_w 341
    //   167: iload 5
    //   169: iand
    //   170: ifne +5362 -> 5532
    //   173: aload_1
    //   174: ifnull +5358 -> 5532
    //   177: aload_1
    //   178: invokevirtual 345	android/content/Intent:getAction	()Ljava/lang/String;
    //   181: ifnull +5351 -> 5532
    //   184: iload_3
    //   185: ifne +5347 -> 5532
    //   188: ldc_w 347
    //   191: aload_1
    //   192: invokevirtual 345	android/content/Intent:getAction	()Ljava/lang/String;
    //   195: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   198: ifeq +1483 -> 1681
    //   201: iconst_0
    //   202: istore 6
    //   204: aload_1
    //   205: invokevirtual 355	android/content/Intent:getType	()Ljava/lang/String;
    //   208: astore 18
    //   210: aload 18
    //   212: ifnull +988 -> 1200
    //   215: aload 18
    //   217: ldc_w 357
    //   220: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   223: ifeq +977 -> 1200
    //   226: aload_1
    //   227: invokevirtual 313	android/content/Intent:getExtras	()Landroid/os/Bundle;
    //   230: ldc_w 359
    //   233: invokevirtual 362	android/os/Bundle:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   236: checkcast 364	android/net/Uri
    //   239: astore 16
    //   241: aload 16
    //   243: ifnull +951 -> 1194
    //   246: aload_0
    //   247: invokevirtual 368	org/vidogram/ui/LaunchActivity:getContentResolver	()Landroid/content/ContentResolver;
    //   250: aload 16
    //   252: invokevirtual 374	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   255: astore 21
    //   257: new 118	java/util/ArrayList
    //   260: dup
    //   261: invokespecial 121	java/util/ArrayList:<init>	()V
    //   264: astore 20
    //   266: aconst_null
    //   267: astore 16
    //   269: new 376	java/io/BufferedReader
    //   272: dup
    //   273: new 378	java/io/InputStreamReader
    //   276: dup
    //   277: aload 21
    //   279: ldc_w 380
    //   282: invokespecial 383	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/lang/String;)V
    //   285: invokespecial 386	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   288: astore 22
    //   290: aload 22
    //   292: invokevirtual 389	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   295: astore 17
    //   297: aload 17
    //   299: ifnull +708 -> 1007
    //   302: aload 17
    //   304: invokestatic 395	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   307: aload 17
    //   309: ldc_w 397
    //   312: invokevirtual 401	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   315: astore 23
    //   317: aload 23
    //   319: arraylength
    //   320: iconst_2
    //   321: if_icmpne -31 -> 290
    //   324: aload 23
    //   326: iconst_0
    //   327: aaload
    //   328: ldc_w 403
    //   331: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   334: ifeq +38 -> 372
    //   337: aload 23
    //   339: iconst_1
    //   340: aaload
    //   341: ldc_w 405
    //   344: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   347: ifeq +25 -> 372
    //   350: new 64	org/vidogram/ui/LaunchActivity$VcardData
    //   353: dup
    //   354: aload_0
    //   355: aconst_null
    //   356: invokespecial 408	org/vidogram/ui/LaunchActivity$VcardData:<init>	(Lorg/vidogram/ui/LaunchActivity;Lorg/vidogram/ui/LaunchActivity$1;)V
    //   359: astore 16
    //   361: aload 20
    //   363: aload 16
    //   365: invokevirtual 259	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   368: pop
    //   369: goto +5455 -> 5824
    //   372: aload 23
    //   374: iconst_0
    //   375: aaload
    //   376: ldc_w 410
    //   379: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   382: ifeq +5442 -> 5824
    //   385: aload 23
    //   387: iconst_1
    //   388: aaload
    //   389: ldc_w 405
    //   392: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   395: ifeq +5429 -> 5824
    //   398: aconst_null
    //   399: astore 16
    //   401: goto +5423 -> 5824
    //   404: aload 23
    //   406: iconst_0
    //   407: aaload
    //   408: ldc_w 412
    //   411: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   414: ifne +27 -> 441
    //   417: aload 23
    //   419: iconst_0
    //   420: aaload
    //   421: ldc_w 418
    //   424: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   427: ifeq +535 -> 962
    //   430: aload 16
    //   432: getfield 421	org/vidogram/ui/LaunchActivity$VcardData:name	Ljava/lang/String;
    //   435: invokestatic 426	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   438: ifeq +524 -> 962
    //   441: aload 23
    //   443: iconst_0
    //   444: aaload
    //   445: ldc_w 428
    //   448: invokevirtual 401	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   451: astore 24
    //   453: aload 24
    //   455: arraylength
    //   456: istore 6
    //   458: aconst_null
    //   459: astore 17
    //   461: aconst_null
    //   462: astore 18
    //   464: iconst_0
    //   465: istore 5
    //   467: iload 5
    //   469: iload 6
    //   471: if_icmpge +82 -> 553
    //   474: aload 24
    //   476: iload 5
    //   478: aaload
    //   479: ldc_w 430
    //   482: invokevirtual 401	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   485: astore 25
    //   487: aload 25
    //   489: arraylength
    //   490: iconst_2
    //   491: if_icmpeq +10 -> 501
    //   494: aload 17
    //   496: astore 19
    //   498: goto +5340 -> 5838
    //   501: aload 25
    //   503: iconst_0
    //   504: aaload
    //   505: ldc_w 432
    //   508: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   511: ifeq +12 -> 523
    //   514: aload 25
    //   516: iconst_1
    //   517: aaload
    //   518: astore 19
    //   520: goto +5318 -> 5838
    //   523: aload 17
    //   525: astore 19
    //   527: aload 25
    //   529: iconst_0
    //   530: aaload
    //   531: ldc_w 434
    //   534: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   537: ifeq +5301 -> 5838
    //   540: aload 25
    //   542: iconst_1
    //   543: aaload
    //   544: astore 18
    //   546: aload 17
    //   548: astore 19
    //   550: goto +5288 -> 5838
    //   553: aload 16
    //   555: aload 23
    //   557: iconst_1
    //   558: aaload
    //   559: putfield 421	org/vidogram/ui/LaunchActivity$VcardData:name	Ljava/lang/String;
    //   562: aload 18
    //   564: ifnull +5287 -> 5851
    //   567: aload 18
    //   569: ldc_w 436
    //   572: invokevirtual 439	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   575: ifeq +5276 -> 5851
    //   578: aload 16
    //   580: getfield 421	org/vidogram/ui/LaunchActivity$VcardData:name	Ljava/lang/String;
    //   583: ldc_w 430
    //   586: invokevirtual 442	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   589: ifeq +44 -> 633
    //   592: aload 18
    //   594: ifnull +39 -> 633
    //   597: aload 16
    //   599: aload 16
    //   601: getfield 421	org/vidogram/ui/LaunchActivity$VcardData:name	Ljava/lang/String;
    //   604: iconst_0
    //   605: aload 16
    //   607: getfield 421	org/vidogram/ui/LaunchActivity$VcardData:name	Ljava/lang/String;
    //   610: invokevirtual 445	java/lang/String:length	()I
    //   613: iconst_1
    //   614: isub
    //   615: invokevirtual 449	java/lang/String:substring	(II)Ljava/lang/String;
    //   618: putfield 421	org/vidogram/ui/LaunchActivity$VcardData:name	Ljava/lang/String;
    //   621: aload 22
    //   623: invokevirtual 389	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   626: astore 19
    //   628: aload 19
    //   630: ifnonnull +55 -> 685
    //   633: aload 16
    //   635: getfield 421	org/vidogram/ui/LaunchActivity$VcardData:name	Ljava/lang/String;
    //   638: invokevirtual 453	java/lang/String:getBytes	()[B
    //   641: invokestatic 457	org/vidogram/messenger/AndroidUtilities:decodeQuotedPrintable	([B)[B
    //   644: astore 18
    //   646: aload 18
    //   648: ifnull +5203 -> 5851
    //   651: aload 18
    //   653: arraylength
    //   654: ifeq +5197 -> 5851
    //   657: new 349	java/lang/String
    //   660: dup
    //   661: aload 18
    //   663: aload 17
    //   665: invokespecial 460	java/lang/String:<init>	([BLjava/lang/String;)V
    //   668: astore 17
    //   670: aload 17
    //   672: ifnull +5179 -> 5851
    //   675: aload 16
    //   677: aload 17
    //   679: putfield 421	org/vidogram/ui/LaunchActivity$VcardData:name	Ljava/lang/String;
    //   682: goto +5169 -> 5851
    //   685: aload 16
    //   687: new 462	java/lang/StringBuilder
    //   690: dup
    //   691: invokespecial 463	java/lang/StringBuilder:<init>	()V
    //   694: aload 16
    //   696: getfield 421	org/vidogram/ui/LaunchActivity$VcardData:name	Ljava/lang/String;
    //   699: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   702: aload 19
    //   704: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   707: invokevirtual 470	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   710: putfield 421	org/vidogram/ui/LaunchActivity$VcardData:name	Ljava/lang/String;
    //   713: goto -135 -> 578
    //   716: astore 16
    //   718: aload 16
    //   720: invokestatic 473	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   723: iconst_1
    //   724: istore 5
    //   726: iload 5
    //   728: ifeq +14 -> 742
    //   731: aload_0
    //   732: ldc_w 475
    //   735: iconst_0
    //   736: invokestatic 481	android/widget/Toast:makeText	(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;
    //   739: invokevirtual 484	android/widget/Toast:show	()V
    //   742: iconst_0
    //   743: istore 5
    //   745: aload 26
    //   747: astore 16
    //   749: aload 27
    //   751: astore 17
    //   753: iload 7
    //   755: istore 6
    //   757: aload 13
    //   759: invokevirtual 487	java/lang/Integer:intValue	()I
    //   762: ifeq +3568 -> 4330
    //   765: new 317	android/os/Bundle
    //   768: dup
    //   769: invokespecial 488	android/os/Bundle:<init>	()V
    //   772: astore 14
    //   774: aload 14
    //   776: ldc_w 490
    //   779: aload 13
    //   781: invokevirtual 487	java/lang/Integer:intValue	()I
    //   784: invokevirtual 494	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   787: aload 15
    //   789: invokevirtual 487	java/lang/Integer:intValue	()I
    //   792: ifeq +16 -> 808
    //   795: aload 14
    //   797: ldc_w 496
    //   800: aload 15
    //   802: invokevirtual 487	java/lang/Integer:intValue	()I
    //   805: invokevirtual 494	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   808: getstatic 123	org/vidogram/ui/LaunchActivity:mainFragmentsStack	Ljava/util/ArrayList;
    //   811: invokevirtual 272	java/util/ArrayList:isEmpty	()Z
    //   814: ifne +28 -> 842
    //   817: aload 14
    //   819: getstatic 123	org/vidogram/ui/LaunchActivity:mainFragmentsStack	Ljava/util/ArrayList;
    //   822: getstatic 123	org/vidogram/ui/LaunchActivity:mainFragmentsStack	Ljava/util/ArrayList;
    //   825: invokevirtual 237	java/util/ArrayList:size	()I
    //   828: iconst_1
    //   829: isub
    //   830: invokevirtual 241	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   833: checkcast 243	org/vidogram/ui/ActionBar/BaseFragment
    //   836: invokestatic 502	org/vidogram/messenger/MessagesController:checkCanOpenChat	(Landroid/os/Bundle;Lorg/vidogram/ui/ActionBar/BaseFragment;)Z
    //   839: ifeq +4688 -> 5527
    //   842: new 245	org/vidogram/ui/ChatActivity
    //   845: dup
    //   846: aload 14
    //   848: invokespecial 505	org/vidogram/ui/ChatActivity:<init>	(Landroid/os/Bundle;)V
    //   851: astore 13
    //   853: aload_0
    //   854: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   857: aload 13
    //   859: iconst_0
    //   860: iconst_1
    //   861: iconst_1
    //   862: invokevirtual 509	org/vidogram/ui/ActionBar/ActionBarLayout:presentFragment	(Lorg/vidogram/ui/ActionBar/BaseFragment;ZZZ)Z
    //   865: ifeq +4662 -> 5527
    //   868: iconst_1
    //   869: istore_3
    //   870: iload_3
    //   871: ifne +83 -> 954
    //   874: iload_2
    //   875: ifne +79 -> 954
    //   878: invokestatic 207	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   881: ifeq +4528 -> 5409
    //   884: invokestatic 340	org/vidogram/messenger/UserConfig:isClientActivated	()Z
    //   887: ifne +4468 -> 5355
    //   890: aload_0
    //   891: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   894: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   897: invokevirtual 272	java/util/ArrayList:isEmpty	()Z
    //   900: ifeq +27 -> 927
    //   903: aload_0
    //   904: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   907: new 511	org/vidogram/ui/LoginActivity
    //   910: dup
    //   911: invokespecial 512	org/vidogram/ui/LoginActivity:<init>	()V
    //   914: invokevirtual 516	org/vidogram/ui/ActionBar/ActionBarLayout:addFragmentToStack	(Lorg/vidogram/ui/ActionBar/BaseFragment;)Z
    //   917: pop
    //   918: aload_0
    //   919: getfield 518	org/vidogram/ui/LaunchActivity:drawerLayoutContainer	Lorg/vidogram/ui/ActionBar/DrawerLayoutContainer;
    //   922: iconst_0
    //   923: iconst_0
    //   924: invokevirtual 524	org/vidogram/ui/ActionBar/DrawerLayoutContainer:setAllowOpenDrawer	(ZZ)V
    //   927: aload_0
    //   928: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   931: invokevirtual 269	org/vidogram/ui/ActionBar/ActionBarLayout:showLastFragment	()V
    //   934: invokestatic 207	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   937: ifeq +17 -> 954
    //   940: aload_0
    //   941: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   944: invokevirtual 269	org/vidogram/ui/ActionBar/ActionBarLayout:showLastFragment	()V
    //   947: aload_0
    //   948: getfield 177	org/vidogram/ui/LaunchActivity:rightActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   951: invokevirtual 269	org/vidogram/ui/ActionBar/ActionBarLayout:showLastFragment	()V
    //   954: aload_1
    //   955: aconst_null
    //   956: invokevirtual 528	android/content/Intent:setAction	(Ljava/lang/String;)Landroid/content/Intent;
    //   959: pop
    //   960: iload_3
    //   961: ireturn
    //   962: aload 23
    //   964: iconst_0
    //   965: aaload
    //   966: ldc_w 530
    //   969: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   972: ifeq +4879 -> 5851
    //   975: aload 23
    //   977: iconst_1
    //   978: aaload
    //   979: iconst_1
    //   980: invokestatic 536	org/vidogram/a/b:a	(Ljava/lang/String;Z)Ljava/lang/String;
    //   983: astore 17
    //   985: aload 17
    //   987: invokevirtual 445	java/lang/String:length	()I
    //   990: ifle +4861 -> 5851
    //   993: aload 16
    //   995: getfield 539	org/vidogram/ui/LaunchActivity$VcardData:phones	Ljava/util/ArrayList;
    //   998: aload 17
    //   1000: invokevirtual 259	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1003: pop
    //   1004: goto +4847 -> 5851
    //   1007: aload 22
    //   1009: invokevirtual 542	java/io/BufferedReader:close	()V
    //   1012: aload 21
    //   1014: invokevirtual 545	java/io/InputStream:close	()V
    //   1017: iconst_0
    //   1018: istore 5
    //   1020: iload 5
    //   1022: aload 20
    //   1024: invokevirtual 237	java/util/ArrayList:size	()I
    //   1027: if_icmpge +161 -> 1188
    //   1030: aload 20
    //   1032: iload 5
    //   1034: invokevirtual 241	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   1037: checkcast 64	org/vidogram/ui/LaunchActivity$VcardData
    //   1040: astore 16
    //   1042: aload 16
    //   1044: getfield 421	org/vidogram/ui/LaunchActivity$VcardData:name	Ljava/lang/String;
    //   1047: ifnull +132 -> 1179
    //   1050: aload 16
    //   1052: getfield 539	org/vidogram/ui/LaunchActivity$VcardData:phones	Ljava/util/ArrayList;
    //   1055: invokevirtual 272	java/util/ArrayList:isEmpty	()Z
    //   1058: ifne +121 -> 1179
    //   1061: aload_0
    //   1062: getfield 337	org/vidogram/ui/LaunchActivity:contactsToSend	Ljava/util/ArrayList;
    //   1065: ifnonnull +4789 -> 5854
    //   1068: aload_0
    //   1069: new 118	java/util/ArrayList
    //   1072: dup
    //   1073: invokespecial 121	java/util/ArrayList:<init>	()V
    //   1076: putfield 337	org/vidogram/ui/LaunchActivity:contactsToSend	Ljava/util/ArrayList;
    //   1079: goto +4775 -> 5854
    //   1082: iload 6
    //   1084: aload 16
    //   1086: getfield 539	org/vidogram/ui/LaunchActivity$VcardData:phones	Ljava/util/ArrayList;
    //   1089: invokevirtual 237	java/util/ArrayList:size	()I
    //   1092: if_icmpge +87 -> 1179
    //   1095: aload 16
    //   1097: getfield 539	org/vidogram/ui/LaunchActivity$VcardData:phones	Ljava/util/ArrayList;
    //   1100: iload 6
    //   1102: invokevirtual 241	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   1105: checkcast 349	java/lang/String
    //   1108: astore 17
    //   1110: new 547	org/vidogram/tgnet/TLRPC$TL_userContact_old2
    //   1113: dup
    //   1114: invokespecial 548	org/vidogram/tgnet/TLRPC$TL_userContact_old2:<init>	()V
    //   1117: astore 18
    //   1119: aload 18
    //   1121: aload 17
    //   1123: putfield 553	org/vidogram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   1126: aload 18
    //   1128: aload 16
    //   1130: getfield 421	org/vidogram/ui/LaunchActivity$VcardData:name	Ljava/lang/String;
    //   1133: putfield 556	org/vidogram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   1136: aload 18
    //   1138: ldc_w 558
    //   1141: putfield 561	org/vidogram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   1144: aload 18
    //   1146: iconst_0
    //   1147: putfield 564	org/vidogram/tgnet/TLRPC$User:id	I
    //   1150: aload_0
    //   1151: getfield 337	org/vidogram/ui/LaunchActivity:contactsToSend	Ljava/util/ArrayList;
    //   1154: aload 18
    //   1156: invokevirtual 259	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1159: pop
    //   1160: iload 6
    //   1162: iconst_1
    //   1163: iadd
    //   1164: istore 6
    //   1166: goto -84 -> 1082
    //   1169: astore 16
    //   1171: aload 16
    //   1173: invokestatic 473	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1176: goto -159 -> 1017
    //   1179: iload 5
    //   1181: iconst_1
    //   1182: iadd
    //   1183: istore 5
    //   1185: goto -165 -> 1020
    //   1188: iconst_0
    //   1189: istore 5
    //   1191: goto -465 -> 726
    //   1194: iconst_1
    //   1195: istore 5
    //   1197: goto -471 -> 726
    //   1200: aload_1
    //   1201: ldc_w 566
    //   1204: invokevirtual 570	android/content/Intent:getStringExtra	(Ljava/lang/String;)Ljava/lang/String;
    //   1207: astore 17
    //   1209: aload 17
    //   1211: astore 16
    //   1213: aload 17
    //   1215: ifnonnull +30 -> 1245
    //   1218: aload_1
    //   1219: ldc_w 566
    //   1222: invokevirtual 574	android/content/Intent:getCharSequenceExtra	(Ljava/lang/String;)Ljava/lang/CharSequence;
    //   1225: astore 19
    //   1227: aload 17
    //   1229: astore 16
    //   1231: aload 19
    //   1233: ifnull +12 -> 1245
    //   1236: aload 19
    //   1238: invokeinterface 577 1 0
    //   1243: astore 16
    //   1245: aload_1
    //   1246: ldc_w 579
    //   1249: invokevirtual 570	android/content/Intent:getStringExtra	(Ljava/lang/String;)Ljava/lang/String;
    //   1252: astore 19
    //   1254: aload 16
    //   1256: ifnull +233 -> 1489
    //   1259: aload 16
    //   1261: invokevirtual 445	java/lang/String:length	()I
    //   1264: ifeq +225 -> 1489
    //   1267: aload 16
    //   1269: ldc_w 581
    //   1272: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   1275: ifne +18 -> 1293
    //   1278: aload 16
    //   1280: astore 17
    //   1282: aload 16
    //   1284: ldc_w 583
    //   1287: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   1290: ifeq +52 -> 1342
    //   1293: aload 16
    //   1295: astore 17
    //   1297: aload 19
    //   1299: ifnull +43 -> 1342
    //   1302: aload 16
    //   1304: astore 17
    //   1306: aload 19
    //   1308: invokevirtual 445	java/lang/String:length	()I
    //   1311: ifeq +31 -> 1342
    //   1314: new 462	java/lang/StringBuilder
    //   1317: dup
    //   1318: invokespecial 463	java/lang/StringBuilder:<init>	()V
    //   1321: aload 19
    //   1323: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1326: ldc_w 585
    //   1329: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1332: aload 16
    //   1334: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1337: invokevirtual 470	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1340: astore 17
    //   1342: aload_0
    //   1343: aload 17
    //   1345: putfield 327	org/vidogram/ui/LaunchActivity:sendingText	Ljava/lang/String;
    //   1348: aload_1
    //   1349: ldc_w 359
    //   1352: invokevirtual 589	android/content/Intent:getParcelableExtra	(Ljava/lang/String;)Landroid/os/Parcelable;
    //   1355: astore 17
    //   1357: aload 17
    //   1359: ifnull +309 -> 1668
    //   1362: aload 17
    //   1364: astore 16
    //   1366: aload 17
    //   1368: instanceof 364
    //   1371: ifne +13 -> 1384
    //   1374: aload 17
    //   1376: invokevirtual 592	java/lang/Object:toString	()Ljava/lang/String;
    //   1379: invokestatic 596	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   1382: astore 16
    //   1384: aload 16
    //   1386: checkcast 364	android/net/Uri
    //   1389: astore 19
    //   1391: iload 6
    //   1393: istore 5
    //   1395: aload 19
    //   1397: ifnull +18 -> 1415
    //   1400: iload 6
    //   1402: istore 5
    //   1404: aload 19
    //   1406: invokestatic 600	org/vidogram/messenger/AndroidUtilities:isInternalUri	(Landroid/net/Uri;)Z
    //   1409: ifeq +6 -> 1415
    //   1412: iconst_1
    //   1413: istore 5
    //   1415: iload 5
    //   1417: ifne +69 -> 1486
    //   1420: aload 19
    //   1422: ifnull +89 -> 1511
    //   1425: aload 18
    //   1427: ifnull +14 -> 1441
    //   1430: aload 18
    //   1432: ldc_w 602
    //   1435: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   1438: ifne +20 -> 1458
    //   1441: aload 19
    //   1443: invokevirtual 603	android/net/Uri:toString	()Ljava/lang/String;
    //   1446: invokevirtual 606	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   1449: ldc_w 608
    //   1452: invokevirtual 442	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   1455: ifeq +56 -> 1511
    //   1458: aload_0
    //   1459: getfield 323	org/vidogram/ui/LaunchActivity:photoPathsArray	Ljava/util/ArrayList;
    //   1462: ifnonnull +14 -> 1476
    //   1465: aload_0
    //   1466: new 118	java/util/ArrayList
    //   1469: dup
    //   1470: invokespecial 121	java/util/ArrayList:<init>	()V
    //   1473: putfield 323	org/vidogram/ui/LaunchActivity:photoPathsArray	Ljava/util/ArrayList;
    //   1476: aload_0
    //   1477: getfield 323	org/vidogram/ui/LaunchActivity:photoPathsArray	Ljava/util/ArrayList;
    //   1480: aload 19
    //   1482: invokevirtual 259	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1485: pop
    //   1486: goto -760 -> 726
    //   1489: aload 19
    //   1491: ifnull -143 -> 1348
    //   1494: aload 19
    //   1496: invokevirtual 445	java/lang/String:length	()I
    //   1499: ifle -151 -> 1348
    //   1502: aload_0
    //   1503: aload 19
    //   1505: putfield 327	org/vidogram/ui/LaunchActivity:sendingText	Ljava/lang/String;
    //   1508: goto -160 -> 1348
    //   1511: aload 19
    //   1513: invokestatic 612	org/vidogram/messenger/AndroidUtilities:getPath	(Landroid/net/Uri;)Ljava/lang/String;
    //   1516: astore 17
    //   1518: aload 17
    //   1520: ifnull +111 -> 1631
    //   1523: aload 17
    //   1525: astore 16
    //   1527: aload 17
    //   1529: ldc_w 614
    //   1532: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   1535: ifeq +16 -> 1551
    //   1538: aload 17
    //   1540: ldc_w 616
    //   1543: ldc_w 558
    //   1546: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   1549: astore 16
    //   1551: aload 18
    //   1553: ifnull +23 -> 1576
    //   1556: aload 18
    //   1558: ldc_w 622
    //   1561: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   1564: ifeq +12 -> 1576
    //   1567: aload_0
    //   1568: aload 16
    //   1570: putfield 325	org/vidogram/ui/LaunchActivity:videoPath	Ljava/lang/String;
    //   1573: goto -87 -> 1486
    //   1576: aload_0
    //   1577: getfield 329	org/vidogram/ui/LaunchActivity:documentsPathsArray	Ljava/util/ArrayList;
    //   1580: ifnonnull +25 -> 1605
    //   1583: aload_0
    //   1584: new 118	java/util/ArrayList
    //   1587: dup
    //   1588: invokespecial 121	java/util/ArrayList:<init>	()V
    //   1591: putfield 329	org/vidogram/ui/LaunchActivity:documentsPathsArray	Ljava/util/ArrayList;
    //   1594: aload_0
    //   1595: new 118	java/util/ArrayList
    //   1598: dup
    //   1599: invokespecial 121	java/util/ArrayList:<init>	()V
    //   1602: putfield 331	org/vidogram/ui/LaunchActivity:documentsOriginalPathsArray	Ljava/util/ArrayList;
    //   1605: aload_0
    //   1606: getfield 329	org/vidogram/ui/LaunchActivity:documentsPathsArray	Ljava/util/ArrayList;
    //   1609: aload 16
    //   1611: invokevirtual 259	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1614: pop
    //   1615: aload_0
    //   1616: getfield 331	org/vidogram/ui/LaunchActivity:documentsOriginalPathsArray	Ljava/util/ArrayList;
    //   1619: aload 19
    //   1621: invokevirtual 603	android/net/Uri:toString	()Ljava/lang/String;
    //   1624: invokevirtual 259	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1627: pop
    //   1628: goto -142 -> 1486
    //   1631: aload_0
    //   1632: getfield 335	org/vidogram/ui/LaunchActivity:documentsUrisArray	Ljava/util/ArrayList;
    //   1635: ifnonnull +14 -> 1649
    //   1638: aload_0
    //   1639: new 118	java/util/ArrayList
    //   1642: dup
    //   1643: invokespecial 121	java/util/ArrayList:<init>	()V
    //   1646: putfield 335	org/vidogram/ui/LaunchActivity:documentsUrisArray	Ljava/util/ArrayList;
    //   1649: aload_0
    //   1650: getfield 335	org/vidogram/ui/LaunchActivity:documentsUrisArray	Ljava/util/ArrayList;
    //   1653: aload 19
    //   1655: invokevirtual 259	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1658: pop
    //   1659: aload_0
    //   1660: aload 18
    //   1662: putfield 333	org/vidogram/ui/LaunchActivity:documentsMimeType	Ljava/lang/String;
    //   1665: goto -179 -> 1486
    //   1668: aload_0
    //   1669: getfield 327	org/vidogram/ui/LaunchActivity:sendingText	Ljava/lang/String;
    //   1672: ifnonnull +4146 -> 5818
    //   1675: iconst_1
    //   1676: istore 5
    //   1678: goto -952 -> 726
    //   1681: aload_1
    //   1682: invokevirtual 345	android/content/Intent:getAction	()Ljava/lang/String;
    //   1685: ldc_w 624
    //   1688: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1691: ifeq +456 -> 2147
    //   1694: aload_1
    //   1695: ldc_w 359
    //   1698: invokevirtual 628	android/content/Intent:getParcelableArrayListExtra	(Ljava/lang/String;)Ljava/util/ArrayList;
    //   1701: astore 17
    //   1703: aload_1
    //   1704: invokevirtual 355	android/content/Intent:getType	()Ljava/lang/String;
    //   1707: astore 19
    //   1709: aload 17
    //   1711: ifnull +4097 -> 5808
    //   1714: iconst_0
    //   1715: istore 5
    //   1717: iload 5
    //   1719: aload 17
    //   1721: invokevirtual 237	java/util/ArrayList:size	()I
    //   1724: if_icmpge +74 -> 1798
    //   1727: aload 17
    //   1729: iload 5
    //   1731: invokevirtual 241	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   1734: checkcast 630	android/os/Parcelable
    //   1737: astore 18
    //   1739: aload 18
    //   1741: astore 16
    //   1743: aload 18
    //   1745: instanceof 364
    //   1748: ifne +13 -> 1761
    //   1751: aload 18
    //   1753: invokevirtual 592	java/lang/Object:toString	()Ljava/lang/String;
    //   1756: invokestatic 596	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   1759: astore 16
    //   1761: aload 16
    //   1763: checkcast 364	android/net/Uri
    //   1766: astore 16
    //   1768: aload 16
    //   1770: ifnull +4045 -> 5815
    //   1773: aload 16
    //   1775: invokestatic 600	org/vidogram/messenger/AndroidUtilities:isInternalUri	(Landroid/net/Uri;)Z
    //   1778: ifeq +4037 -> 5815
    //   1781: aload 17
    //   1783: iload 5
    //   1785: invokevirtual 255	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   1788: pop
    //   1789: iload 5
    //   1791: iconst_1
    //   1792: isub
    //   1793: istore 5
    //   1795: goto +4065 -> 5860
    //   1798: aload 17
    //   1800: invokevirtual 272	java/util/ArrayList:isEmpty	()Z
    //   1803: ifeq +4005 -> 5808
    //   1806: aconst_null
    //   1807: astore 16
    //   1809: aload 16
    //   1811: ifnull +317 -> 2128
    //   1814: aload 19
    //   1816: ifnull +142 -> 1958
    //   1819: aload 19
    //   1821: ldc_w 602
    //   1824: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   1827: ifeq +131 -> 1958
    //   1830: iconst_0
    //   1831: istore 5
    //   1833: iload 5
    //   1835: aload 16
    //   1837: invokevirtual 237	java/util/ArrayList:size	()I
    //   1840: if_icmpge +81 -> 1921
    //   1843: aload 16
    //   1845: iload 5
    //   1847: invokevirtual 241	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   1850: checkcast 630	android/os/Parcelable
    //   1853: astore 18
    //   1855: aload 18
    //   1857: astore 17
    //   1859: aload 18
    //   1861: instanceof 364
    //   1864: ifne +13 -> 1877
    //   1867: aload 18
    //   1869: invokevirtual 592	java/lang/Object:toString	()Ljava/lang/String;
    //   1872: invokestatic 596	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   1875: astore 17
    //   1877: aload 17
    //   1879: checkcast 364	android/net/Uri
    //   1882: astore 17
    //   1884: aload_0
    //   1885: getfield 323	org/vidogram/ui/LaunchActivity:photoPathsArray	Ljava/util/ArrayList;
    //   1888: ifnonnull +14 -> 1902
    //   1891: aload_0
    //   1892: new 118	java/util/ArrayList
    //   1895: dup
    //   1896: invokespecial 121	java/util/ArrayList:<init>	()V
    //   1899: putfield 323	org/vidogram/ui/LaunchActivity:photoPathsArray	Ljava/util/ArrayList;
    //   1902: aload_0
    //   1903: getfield 323	org/vidogram/ui/LaunchActivity:photoPathsArray	Ljava/util/ArrayList;
    //   1906: aload 17
    //   1908: invokevirtual 259	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1911: pop
    //   1912: iload 5
    //   1914: iconst_1
    //   1915: iadd
    //   1916: istore 5
    //   1918: goto -85 -> 1833
    //   1921: iconst_0
    //   1922: istore 5
    //   1924: iload 5
    //   1926: ifeq +14 -> 1940
    //   1929: aload_0
    //   1930: ldc_w 475
    //   1933: iconst_0
    //   1934: invokestatic 481	android/widget/Toast:makeText	(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;
    //   1937: invokevirtual 484	android/widget/Toast:show	()V
    //   1940: iconst_0
    //   1941: istore 5
    //   1943: aload 26
    //   1945: astore 16
    //   1947: aload 27
    //   1949: astore 17
    //   1951: iload 7
    //   1953: istore 6
    //   1955: goto -1198 -> 757
    //   1958: iconst_0
    //   1959: istore 5
    //   1961: iload 5
    //   1963: aload 16
    //   1965: invokevirtual 237	java/util/ArrayList:size	()I
    //   1968: if_icmpge +154 -> 2122
    //   1971: aload 16
    //   1973: iload 5
    //   1975: invokevirtual 241	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   1978: checkcast 630	android/os/Parcelable
    //   1981: astore 18
    //   1983: aload 18
    //   1985: instanceof 364
    //   1988: ifne +3817 -> 5805
    //   1991: aload 18
    //   1993: invokevirtual 592	java/lang/Object:toString	()Ljava/lang/String;
    //   1996: invokestatic 596	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   1999: astore 18
    //   2001: aload 18
    //   2003: checkcast 364	android/net/Uri
    //   2006: invokestatic 612	org/vidogram/messenger/AndroidUtilities:getPath	(Landroid/net/Uri;)Ljava/lang/String;
    //   2009: astore 17
    //   2011: aload 18
    //   2013: invokevirtual 592	java/lang/Object:toString	()Ljava/lang/String;
    //   2016: astore 19
    //   2018: aload 19
    //   2020: astore 18
    //   2022: aload 19
    //   2024: ifnonnull +7 -> 2031
    //   2027: aload 17
    //   2029: astore 18
    //   2031: aload 17
    //   2033: ifnull +80 -> 2113
    //   2036: aload 17
    //   2038: astore 19
    //   2040: aload 17
    //   2042: ldc_w 614
    //   2045: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   2048: ifeq +16 -> 2064
    //   2051: aload 17
    //   2053: ldc_w 616
    //   2056: ldc_w 558
    //   2059: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   2062: astore 19
    //   2064: aload_0
    //   2065: getfield 329	org/vidogram/ui/LaunchActivity:documentsPathsArray	Ljava/util/ArrayList;
    //   2068: ifnonnull +25 -> 2093
    //   2071: aload_0
    //   2072: new 118	java/util/ArrayList
    //   2075: dup
    //   2076: invokespecial 121	java/util/ArrayList:<init>	()V
    //   2079: putfield 329	org/vidogram/ui/LaunchActivity:documentsPathsArray	Ljava/util/ArrayList;
    //   2082: aload_0
    //   2083: new 118	java/util/ArrayList
    //   2086: dup
    //   2087: invokespecial 121	java/util/ArrayList:<init>	()V
    //   2090: putfield 331	org/vidogram/ui/LaunchActivity:documentsOriginalPathsArray	Ljava/util/ArrayList;
    //   2093: aload_0
    //   2094: getfield 329	org/vidogram/ui/LaunchActivity:documentsPathsArray	Ljava/util/ArrayList;
    //   2097: aload 19
    //   2099: invokevirtual 259	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   2102: pop
    //   2103: aload_0
    //   2104: getfield 331	org/vidogram/ui/LaunchActivity:documentsOriginalPathsArray	Ljava/util/ArrayList;
    //   2107: aload 18
    //   2109: invokevirtual 259	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   2112: pop
    //   2113: iload 5
    //   2115: iconst_1
    //   2116: iadd
    //   2117: istore 5
    //   2119: goto -158 -> 1961
    //   2122: iconst_0
    //   2123: istore 5
    //   2125: goto -201 -> 1924
    //   2128: iconst_1
    //   2129: istore 5
    //   2131: goto -207 -> 1924
    //   2134: astore 16
    //   2136: aload 16
    //   2138: invokestatic 473	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2141: iconst_1
    //   2142: istore 5
    //   2144: goto -220 -> 1924
    //   2147: ldc_w 632
    //   2150: aload_1
    //   2151: invokevirtual 345	android/content/Intent:getAction	()Ljava/lang/String;
    //   2154: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2157: ifeq +1903 -> 4060
    //   2160: aload_1
    //   2161: invokevirtual 636	android/content/Intent:getData	()Landroid/net/Uri;
    //   2164: astore 21
    //   2166: aload 21
    //   2168: ifnull +3622 -> 5790
    //   2171: aconst_null
    //   2172: astore 25
    //   2174: iconst_0
    //   2175: istore 9
    //   2177: iconst_0
    //   2178: istore 10
    //   2180: iconst_0
    //   2181: istore_3
    //   2182: aload 21
    //   2184: invokevirtual 639	android/net/Uri:getScheme	()Ljava/lang/String;
    //   2187: astore 16
    //   2189: aload 14
    //   2191: astore 19
    //   2193: aload 13
    //   2195: astore 18
    //   2197: aload 16
    //   2199: ifnull +3357 -> 5556
    //   2202: aload 16
    //   2204: ldc_w 641
    //   2207: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2210: ifne +14 -> 2224
    //   2213: aload 16
    //   2215: ldc_w 643
    //   2218: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2221: ifeq +748 -> 2969
    //   2224: aload 21
    //   2226: invokevirtual 646	android/net/Uri:getHost	()Ljava/lang/String;
    //   2229: invokevirtual 606	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   2232: astore 16
    //   2234: aload 16
    //   2236: ldc_w 648
    //   2239: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2242: ifne +25 -> 2267
    //   2245: aload 16
    //   2247: ldc_w 650
    //   2250: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2253: ifne +14 -> 2267
    //   2256: aload 16
    //   2258: ldc_w 652
    //   2261: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2264: ifeq +3451 -> 5715
    //   2267: aload 21
    //   2269: invokevirtual 654	android/net/Uri:getPath	()Ljava/lang/String;
    //   2272: astore 16
    //   2274: aload 16
    //   2276: ifnull +3439 -> 5715
    //   2279: aload 16
    //   2281: invokevirtual 445	java/lang/String:length	()I
    //   2284: iconst_1
    //   2285: if_icmple +3430 -> 5715
    //   2288: aload 16
    //   2290: iconst_1
    //   2291: invokevirtual 657	java/lang/String:substring	(I)Ljava/lang/String;
    //   2294: astore 16
    //   2296: aload 16
    //   2298: ldc_w 659
    //   2301: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   2304: ifeq +237 -> 2541
    //   2307: aload 16
    //   2309: ldc_w 659
    //   2312: ldc_w 558
    //   2315: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   2318: astore 23
    //   2320: aconst_null
    //   2321: astore 16
    //   2323: aconst_null
    //   2324: astore 20
    //   2326: aconst_null
    //   2327: astore 17
    //   2329: aconst_null
    //   2330: astore 19
    //   2332: aconst_null
    //   2333: astore 21
    //   2335: aconst_null
    //   2336: astore 24
    //   2338: aconst_null
    //   2339: astore 18
    //   2341: aconst_null
    //   2342: astore 22
    //   2344: aload 15
    //   2346: astore 32
    //   2348: aload 22
    //   2350: astore 30
    //   2352: aload 23
    //   2354: astore 29
    //   2356: aload 24
    //   2358: astore 31
    //   2360: aload 18
    //   2362: astore 22
    //   2364: aload 25
    //   2366: astore 23
    //   2368: aload 21
    //   2370: astore 18
    //   2372: aload 14
    //   2374: astore 15
    //   2376: aload 32
    //   2378: astore 14
    //   2380: aload 22
    //   2382: astore 25
    //   2384: aload 16
    //   2386: astore 24
    //   2388: aload 19
    //   2390: astore 16
    //   2392: aload 20
    //   2394: astore 22
    //   2396: aload 30
    //   2398: astore 21
    //   2400: aload 18
    //   2402: astore 20
    //   2404: aload 29
    //   2406: astore 19
    //   2408: aload 31
    //   2410: astore 18
    //   2412: aload 16
    //   2414: astore 29
    //   2416: aload 16
    //   2418: ifnull +41 -> 2459
    //   2421: aload 16
    //   2423: astore 29
    //   2425: aload 16
    //   2427: ldc_w 661
    //   2430: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   2433: ifeq +26 -> 2459
    //   2436: new 462	java/lang/StringBuilder
    //   2439: dup
    //   2440: invokespecial 463	java/lang/StringBuilder:<init>	()V
    //   2443: ldc_w 663
    //   2446: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2449: aload 16
    //   2451: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2454: invokevirtual 470	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2457: astore 29
    //   2459: aload 25
    //   2461: ifnonnull +8 -> 2469
    //   2464: aload 17
    //   2466: ifnull +1453 -> 3919
    //   2469: new 317	android/os/Bundle
    //   2472: dup
    //   2473: invokespecial 488	android/os/Bundle:<init>	()V
    //   2476: astore 16
    //   2478: aload 16
    //   2480: ldc_w 664
    //   2483: aload 25
    //   2485: invokevirtual 668	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   2488: aload 16
    //   2490: ldc_w 670
    //   2493: aload 17
    //   2495: invokevirtual 668	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   2498: new 48	org/vidogram/ui/LaunchActivity$7
    //   2501: dup
    //   2502: aload_0
    //   2503: aload 16
    //   2505: invokespecial 673	org/vidogram/ui/LaunchActivity$7:<init>	(Lorg/vidogram/ui/LaunchActivity;Landroid/os/Bundle;)V
    //   2508: invokestatic 677	org/vidogram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
    //   2511: aload 27
    //   2513: astore 17
    //   2515: aload 15
    //   2517: astore 18
    //   2519: iconst_0
    //   2520: istore 5
    //   2522: aload 26
    //   2524: astore 16
    //   2526: aload 14
    //   2528: astore 15
    //   2530: aload 18
    //   2532: astore 14
    //   2534: iload 7
    //   2536: istore 6
    //   2538: goto -1781 -> 757
    //   2541: aload 16
    //   2543: ldc_w 679
    //   2546: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   2549: ifeq +43 -> 2592
    //   2552: aload 16
    //   2554: ldc_w 679
    //   2557: ldc_w 558
    //   2560: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   2563: astore 21
    //   2565: aconst_null
    //   2566: astore 16
    //   2568: aconst_null
    //   2569: astore 20
    //   2571: aconst_null
    //   2572: astore 23
    //   2574: aconst_null
    //   2575: astore 17
    //   2577: aconst_null
    //   2578: astore 19
    //   2580: aconst_null
    //   2581: astore 24
    //   2583: aconst_null
    //   2584: astore 18
    //   2586: aconst_null
    //   2587: astore 22
    //   2589: goto -245 -> 2344
    //   2592: aload 16
    //   2594: ldc_w 681
    //   2597: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   2600: ifne +14 -> 2614
    //   2603: aload 16
    //   2605: ldc_w 683
    //   2608: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   2611: ifeq +154 -> 2765
    //   2614: aload 21
    //   2616: ldc_w 685
    //   2619: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   2622: astore 17
    //   2624: aload 17
    //   2626: astore 16
    //   2628: aload 17
    //   2630: ifnonnull +8 -> 2638
    //   2633: ldc_w 558
    //   2636: astore 16
    //   2638: aload 21
    //   2640: ldc_w 690
    //   2643: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   2646: ifnull +3135 -> 5781
    //   2649: aload 16
    //   2651: invokevirtual 445	java/lang/String:length	()I
    //   2654: ifle +3122 -> 5776
    //   2657: new 462	java/lang/StringBuilder
    //   2660: dup
    //   2661: invokespecial 463	java/lang/StringBuilder:<init>	()V
    //   2664: aload 16
    //   2666: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2669: ldc_w 585
    //   2672: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2675: invokevirtual 470	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2678: astore 16
    //   2680: iconst_1
    //   2681: istore_3
    //   2682: new 462	java/lang/StringBuilder
    //   2685: dup
    //   2686: invokespecial 463	java/lang/StringBuilder:<init>	()V
    //   2689: aload 16
    //   2691: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2694: aload 21
    //   2696: ldc_w 690
    //   2699: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   2702: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2705: invokevirtual 470	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2708: astore 17
    //   2710: aload 17
    //   2712: astore 16
    //   2714: aload 17
    //   2716: invokevirtual 445	java/lang/String:length	()I
    //   2719: sipush 16384
    //   2722: if_icmple +14 -> 2736
    //   2725: aload 17
    //   2727: iconst_0
    //   2728: sipush 16384
    //   2731: invokevirtual 449	java/lang/String:substring	(II)Ljava/lang/String;
    //   2734: astore 16
    //   2736: aload 16
    //   2738: ldc_w 585
    //   2741: invokevirtual 442	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   2744: ifeq +3001 -> 5745
    //   2747: aload 16
    //   2749: iconst_0
    //   2750: aload 16
    //   2752: invokevirtual 445	java/lang/String:length	()I
    //   2755: iconst_1
    //   2756: isub
    //   2757: invokevirtual 449	java/lang/String:substring	(II)Ljava/lang/String;
    //   2760: astore 16
    //   2762: goto -26 -> 2736
    //   2765: aload 16
    //   2767: ldc_w 692
    //   2770: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   2773: ifeq +47 -> 2820
    //   2776: aload 21
    //   2778: ldc_w 664
    //   2781: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   2784: astore 18
    //   2786: aload 21
    //   2788: ldc_w 670
    //   2791: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   2794: astore 17
    //   2796: aconst_null
    //   2797: astore 19
    //   2799: aconst_null
    //   2800: astore 22
    //   2802: aconst_null
    //   2803: astore 21
    //   2805: aconst_null
    //   2806: astore 24
    //   2808: aconst_null
    //   2809: astore 16
    //   2811: aconst_null
    //   2812: astore 20
    //   2814: aconst_null
    //   2815: astore 23
    //   2817: goto -473 -> 2344
    //   2820: aload 16
    //   2822: invokevirtual 445	java/lang/String:length	()I
    //   2825: iconst_1
    //   2826: if_icmplt +2889 -> 5715
    //   2829: aload 21
    //   2831: invokevirtual 696	android/net/Uri:getPathSegments	()Ljava/util/List;
    //   2834: astore 17
    //   2836: aload 17
    //   2838: invokeinterface 699 1 0
    //   2843: ifle +2863 -> 5706
    //   2846: aload 17
    //   2848: iconst_0
    //   2849: invokeinterface 700 2 0
    //   2854: checkcast 349	java/lang/String
    //   2857: astore 16
    //   2859: aload 17
    //   2861: invokeinterface 699 1 0
    //   2866: iconst_1
    //   2867: if_icmple +2829 -> 5696
    //   2870: aload 17
    //   2872: iconst_1
    //   2873: invokeinterface 700 2 0
    //   2878: checkcast 349	java/lang/String
    //   2881: invokestatic 706	org/vidogram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   2884: astore 18
    //   2886: aload 18
    //   2888: invokevirtual 487	java/lang/Integer:intValue	()I
    //   2891: ifne +2794 -> 5685
    //   2894: aconst_null
    //   2895: astore 18
    //   2897: aload 16
    //   2899: astore 17
    //   2901: aload 18
    //   2903: astore 16
    //   2905: aload 21
    //   2907: ldc_w 708
    //   2910: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   2913: astore 22
    //   2915: aload 21
    //   2917: ldc_w 710
    //   2920: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   2923: astore 20
    //   2925: aload 21
    //   2927: ldc_w 712
    //   2930: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   2933: astore 18
    //   2935: aconst_null
    //   2936: astore 23
    //   2938: aload 17
    //   2940: astore 24
    //   2942: aload 16
    //   2944: astore 25
    //   2946: aconst_null
    //   2947: astore 17
    //   2949: aconst_null
    //   2950: astore 19
    //   2952: aconst_null
    //   2953: astore 21
    //   2955: aconst_null
    //   2956: astore 29
    //   2958: aload 18
    //   2960: astore 16
    //   2962: aload 29
    //   2964: astore 18
    //   2966: goto -622 -> 2344
    //   2969: aload 14
    //   2971: astore 19
    //   2973: aload 13
    //   2975: astore 18
    //   2977: aload 16
    //   2979: ldc_w 714
    //   2982: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2985: ifeq +2571 -> 5556
    //   2988: aload 21
    //   2990: invokevirtual 603	android/net/Uri:toString	()Ljava/lang/String;
    //   2993: astore 16
    //   2995: aload 16
    //   2997: ldc_w 716
    //   3000: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3003: ifne +14 -> 3017
    //   3006: aload 16
    //   3008: ldc_w 718
    //   3011: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3014: ifeq +124 -> 3138
    //   3017: aload 16
    //   3019: ldc_w 716
    //   3022: ldc_w 720
    //   3025: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   3028: ldc_w 718
    //   3031: ldc_w 720
    //   3034: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   3037: invokestatic 596	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   3040: astore 16
    //   3042: aload 16
    //   3044: ldc_w 722
    //   3047: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3050: astore 18
    //   3052: aload 16
    //   3054: ldc_w 708
    //   3057: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3060: astore 21
    //   3062: aload 16
    //   3064: ldc_w 710
    //   3067: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3070: astore 22
    //   3072: aload 16
    //   3074: ldc_w 712
    //   3077: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3080: astore 24
    //   3082: aload 16
    //   3084: ldc_w 724
    //   3087: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3090: invokestatic 706	org/vidogram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   3093: astore 23
    //   3095: aload 23
    //   3097: invokevirtual 487	java/lang/Integer:intValue	()I
    //   3100: ifne +2553 -> 5653
    //   3103: aconst_null
    //   3104: astore 17
    //   3106: aconst_null
    //   3107: astore 25
    //   3109: iconst_0
    //   3110: istore_3
    //   3111: aconst_null
    //   3112: astore 16
    //   3114: aconst_null
    //   3115: astore 19
    //   3117: aconst_null
    //   3118: astore 23
    //   3120: aconst_null
    //   3121: astore 20
    //   3123: aload 14
    //   3125: astore 29
    //   3127: aload 15
    //   3129: astore 14
    //   3131: aload 29
    //   3133: astore 15
    //   3135: goto -723 -> 2412
    //   3138: aload 16
    //   3140: ldc_w 726
    //   3143: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3146: ifne +14 -> 3160
    //   3149: aload 16
    //   3151: ldc_w 728
    //   3154: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3157: ifeq +78 -> 3235
    //   3160: aload 16
    //   3162: ldc_w 726
    //   3165: ldc_w 720
    //   3168: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   3171: ldc_w 728
    //   3174: ldc_w 720
    //   3177: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   3180: invokestatic 596	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   3183: ldc_w 730
    //   3186: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3189: astore 19
    //   3191: aconst_null
    //   3192: astore 17
    //   3194: aconst_null
    //   3195: astore 18
    //   3197: aconst_null
    //   3198: astore 22
    //   3200: aconst_null
    //   3201: astore 21
    //   3203: aconst_null
    //   3204: astore 25
    //   3206: aconst_null
    //   3207: astore 24
    //   3209: iconst_0
    //   3210: istore_3
    //   3211: aconst_null
    //   3212: astore 16
    //   3214: aconst_null
    //   3215: astore 23
    //   3217: aconst_null
    //   3218: astore 20
    //   3220: aload 14
    //   3222: astore 29
    //   3224: aload 15
    //   3226: astore 14
    //   3228: aload 29
    //   3230: astore 15
    //   3232: goto -820 -> 2412
    //   3235: aload 16
    //   3237: ldc_w 732
    //   3240: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3243: ifne +14 -> 3257
    //   3246: aload 16
    //   3248: ldc_w 734
    //   3251: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3254: ifeq +78 -> 3332
    //   3257: aload 16
    //   3259: ldc_w 732
    //   3262: ldc_w 720
    //   3265: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   3268: ldc_w 734
    //   3271: ldc_w 720
    //   3274: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   3277: invokestatic 596	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   3280: ldc_w 736
    //   3283: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3286: astore 20
    //   3288: aconst_null
    //   3289: astore 17
    //   3291: aconst_null
    //   3292: astore 18
    //   3294: aconst_null
    //   3295: astore 22
    //   3297: aconst_null
    //   3298: astore 21
    //   3300: aconst_null
    //   3301: astore 25
    //   3303: aconst_null
    //   3304: astore 24
    //   3306: iconst_0
    //   3307: istore_3
    //   3308: aconst_null
    //   3309: astore 16
    //   3311: aconst_null
    //   3312: astore 19
    //   3314: aconst_null
    //   3315: astore 23
    //   3317: aload 14
    //   3319: astore 29
    //   3321: aload 15
    //   3323: astore 14
    //   3325: aload 29
    //   3327: astore 15
    //   3329: goto -917 -> 2412
    //   3332: aload 16
    //   3334: ldc_w 738
    //   3337: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3340: ifne +36 -> 3376
    //   3343: aload 16
    //   3345: ldc_w 740
    //   3348: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3351: ifne +25 -> 3376
    //   3354: aload 16
    //   3356: ldc_w 742
    //   3359: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3362: ifne +14 -> 3376
    //   3365: aload 16
    //   3367: ldc_w 744
    //   3370: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3373: ifeq +211 -> 3584
    //   3376: aload 16
    //   3378: ldc_w 738
    //   3381: ldc_w 720
    //   3384: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   3387: ldc_w 740
    //   3390: ldc_w 720
    //   3393: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   3396: ldc_w 742
    //   3399: ldc_w 720
    //   3402: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   3405: ldc_w 744
    //   3408: ldc_w 720
    //   3411: invokevirtual 620	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   3414: invokestatic 596	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   3417: astore 18
    //   3419: aload 18
    //   3421: ldc_w 685
    //   3424: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3427: astore 17
    //   3429: aload 17
    //   3431: astore 16
    //   3433: aload 17
    //   3435: ifnonnull +8 -> 3443
    //   3438: ldc_w 558
    //   3441: astore 16
    //   3443: aload 16
    //   3445: astore 17
    //   3447: iload 10
    //   3449: istore_3
    //   3450: aload 18
    //   3452: ldc_w 690
    //   3455: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3458: ifnull +71 -> 3529
    //   3461: aload 16
    //   3463: astore 17
    //   3465: iload 9
    //   3467: istore_3
    //   3468: aload 16
    //   3470: invokevirtual 445	java/lang/String:length	()I
    //   3473: ifle +28 -> 3501
    //   3476: iconst_1
    //   3477: istore_3
    //   3478: new 462	java/lang/StringBuilder
    //   3481: dup
    //   3482: invokespecial 463	java/lang/StringBuilder:<init>	()V
    //   3485: aload 16
    //   3487: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3490: ldc_w 585
    //   3493: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3496: invokevirtual 470	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3499: astore 17
    //   3501: new 462	java/lang/StringBuilder
    //   3504: dup
    //   3505: invokespecial 463	java/lang/StringBuilder:<init>	()V
    //   3508: aload 17
    //   3510: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3513: aload 18
    //   3515: ldc_w 690
    //   3518: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3521: invokevirtual 467	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3524: invokevirtual 470	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3527: astore 17
    //   3529: aload 17
    //   3531: astore 16
    //   3533: aload 17
    //   3535: invokevirtual 445	java/lang/String:length	()I
    //   3538: sipush 16384
    //   3541: if_icmple +14 -> 3555
    //   3544: aload 17
    //   3546: iconst_0
    //   3547: sipush 16384
    //   3550: invokevirtual 449	java/lang/String:substring	(II)Ljava/lang/String;
    //   3553: astore 16
    //   3555: aload 16
    //   3557: ldc_w 585
    //   3560: invokevirtual 442	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   3563: ifeq +2048 -> 5611
    //   3566: aload 16
    //   3568: iconst_0
    //   3569: aload 16
    //   3571: invokevirtual 445	java/lang/String:length	()I
    //   3574: iconst_1
    //   3575: isub
    //   3576: invokevirtual 449	java/lang/String:substring	(II)Ljava/lang/String;
    //   3579: astore 16
    //   3581: goto -26 -> 3555
    //   3584: aload 16
    //   3586: ldc_w 746
    //   3589: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3592: ifne +14 -> 3606
    //   3595: aload 16
    //   3597: ldc_w 748
    //   3600: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3603: ifeq +64 -> 3667
    //   3606: aload 21
    //   3608: ldc_w 664
    //   3611: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3614: astore 25
    //   3616: aload 21
    //   3618: ldc_w 670
    //   3621: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3624: astore 17
    //   3626: aconst_null
    //   3627: astore 18
    //   3629: aconst_null
    //   3630: astore 22
    //   3632: aconst_null
    //   3633: astore 21
    //   3635: aconst_null
    //   3636: astore 24
    //   3638: iconst_0
    //   3639: istore_3
    //   3640: aconst_null
    //   3641: astore 16
    //   3643: aconst_null
    //   3644: astore 19
    //   3646: aconst_null
    //   3647: astore 23
    //   3649: aconst_null
    //   3650: astore 20
    //   3652: aload 14
    //   3654: astore 29
    //   3656: aload 15
    //   3658: astore 14
    //   3660: aload 29
    //   3662: astore 15
    //   3664: goto -1252 -> 2412
    //   3667: aload 16
    //   3669: ldc_w 750
    //   3672: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3675: ifne +22 -> 3697
    //   3678: aload 14
    //   3680: astore 19
    //   3682: aload 13
    //   3684: astore 18
    //   3686: aload 16
    //   3688: ldc_w 752
    //   3691: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   3694: ifeq +1862 -> 5556
    //   3697: aload 21
    //   3699: ldc_w 490
    //   3702: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3705: astore 16
    //   3707: aload 21
    //   3709: ldc_w 754
    //   3712: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3715: astore 18
    //   3717: aload 21
    //   3719: ldc_w 496
    //   3722: invokevirtual 688	android/net/Uri:getQueryParameter	(Ljava/lang/String;)Ljava/lang/String;
    //   3725: astore 20
    //   3727: aload 16
    //   3729: ifnull +99 -> 3828
    //   3732: aload 16
    //   3734: invokestatic 757	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   3737: istore 5
    //   3739: iload 5
    //   3741: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3744: astore 16
    //   3746: aload 14
    //   3748: astore 17
    //   3750: aload 17
    //   3752: astore 19
    //   3754: aload 16
    //   3756: astore 18
    //   3758: aload 20
    //   3760: ifnull +1796 -> 5556
    //   3763: aload 20
    //   3765: invokestatic 757	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   3768: istore 5
    //   3770: aconst_null
    //   3771: astore 30
    //   3773: aconst_null
    //   3774: astore 18
    //   3776: aconst_null
    //   3777: astore 22
    //   3779: aconst_null
    //   3780: astore 21
    //   3782: aconst_null
    //   3783: astore 25
    //   3785: aconst_null
    //   3786: astore 24
    //   3788: iconst_0
    //   3789: istore_3
    //   3790: aconst_null
    //   3791: astore 29
    //   3793: aconst_null
    //   3794: astore 19
    //   3796: aconst_null
    //   3797: astore 23
    //   3799: aconst_null
    //   3800: astore 20
    //   3802: iload 5
    //   3804: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3807: astore 14
    //   3809: aload 17
    //   3811: astore 15
    //   3813: aload 16
    //   3815: astore 13
    //   3817: aload 30
    //   3819: astore 17
    //   3821: aload 29
    //   3823: astore 16
    //   3825: goto -1413 -> 2412
    //   3828: aload 14
    //   3830: astore 17
    //   3832: aload 13
    //   3834: astore 16
    //   3836: aload 18
    //   3838: ifnull -88 -> 3750
    //   3841: aload 18
    //   3843: invokestatic 757	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   3846: istore 5
    //   3848: iload 5
    //   3850: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3853: astore 17
    //   3855: aload 13
    //   3857: astore 16
    //   3859: goto -109 -> 3750
    //   3862: astore 13
    //   3864: aconst_null
    //   3865: astore 30
    //   3867: aconst_null
    //   3868: astore 18
    //   3870: aconst_null
    //   3871: astore 22
    //   3873: aconst_null
    //   3874: astore 21
    //   3876: aconst_null
    //   3877: astore 25
    //   3879: aconst_null
    //   3880: astore 24
    //   3882: iconst_0
    //   3883: istore_3
    //   3884: aconst_null
    //   3885: astore 29
    //   3887: aconst_null
    //   3888: astore 19
    //   3890: aconst_null
    //   3891: astore 23
    //   3893: aconst_null
    //   3894: astore 20
    //   3896: aload 15
    //   3898: astore 14
    //   3900: aload 17
    //   3902: astore 15
    //   3904: aload 16
    //   3906: astore 13
    //   3908: aload 30
    //   3910: astore 17
    //   3912: aload 29
    //   3914: astore 16
    //   3916: goto -1504 -> 2412
    //   3919: aload 18
    //   3921: ifnonnull +23 -> 3944
    //   3924: aload 19
    //   3926: ifnonnull +18 -> 3944
    //   3929: aload 20
    //   3931: ifnonnull +13 -> 3944
    //   3934: aload 29
    //   3936: ifnonnull +8 -> 3944
    //   3939: aload 24
    //   3941: ifnull +28 -> 3969
    //   3944: aload_0
    //   3945: aload 18
    //   3947: aload 19
    //   3949: aload 20
    //   3951: aload 21
    //   3953: aload 22
    //   3955: aload 29
    //   3957: iload_3
    //   3958: aload 23
    //   3960: aload 24
    //   3962: iconst_0
    //   3963: invokespecial 155	org/vidogram/ui/LaunchActivity:runLinkRequest	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/Integer;Ljava/lang/String;I)V
    //   3966: goto -1455 -> 2511
    //   3969: aload_0
    //   3970: invokevirtual 368	org/vidogram/ui/LaunchActivity:getContentResolver	()Landroid/content/ContentResolver;
    //   3973: aload_1
    //   3974: invokevirtual 636	android/content/Intent:getData	()Landroid/net/Uri;
    //   3977: aconst_null
    //   3978: aconst_null
    //   3979: aconst_null
    //   3980: aconst_null
    //   3981: invokevirtual 761	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   3984: astore 16
    //   3986: aload 16
    //   3988: ifnull +1565 -> 5553
    //   3991: aload 16
    //   3993: invokeinterface 766 1 0
    //   3998: ifeq +1552 -> 5550
    //   4001: aload 16
    //   4003: aload 16
    //   4005: ldc_w 768
    //   4008: invokeinterface 771 2 0
    //   4013: invokeinterface 775 2 0
    //   4018: istore 5
    //   4020: invokestatic 781	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   4023: getstatic 784	org/vidogram/messenger/NotificationCenter:closeChats	I
    //   4026: iconst_0
    //   4027: anewarray 591	java/lang/Object
    //   4030: invokevirtual 788	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   4033: iload 5
    //   4035: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4038: astore 13
    //   4040: aload 16
    //   4042: invokeinterface 789 1 0
    //   4047: goto -1536 -> 2511
    //   4050: astore 16
    //   4052: aload 16
    //   4054: invokestatic 473	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   4057: goto -1546 -> 2511
    //   4060: aload_1
    //   4061: invokevirtual 345	android/content/Intent:getAction	()Ljava/lang/String;
    //   4064: ldc_w 791
    //   4067: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   4070: ifeq +23 -> 4093
    //   4073: aload 26
    //   4075: astore 16
    //   4077: iconst_1
    //   4078: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4081: astore 17
    //   4083: iconst_0
    //   4084: istore 5
    //   4086: iload 7
    //   4088: istore 6
    //   4090: goto -3333 -> 757
    //   4093: aload_1
    //   4094: invokevirtual 345	android/content/Intent:getAction	()Ljava/lang/String;
    //   4097: ldc_w 793
    //   4100: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   4103: ifeq +23 -> 4126
    //   4106: iconst_1
    //   4107: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4110: astore 16
    //   4112: aload 27
    //   4114: astore 17
    //   4116: iconst_0
    //   4117: istore 5
    //   4119: iload 7
    //   4121: istore 6
    //   4123: goto -3366 -> 757
    //   4126: aload_1
    //   4127: invokevirtual 345	android/content/Intent:getAction	()Ljava/lang/String;
    //   4130: ldc_w 795
    //   4133: invokevirtual 416	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   4136: ifeq +163 -> 4299
    //   4139: aload_1
    //   4140: ldc_w 797
    //   4143: iconst_0
    //   4144: invokevirtual 801	android/content/Intent:getIntExtra	(Ljava/lang/String;I)I
    //   4147: istore 5
    //   4149: aload_1
    //   4150: ldc_w 803
    //   4153: iconst_0
    //   4154: invokevirtual 801	android/content/Intent:getIntExtra	(Ljava/lang/String;I)I
    //   4157: istore 6
    //   4159: aload_1
    //   4160: ldc_w 805
    //   4163: iconst_0
    //   4164: invokevirtual 801	android/content/Intent:getIntExtra	(Ljava/lang/String;I)I
    //   4167: istore 7
    //   4169: iload 5
    //   4171: ifeq +52 -> 4223
    //   4174: invokestatic 781	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   4177: getstatic 784	org/vidogram/messenger/NotificationCenter:closeChats	I
    //   4180: iconst_0
    //   4181: anewarray 591	java/lang/Object
    //   4184: invokevirtual 788	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   4187: iload 5
    //   4189: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4192: astore 14
    //   4194: iconst_0
    //   4195: istore 5
    //   4197: aload 28
    //   4199: astore 16
    //   4201: iload 5
    //   4203: istore 6
    //   4205: aload 27
    //   4207: astore 17
    //   4209: aload 16
    //   4211: astore 28
    //   4213: iconst_0
    //   4214: istore 5
    //   4216: aload 26
    //   4218: astore 16
    //   4220: goto -3463 -> 757
    //   4223: iload 6
    //   4225: ifeq +33 -> 4258
    //   4228: invokestatic 781	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   4231: getstatic 784	org/vidogram/messenger/NotificationCenter:closeChats	I
    //   4234: iconst_0
    //   4235: anewarray 591	java/lang/Object
    //   4238: invokevirtual 788	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   4241: iload 6
    //   4243: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4246: astore 13
    //   4248: iconst_0
    //   4249: istore 5
    //   4251: aload 28
    //   4253: astore 16
    //   4255: goto -54 -> 4201
    //   4258: iload 7
    //   4260: ifeq +29 -> 4289
    //   4263: invokestatic 781	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   4266: getstatic 784	org/vidogram/messenger/NotificationCenter:closeChats	I
    //   4269: iconst_0
    //   4270: anewarray 591	java/lang/Object
    //   4273: invokevirtual 788	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   4276: iload 7
    //   4278: invokestatic 309	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4281: astore 16
    //   4283: iconst_0
    //   4284: istore 5
    //   4286: goto -85 -> 4201
    //   4289: iconst_1
    //   4290: istore 5
    //   4292: aload 28
    //   4294: astore 16
    //   4296: goto -95 -> 4201
    //   4299: aload_1
    //   4300: invokevirtual 345	android/content/Intent:getAction	()Ljava/lang/String;
    //   4303: ldc_w 807
    //   4306: invokevirtual 352	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   4309: ifeq +1223 -> 5532
    //   4312: iconst_1
    //   4313: istore 5
    //   4315: aload 26
    //   4317: astore 16
    //   4319: aload 27
    //   4321: astore 17
    //   4323: iload 7
    //   4325: istore 6
    //   4327: goto -3570 -> 757
    //   4330: aload 14
    //   4332: invokevirtual 487	java/lang/Integer:intValue	()I
    //   4335: ifeq +117 -> 4452
    //   4338: new 317	android/os/Bundle
    //   4341: dup
    //   4342: invokespecial 488	android/os/Bundle:<init>	()V
    //   4345: astore 13
    //   4347: aload 13
    //   4349: ldc_w 754
    //   4352: aload 14
    //   4354: invokevirtual 487	java/lang/Integer:intValue	()I
    //   4357: invokevirtual 494	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   4360: aload 15
    //   4362: invokevirtual 487	java/lang/Integer:intValue	()I
    //   4365: ifeq +16 -> 4381
    //   4368: aload 13
    //   4370: ldc_w 496
    //   4373: aload 15
    //   4375: invokevirtual 487	java/lang/Integer:intValue	()I
    //   4378: invokevirtual 494	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   4381: getstatic 123	org/vidogram/ui/LaunchActivity:mainFragmentsStack	Ljava/util/ArrayList;
    //   4384: invokevirtual 272	java/util/ArrayList:isEmpty	()Z
    //   4387: ifne +31 -> 4418
    //   4390: iload 4
    //   4392: istore_3
    //   4393: aload 13
    //   4395: getstatic 123	org/vidogram/ui/LaunchActivity:mainFragmentsStack	Ljava/util/ArrayList;
    //   4398: getstatic 123	org/vidogram/ui/LaunchActivity:mainFragmentsStack	Ljava/util/ArrayList;
    //   4401: invokevirtual 237	java/util/ArrayList:size	()I
    //   4404: iconst_1
    //   4405: isub
    //   4406: invokevirtual 241	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4409: checkcast 243	org/vidogram/ui/ActionBar/BaseFragment
    //   4412: invokestatic 502	org/vidogram/messenger/MessagesController:checkCanOpenChat	(Landroid/os/Bundle;Lorg/vidogram/ui/ActionBar/BaseFragment;)Z
    //   4415: ifeq +34 -> 4449
    //   4418: new 245	org/vidogram/ui/ChatActivity
    //   4421: dup
    //   4422: aload 13
    //   4424: invokespecial 505	org/vidogram/ui/ChatActivity:<init>	(Landroid/os/Bundle;)V
    //   4427: astore 13
    //   4429: iload 4
    //   4431: istore_3
    //   4432: aload_0
    //   4433: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4436: aload 13
    //   4438: iconst_0
    //   4439: iconst_1
    //   4440: iconst_1
    //   4441: invokevirtual 509	org/vidogram/ui/ActionBar/ActionBarLayout:presentFragment	(Lorg/vidogram/ui/ActionBar/BaseFragment;ZZZ)Z
    //   4444: ifeq +5 -> 4449
    //   4447: iconst_1
    //   4448: istore_3
    //   4449: goto -3579 -> 870
    //   4452: aload 28
    //   4454: invokevirtual 487	java/lang/Integer:intValue	()I
    //   4457: ifeq +59 -> 4516
    //   4460: new 317	android/os/Bundle
    //   4463: dup
    //   4464: invokespecial 488	android/os/Bundle:<init>	()V
    //   4467: astore 13
    //   4469: aload 13
    //   4471: ldc_w 809
    //   4474: aload 28
    //   4476: invokevirtual 487	java/lang/Integer:intValue	()I
    //   4479: invokevirtual 494	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   4482: new 245	org/vidogram/ui/ChatActivity
    //   4485: dup
    //   4486: aload 13
    //   4488: invokespecial 505	org/vidogram/ui/ChatActivity:<init>	(Landroid/os/Bundle;)V
    //   4491: astore 13
    //   4493: iload 8
    //   4495: istore_3
    //   4496: aload_0
    //   4497: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4500: aload 13
    //   4502: iconst_0
    //   4503: iconst_1
    //   4504: iconst_1
    //   4505: invokevirtual 509	org/vidogram/ui/ActionBar/ActionBarLayout:presentFragment	(Lorg/vidogram/ui/ActionBar/BaseFragment;ZZZ)Z
    //   4508: ifeq +5 -> 4513
    //   4511: iconst_1
    //   4512: istore_3
    //   4513: goto -3643 -> 870
    //   4516: iload 6
    //   4518: ifeq +86 -> 4604
    //   4521: invokestatic 207	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   4524: ifne +17 -> 4541
    //   4527: aload_0
    //   4528: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4531: invokevirtual 812	org/vidogram/ui/ActionBar/ActionBarLayout:removeAllFragments	()V
    //   4534: iconst_0
    //   4535: istore_2
    //   4536: iconst_0
    //   4537: istore_3
    //   4538: goto -3668 -> 870
    //   4541: aload_0
    //   4542: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4545: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   4548: invokevirtual 272	java/util/ArrayList:isEmpty	()Z
    //   4551: ifne -17 -> 4534
    //   4554: aload_0
    //   4555: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4558: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   4561: invokevirtual 237	java/util/ArrayList:size	()I
    //   4564: iconst_1
    //   4565: isub
    //   4566: ifle +27 -> 4593
    //   4569: aload_0
    //   4570: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4573: aload_0
    //   4574: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4577: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   4580: iconst_0
    //   4581: invokevirtual 241	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4584: checkcast 243	org/vidogram/ui/ActionBar/BaseFragment
    //   4587: invokevirtual 816	org/vidogram/ui/ActionBar/ActionBarLayout:removeFragmentFromStack	(Lorg/vidogram/ui/ActionBar/BaseFragment;)V
    //   4590: goto -36 -> 4554
    //   4593: aload_0
    //   4594: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4597: iconst_0
    //   4598: invokevirtual 819	org/vidogram/ui/ActionBar/ActionBarLayout:closeLastFragment	(Z)V
    //   4601: goto -67 -> 4534
    //   4604: iload 5
    //   4606: ifeq +189 -> 4795
    //   4609: invokestatic 207	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   4612: ifeq +110 -> 4722
    //   4615: iconst_0
    //   4616: istore 5
    //   4618: iload 5
    //   4620: aload_0
    //   4621: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4624: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   4627: invokevirtual 237	java/util/ArrayList:size	()I
    //   4630: if_icmpge +37 -> 4667
    //   4633: aload_0
    //   4634: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4637: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   4640: iload 5
    //   4642: invokevirtual 241	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4645: checkcast 243	org/vidogram/ui/ActionBar/BaseFragment
    //   4648: astore 13
    //   4650: aload 13
    //   4652: instanceof 821
    //   4655: ifeq +58 -> 4713
    //   4658: aload_0
    //   4659: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4662: aload 13
    //   4664: invokevirtual 816	org/vidogram/ui/ActionBar/ActionBarLayout:removeFragmentFromStack	(Lorg/vidogram/ui/ActionBar/BaseFragment;)V
    //   4667: aload_0
    //   4668: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4671: invokevirtual 269	org/vidogram/ui/ActionBar/ActionBarLayout:showLastFragment	()V
    //   4674: aload_0
    //   4675: getfield 177	org/vidogram/ui/LaunchActivity:rightActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4678: invokevirtual 269	org/vidogram/ui/ActionBar/ActionBarLayout:showLastFragment	()V
    //   4681: aload_0
    //   4682: getfield 518	org/vidogram/ui/LaunchActivity:drawerLayoutContainer	Lorg/vidogram/ui/ActionBar/DrawerLayoutContainer;
    //   4685: iconst_0
    //   4686: iconst_0
    //   4687: invokevirtual 524	org/vidogram/ui/ActionBar/DrawerLayoutContainer:setAllowOpenDrawer	(ZZ)V
    //   4690: aload_0
    //   4691: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4694: new 821	org/vidogram/ui/AudioPlayerActivity
    //   4697: dup
    //   4698: invokespecial 822	org/vidogram/ui/AudioPlayerActivity:<init>	()V
    //   4701: iconst_0
    //   4702: iconst_1
    //   4703: iconst_1
    //   4704: invokevirtual 509	org/vidogram/ui/ActionBar/ActionBarLayout:presentFragment	(Lorg/vidogram/ui/ActionBar/BaseFragment;ZZZ)Z
    //   4707: pop
    //   4708: iconst_1
    //   4709: istore_3
    //   4710: goto -3840 -> 870
    //   4713: iload 5
    //   4715: iconst_1
    //   4716: iadd
    //   4717: istore 5
    //   4719: goto -101 -> 4618
    //   4722: iconst_0
    //   4723: istore 5
    //   4725: iload 5
    //   4727: aload_0
    //   4728: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4731: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   4734: invokevirtual 237	java/util/ArrayList:size	()I
    //   4737: if_icmpge +37 -> 4774
    //   4740: aload_0
    //   4741: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4744: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   4747: iload 5
    //   4749: invokevirtual 241	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4752: checkcast 243	org/vidogram/ui/ActionBar/BaseFragment
    //   4755: astore 13
    //   4757: aload 13
    //   4759: instanceof 821
    //   4762: ifeq +24 -> 4786
    //   4765: aload_0
    //   4766: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4769: aload 13
    //   4771: invokevirtual 816	org/vidogram/ui/ActionBar/ActionBarLayout:removeFragmentFromStack	(Lorg/vidogram/ui/ActionBar/BaseFragment;)V
    //   4774: aload_0
    //   4775: getfield 518	org/vidogram/ui/LaunchActivity:drawerLayoutContainer	Lorg/vidogram/ui/ActionBar/DrawerLayoutContainer;
    //   4778: iconst_1
    //   4779: iconst_0
    //   4780: invokevirtual 524	org/vidogram/ui/ActionBar/DrawerLayoutContainer:setAllowOpenDrawer	(ZZ)V
    //   4783: goto -93 -> 4690
    //   4786: iload 5
    //   4788: iconst_1
    //   4789: iadd
    //   4790: istore 5
    //   4792: goto -67 -> 4725
    //   4795: aload_0
    //   4796: getfield 325	org/vidogram/ui/LaunchActivity:videoPath	Ljava/lang/String;
    //   4799: ifnonnull +38 -> 4837
    //   4802: aload_0
    //   4803: getfield 323	org/vidogram/ui/LaunchActivity:photoPathsArray	Ljava/util/ArrayList;
    //   4806: ifnonnull +31 -> 4837
    //   4809: aload_0
    //   4810: getfield 327	org/vidogram/ui/LaunchActivity:sendingText	Ljava/lang/String;
    //   4813: ifnonnull +24 -> 4837
    //   4816: aload_0
    //   4817: getfield 329	org/vidogram/ui/LaunchActivity:documentsPathsArray	Ljava/util/ArrayList;
    //   4820: ifnonnull +17 -> 4837
    //   4823: aload_0
    //   4824: getfield 337	org/vidogram/ui/LaunchActivity:contactsToSend	Ljava/util/ArrayList;
    //   4827: ifnonnull +10 -> 4837
    //   4830: aload_0
    //   4831: getfield 335	org/vidogram/ui/LaunchActivity:documentsUrisArray	Ljava/util/ArrayList;
    //   4834: ifnull +357 -> 5191
    //   4837: invokestatic 207	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   4840: ifne +16 -> 4856
    //   4843: invokestatic 781	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   4846: getstatic 784	org/vidogram/messenger/NotificationCenter:closeChats	I
    //   4849: iconst_0
    //   4850: anewarray 591	java/lang/Object
    //   4853: invokevirtual 788	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   4856: lload 11
    //   4858: lconst_0
    //   4859: lcmp
    //   4860: ifne +318 -> 5178
    //   4863: new 317	android/os/Bundle
    //   4866: dup
    //   4867: invokespecial 488	android/os/Bundle:<init>	()V
    //   4870: astore 13
    //   4872: aload 13
    //   4874: ldc_w 824
    //   4877: iconst_1
    //   4878: invokevirtual 828	android/os/Bundle:putBoolean	(Ljava/lang/String;Z)V
    //   4881: aload_0
    //   4882: getfield 337	org/vidogram/ui/LaunchActivity:contactsToSend	Ljava/util/ArrayList;
    //   4885: ifnull +167 -> 5052
    //   4888: aload 13
    //   4890: ldc_w 830
    //   4893: ldc_w 832
    //   4896: ldc_w 833
    //   4899: invokestatic 839	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4902: invokevirtual 668	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   4905: aload 13
    //   4907: ldc_w 841
    //   4910: ldc_w 843
    //   4913: ldc_w 844
    //   4916: invokestatic 839	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4919: invokevirtual 668	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   4922: new 846	org/vidogram/ui/DialogsActivity
    //   4925: dup
    //   4926: aload 13
    //   4928: invokespecial 847	org/vidogram/ui/DialogsActivity:<init>	(Landroid/os/Bundle;)V
    //   4931: astore 13
    //   4933: aload 13
    //   4935: aload_0
    //   4936: invokevirtual 851	org/vidogram/ui/DialogsActivity:setDelegate	(Lorg/vidogram/ui/DialogsActivity$DialogsActivityDelegate;)V
    //   4939: invokestatic 207	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   4942: ifeq +152 -> 5094
    //   4945: aload_0
    //   4946: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4949: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   4952: invokevirtual 237	java/util/ArrayList:size	()I
    //   4955: ifle +134 -> 5089
    //   4958: aload_0
    //   4959: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4962: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   4965: aload_0
    //   4966: getfield 187	org/vidogram/ui/LaunchActivity:layersActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4969: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   4972: invokevirtual 237	java/util/ArrayList:size	()I
    //   4975: iconst_1
    //   4976: isub
    //   4977: invokevirtual 241	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4980: instanceof 846
    //   4983: ifeq +106 -> 5089
    //   4986: iconst_1
    //   4987: istore_3
    //   4988: aload_0
    //   4989: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   4992: aload 13
    //   4994: iload_3
    //   4995: iconst_1
    //   4996: iconst_1
    //   4997: invokevirtual 509	org/vidogram/ui/ActionBar/ActionBarLayout:presentFragment	(Lorg/vidogram/ui/ActionBar/BaseFragment;ZZZ)Z
    //   5000: pop
    //   5001: invokestatic 856	org/vidogram/ui/PhotoViewer:getInstance	()Lorg/vidogram/ui/PhotoViewer;
    //   5004: invokevirtual 859	org/vidogram/ui/PhotoViewer:isVisible	()Z
    //   5007: ifeq +139 -> 5146
    //   5010: invokestatic 856	org/vidogram/ui/PhotoViewer:getInstance	()Lorg/vidogram/ui/PhotoViewer;
    //   5013: iconst_0
    //   5014: iconst_1
    //   5015: invokevirtual 862	org/vidogram/ui/PhotoViewer:closePhoto	(ZZ)V
    //   5018: aload_0
    //   5019: getfield 518	org/vidogram/ui/LaunchActivity:drawerLayoutContainer	Lorg/vidogram/ui/ActionBar/DrawerLayoutContainer;
    //   5022: iconst_0
    //   5023: iconst_0
    //   5024: invokevirtual 524	org/vidogram/ui/ActionBar/DrawerLayoutContainer:setAllowOpenDrawer	(ZZ)V
    //   5027: invokestatic 207	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   5030: ifeq +136 -> 5166
    //   5033: aload_0
    //   5034: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5037: invokevirtual 269	org/vidogram/ui/ActionBar/ActionBarLayout:showLastFragment	()V
    //   5040: aload_0
    //   5041: getfield 177	org/vidogram/ui/LaunchActivity:rightActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5044: invokevirtual 269	org/vidogram/ui/ActionBar/ActionBarLayout:showLastFragment	()V
    //   5047: iconst_1
    //   5048: istore_3
    //   5049: goto -4179 -> 870
    //   5052: aload 13
    //   5054: ldc_w 830
    //   5057: ldc_w 864
    //   5060: ldc_w 833
    //   5063: invokestatic 839	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5066: invokevirtual 668	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   5069: aload 13
    //   5071: ldc_w 841
    //   5074: ldc_w 866
    //   5077: ldc_w 867
    //   5080: invokestatic 839	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5083: invokevirtual 668	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   5086: goto -164 -> 4922
    //   5089: iconst_0
    //   5090: istore_3
    //   5091: goto -103 -> 4988
    //   5094: aload_0
    //   5095: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5098: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   5101: invokevirtual 237	java/util/ArrayList:size	()I
    //   5104: iconst_1
    //   5105: if_icmple +36 -> 5141
    //   5108: aload_0
    //   5109: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5112: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   5115: aload_0
    //   5116: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5119: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   5122: invokevirtual 237	java/util/ArrayList:size	()I
    //   5125: iconst_1
    //   5126: isub
    //   5127: invokevirtual 241	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   5130: instanceof 846
    //   5133: ifeq +8 -> 5141
    //   5136: iconst_1
    //   5137: istore_3
    //   5138: goto -150 -> 4988
    //   5141: iconst_0
    //   5142: istore_3
    //   5143: goto -155 -> 4988
    //   5146: invokestatic 872	org/vidogram/ui/ArticleViewer:getInstance	()Lorg/vidogram/ui/ArticleViewer;
    //   5149: invokevirtual 873	org/vidogram/ui/ArticleViewer:isVisible	()Z
    //   5152: ifeq -134 -> 5018
    //   5155: invokestatic 872	org/vidogram/ui/ArticleViewer:getInstance	()Lorg/vidogram/ui/ArticleViewer;
    //   5158: iconst_0
    //   5159: iconst_1
    //   5160: invokevirtual 875	org/vidogram/ui/ArticleViewer:close	(ZZ)V
    //   5163: goto -145 -> 5018
    //   5166: aload_0
    //   5167: getfield 518	org/vidogram/ui/LaunchActivity:drawerLayoutContainer	Lorg/vidogram/ui/ActionBar/DrawerLayoutContainer;
    //   5170: iconst_1
    //   5171: iconst_0
    //   5172: invokevirtual 524	org/vidogram/ui/ActionBar/DrawerLayoutContainer:setAllowOpenDrawer	(ZZ)V
    //   5175: goto -128 -> 5047
    //   5178: aload_0
    //   5179: aconst_null
    //   5180: lload 11
    //   5182: iconst_0
    //   5183: invokevirtual 879	org/vidogram/ui/LaunchActivity:didSelectDialog	(Lorg/vidogram/ui/DialogsActivity;JZ)V
    //   5186: iconst_0
    //   5187: istore_3
    //   5188: goto -4318 -> 870
    //   5191: aload 17
    //   5193: invokevirtual 487	java/lang/Integer:intValue	()I
    //   5196: ifeq +67 -> 5263
    //   5199: aload_0
    //   5200: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5203: new 881	org/vidogram/ui/SettingsActivity
    //   5206: dup
    //   5207: invokespecial 882	org/vidogram/ui/SettingsActivity:<init>	()V
    //   5210: iconst_0
    //   5211: iconst_1
    //   5212: iconst_1
    //   5213: invokevirtual 509	org/vidogram/ui/ActionBar/ActionBarLayout:presentFragment	(Lorg/vidogram/ui/ActionBar/BaseFragment;ZZZ)Z
    //   5216: pop
    //   5217: invokestatic 207	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   5220: ifeq +31 -> 5251
    //   5223: aload_0
    //   5224: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5227: invokevirtual 269	org/vidogram/ui/ActionBar/ActionBarLayout:showLastFragment	()V
    //   5230: aload_0
    //   5231: getfield 177	org/vidogram/ui/LaunchActivity:rightActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5234: invokevirtual 269	org/vidogram/ui/ActionBar/ActionBarLayout:showLastFragment	()V
    //   5237: aload_0
    //   5238: getfield 518	org/vidogram/ui/LaunchActivity:drawerLayoutContainer	Lorg/vidogram/ui/ActionBar/DrawerLayoutContainer;
    //   5241: iconst_0
    //   5242: iconst_0
    //   5243: invokevirtual 524	org/vidogram/ui/ActionBar/DrawerLayoutContainer:setAllowOpenDrawer	(ZZ)V
    //   5246: iconst_1
    //   5247: istore_3
    //   5248: goto -4378 -> 870
    //   5251: aload_0
    //   5252: getfield 518	org/vidogram/ui/LaunchActivity:drawerLayoutContainer	Lorg/vidogram/ui/ActionBar/DrawerLayoutContainer;
    //   5255: iconst_1
    //   5256: iconst_0
    //   5257: invokevirtual 524	org/vidogram/ui/ActionBar/DrawerLayoutContainer:setAllowOpenDrawer	(ZZ)V
    //   5260: goto -14 -> 5246
    //   5263: aload 16
    //   5265: invokevirtual 487	java/lang/Integer:intValue	()I
    //   5268: ifeq +259 -> 5527
    //   5271: new 317	android/os/Bundle
    //   5274: dup
    //   5275: invokespecial 488	android/os/Bundle:<init>	()V
    //   5278: astore 13
    //   5280: aload 13
    //   5282: ldc_w 884
    //   5285: iconst_1
    //   5286: invokevirtual 828	android/os/Bundle:putBoolean	(Ljava/lang/String;Z)V
    //   5289: aload_0
    //   5290: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5293: new 886	org/vidogram/ui/ContactsActivity
    //   5296: dup
    //   5297: aload 13
    //   5299: invokespecial 887	org/vidogram/ui/ContactsActivity:<init>	(Landroid/os/Bundle;)V
    //   5302: iconst_0
    //   5303: iconst_1
    //   5304: iconst_1
    //   5305: invokevirtual 509	org/vidogram/ui/ActionBar/ActionBarLayout:presentFragment	(Lorg/vidogram/ui/ActionBar/BaseFragment;ZZZ)Z
    //   5308: pop
    //   5309: invokestatic 207	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   5312: ifeq +31 -> 5343
    //   5315: aload_0
    //   5316: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5319: invokevirtual 269	org/vidogram/ui/ActionBar/ActionBarLayout:showLastFragment	()V
    //   5322: aload_0
    //   5323: getfield 177	org/vidogram/ui/LaunchActivity:rightActionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5326: invokevirtual 269	org/vidogram/ui/ActionBar/ActionBarLayout:showLastFragment	()V
    //   5329: aload_0
    //   5330: getfield 518	org/vidogram/ui/LaunchActivity:drawerLayoutContainer	Lorg/vidogram/ui/ActionBar/DrawerLayoutContainer;
    //   5333: iconst_0
    //   5334: iconst_0
    //   5335: invokevirtual 524	org/vidogram/ui/ActionBar/DrawerLayoutContainer:setAllowOpenDrawer	(ZZ)V
    //   5338: iconst_1
    //   5339: istore_3
    //   5340: goto -4470 -> 870
    //   5343: aload_0
    //   5344: getfield 518	org/vidogram/ui/LaunchActivity:drawerLayoutContainer	Lorg/vidogram/ui/ActionBar/DrawerLayoutContainer;
    //   5347: iconst_1
    //   5348: iconst_0
    //   5349: invokevirtual 524	org/vidogram/ui/ActionBar/DrawerLayoutContainer:setAllowOpenDrawer	(ZZ)V
    //   5352: goto -14 -> 5338
    //   5355: aload_0
    //   5356: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5359: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   5362: invokevirtual 272	java/util/ArrayList:isEmpty	()Z
    //   5365: ifeq -4438 -> 927
    //   5368: new 846	org/vidogram/ui/DialogsActivity
    //   5371: dup
    //   5372: aconst_null
    //   5373: invokespecial 847	org/vidogram/ui/DialogsActivity:<init>	(Landroid/os/Bundle;)V
    //   5376: astore 13
    //   5378: aload 13
    //   5380: aload_0
    //   5381: getfield 889	org/vidogram/ui/LaunchActivity:sideMenu	Lorg/vidogram/ui/Components/RecyclerListView;
    //   5384: invokevirtual 893	org/vidogram/ui/DialogsActivity:setSideMenu	(Lorg/vidogram/messenger/support/widget/RecyclerView;)V
    //   5387: aload_0
    //   5388: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5391: aload 13
    //   5393: invokevirtual 516	org/vidogram/ui/ActionBar/ActionBarLayout:addFragmentToStack	(Lorg/vidogram/ui/ActionBar/BaseFragment;)Z
    //   5396: pop
    //   5397: aload_0
    //   5398: getfield 518	org/vidogram/ui/LaunchActivity:drawerLayoutContainer	Lorg/vidogram/ui/ActionBar/DrawerLayoutContainer;
    //   5401: iconst_1
    //   5402: iconst_0
    //   5403: invokevirtual 524	org/vidogram/ui/ActionBar/DrawerLayoutContainer:setAllowOpenDrawer	(ZZ)V
    //   5406: goto -4479 -> 927
    //   5409: aload_0
    //   5410: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5413: getfield 233	org/vidogram/ui/ActionBar/ActionBarLayout:fragmentsStack	Ljava/util/ArrayList;
    //   5416: invokevirtual 272	java/util/ArrayList:isEmpty	()Z
    //   5419: ifeq -4492 -> 927
    //   5422: invokestatic 340	org/vidogram/messenger/UserConfig:isClientActivated	()Z
    //   5425: ifne +30 -> 5455
    //   5428: aload_0
    //   5429: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5432: new 511	org/vidogram/ui/LoginActivity
    //   5435: dup
    //   5436: invokespecial 512	org/vidogram/ui/LoginActivity:<init>	()V
    //   5439: invokevirtual 516	org/vidogram/ui/ActionBar/ActionBarLayout:addFragmentToStack	(Lorg/vidogram/ui/ActionBar/BaseFragment;)Z
    //   5442: pop
    //   5443: aload_0
    //   5444: getfield 518	org/vidogram/ui/LaunchActivity:drawerLayoutContainer	Lorg/vidogram/ui/ActionBar/DrawerLayoutContainer;
    //   5447: iconst_0
    //   5448: iconst_0
    //   5449: invokevirtual 524	org/vidogram/ui/ActionBar/DrawerLayoutContainer:setAllowOpenDrawer	(ZZ)V
    //   5452: goto -4525 -> 927
    //   5455: new 846	org/vidogram/ui/DialogsActivity
    //   5458: dup
    //   5459: aconst_null
    //   5460: invokespecial 847	org/vidogram/ui/DialogsActivity:<init>	(Landroid/os/Bundle;)V
    //   5463: astore 13
    //   5465: aload 13
    //   5467: aload_0
    //   5468: getfield 889	org/vidogram/ui/LaunchActivity:sideMenu	Lorg/vidogram/ui/Components/RecyclerListView;
    //   5471: invokevirtual 893	org/vidogram/ui/DialogsActivity:setSideMenu	(Lorg/vidogram/messenger/support/widget/RecyclerView;)V
    //   5474: aload_0
    //   5475: getfield 137	org/vidogram/ui/LaunchActivity:actionBarLayout	Lorg/vidogram/ui/ActionBar/ActionBarLayout;
    //   5478: aload 13
    //   5480: invokevirtual 516	org/vidogram/ui/ActionBar/ActionBarLayout:addFragmentToStack	(Lorg/vidogram/ui/ActionBar/BaseFragment;)Z
    //   5483: pop
    //   5484: aload_0
    //   5485: getfield 518	org/vidogram/ui/LaunchActivity:drawerLayoutContainer	Lorg/vidogram/ui/ActionBar/DrawerLayoutContainer;
    //   5488: iconst_1
    //   5489: iconst_0
    //   5490: invokevirtual 524	org/vidogram/ui/ActionBar/DrawerLayoutContainer:setAllowOpenDrawer	(ZZ)V
    //   5493: goto -4566 -> 927
    //   5496: astore 16
    //   5498: goto -1446 -> 4052
    //   5501: astore 16
    //   5503: aload 14
    //   5505: astore 17
    //   5507: aload 13
    //   5509: astore 16
    //   5511: goto -1761 -> 3750
    //   5514: astore 16
    //   5516: aload 14
    //   5518: astore 17
    //   5520: aload 13
    //   5522: astore 16
    //   5524: goto -1774 -> 3750
    //   5527: iconst_0
    //   5528: istore_3
    //   5529: goto -4659 -> 870
    //   5532: iconst_0
    //   5533: istore 5
    //   5535: aload 26
    //   5537: astore 16
    //   5539: aload 27
    //   5541: astore 17
    //   5543: iload 7
    //   5545: istore 6
    //   5547: goto -4790 -> 757
    //   5550: goto -1510 -> 4040
    //   5553: goto -1506 -> 4047
    //   5556: aconst_null
    //   5557: astore 17
    //   5559: aconst_null
    //   5560: astore 30
    //   5562: aconst_null
    //   5563: astore 22
    //   5565: aconst_null
    //   5566: astore 21
    //   5568: aconst_null
    //   5569: astore 25
    //   5571: aconst_null
    //   5572: astore 24
    //   5574: iconst_0
    //   5575: istore_3
    //   5576: aconst_null
    //   5577: astore 16
    //   5579: aconst_null
    //   5580: astore 29
    //   5582: aconst_null
    //   5583: astore 23
    //   5585: aconst_null
    //   5586: astore 20
    //   5588: aload 15
    //   5590: astore 14
    //   5592: aload 19
    //   5594: astore 15
    //   5596: aload 18
    //   5598: astore 13
    //   5600: aload 30
    //   5602: astore 18
    //   5604: aload 29
    //   5606: astore 19
    //   5608: goto -3196 -> 2412
    //   5611: aconst_null
    //   5612: astore 23
    //   5614: aconst_null
    //   5615: astore 18
    //   5617: aconst_null
    //   5618: astore 21
    //   5620: aconst_null
    //   5621: astore 20
    //   5623: aconst_null
    //   5624: astore 25
    //   5626: aconst_null
    //   5627: astore 19
    //   5629: aload 14
    //   5631: astore 29
    //   5633: aconst_null
    //   5634: astore 24
    //   5636: aconst_null
    //   5637: astore 17
    //   5639: aconst_null
    //   5640: astore 22
    //   5642: aload 15
    //   5644: astore 14
    //   5646: aload 29
    //   5648: astore 15
    //   5650: goto -3238 -> 2412
    //   5653: aconst_null
    //   5654: astore 17
    //   5656: aconst_null
    //   5657: astore 25
    //   5659: iconst_0
    //   5660: istore_3
    //   5661: aconst_null
    //   5662: astore 16
    //   5664: aconst_null
    //   5665: astore 19
    //   5667: aconst_null
    //   5668: astore 20
    //   5670: aload 14
    //   5672: astore 29
    //   5674: aload 15
    //   5676: astore 14
    //   5678: aload 29
    //   5680: astore 15
    //   5682: goto -3270 -> 2412
    //   5685: aload 16
    //   5687: astore 17
    //   5689: aload 18
    //   5691: astore 16
    //   5693: goto -2788 -> 2905
    //   5696: aload 16
    //   5698: astore 17
    //   5700: aconst_null
    //   5701: astore 16
    //   5703: goto -2798 -> 2905
    //   5706: aconst_null
    //   5707: astore 16
    //   5709: aconst_null
    //   5710: astore 17
    //   5712: goto -2807 -> 2905
    //   5715: aconst_null
    //   5716: astore 17
    //   5718: aconst_null
    //   5719: astore 16
    //   5721: aconst_null
    //   5722: astore 19
    //   5724: aconst_null
    //   5725: astore 20
    //   5727: aconst_null
    //   5728: astore 21
    //   5730: aconst_null
    //   5731: astore 23
    //   5733: aconst_null
    //   5734: astore 22
    //   5736: aconst_null
    //   5737: astore 24
    //   5739: aconst_null
    //   5740: astore 18
    //   5742: goto -3398 -> 2344
    //   5745: aconst_null
    //   5746: astore 21
    //   5748: aconst_null
    //   5749: astore 17
    //   5751: aload 16
    //   5753: astore 19
    //   5755: aconst_null
    //   5756: astore 16
    //   5758: aconst_null
    //   5759: astore 20
    //   5761: aconst_null
    //   5762: astore 23
    //   5764: aconst_null
    //   5765: astore 22
    //   5767: aconst_null
    //   5768: astore 24
    //   5770: aconst_null
    //   5771: astore 18
    //   5773: goto -3429 -> 2344
    //   5776: iconst_0
    //   5777: istore_3
    //   5778: goto -3096 -> 2682
    //   5781: iconst_0
    //   5782: istore_3
    //   5783: aload 16
    //   5785: astore 17
    //   5787: goto -3077 -> 2710
    //   5790: aload 14
    //   5792: astore 16
    //   5794: aload 15
    //   5796: astore 14
    //   5798: aload 16
    //   5800: astore 15
    //   5802: goto -3291 -> 2511
    //   5805: goto -3804 -> 2001
    //   5808: aload 17
    //   5810: astore 16
    //   5812: goto -4003 -> 1809
    //   5815: goto +45 -> 5860
    //   5818: iconst_0
    //   5819: istore 5
    //   5821: goto -5095 -> 726
    //   5824: aload 16
    //   5826: ifnonnull -5422 -> 404
    //   5829: goto -5539 -> 290
    //   5832: lconst_0
    //   5833: lstore 11
    //   5835: goto -5720 -> 115
    //   5838: iload 5
    //   5840: iconst_1
    //   5841: iadd
    //   5842: istore 5
    //   5844: aload 19
    //   5846: astore 17
    //   5848: goto -5381 -> 467
    //   5851: goto -5561 -> 290
    //   5854: iconst_0
    //   5855: istore 6
    //   5857: goto -4775 -> 1082
    //   5860: iload 5
    //   5862: iconst_1
    //   5863: iadd
    //   5864: istore 5
    //   5866: goto -4149 -> 1717
    //
    // Exception table:
    //   from	to	target	type
    //   226	241	716	java/lang/Exception
    //   246	266	716	java/lang/Exception
    //   269	290	716	java/lang/Exception
    //   290	297	716	java/lang/Exception
    //   302	369	716	java/lang/Exception
    //   372	398	716	java/lang/Exception
    //   404	441	716	java/lang/Exception
    //   441	458	716	java/lang/Exception
    //   474	494	716	java/lang/Exception
    //   501	514	716	java/lang/Exception
    //   527	540	716	java/lang/Exception
    //   553	562	716	java/lang/Exception
    //   567	578	716	java/lang/Exception
    //   578	592	716	java/lang/Exception
    //   597	628	716	java/lang/Exception
    //   633	646	716	java/lang/Exception
    //   651	670	716	java/lang/Exception
    //   675	682	716	java/lang/Exception
    //   685	713	716	java/lang/Exception
    //   962	1004	716	java/lang/Exception
    //   1020	1079	716	java/lang/Exception
    //   1082	1160	716	java/lang/Exception
    //   1171	1176	716	java/lang/Exception
    //   1007	1017	1169	java/lang/Exception
    //   1694	1709	2134	java/lang/Exception
    //   1717	1739	2134	java/lang/Exception
    //   1743	1761	2134	java/lang/Exception
    //   1761	1768	2134	java/lang/Exception
    //   1773	1789	2134	java/lang/Exception
    //   1798	1806	2134	java/lang/Exception
    //   1819	1830	2134	java/lang/Exception
    //   1833	1855	2134	java/lang/Exception
    //   1859	1877	2134	java/lang/Exception
    //   1877	1902	2134	java/lang/Exception
    //   1902	1912	2134	java/lang/Exception
    //   1961	2001	2134	java/lang/Exception
    //   2001	2018	2134	java/lang/Exception
    //   2040	2064	2134	java/lang/Exception
    //   2064	2093	2134	java/lang/Exception
    //   2093	2113	2134	java/lang/Exception
    //   3763	3770	3862	java/lang/NumberFormatException
    //   3969	3986	4050	java/lang/Exception
    //   3991	4033	4050	java/lang/Exception
    //   4040	4047	5496	java/lang/Exception
    //   3841	3848	5501	java/lang/NumberFormatException
    //   3732	3739	5514	java/lang/NumberFormatException
  }

  private void onFinish()
  {
    if (this.finished)
      return;
    this.finished = true;
    if (this.lockRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
      this.lockRunnable = null;
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mainUserInfoChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdatedConnectionState);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needShowAlert);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
  }

  private void onPasscodePause()
  {
    if (this.lockRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
      this.lockRunnable = null;
    }
    if (UserConfig.passcodeHash.length() != 0)
    {
      UserConfig.lastPauseTime = ConnectionsManager.getInstance().getCurrentTime();
      this.lockRunnable = new Runnable()
      {
        public void run()
        {
          if (LaunchActivity.this.lockRunnable == this)
          {
            if (!AndroidUtilities.needShowPasscode(true))
              break label40;
            FileLog.e("lock app");
            LaunchActivity.this.showPasscodeActivity();
          }
          while (true)
          {
            LaunchActivity.access$1602(LaunchActivity.this, null);
            return;
            label40: FileLog.e("didn't pass lock check");
          }
        }
      };
      if (UserConfig.appLocked)
        AndroidUtilities.runOnUIThread(this.lockRunnable, 1000L);
    }
    while (true)
    {
      UserConfig.saveConfig(false);
      return;
      if (UserConfig.autoLockIn == 0)
        continue;
      AndroidUtilities.runOnUIThread(this.lockRunnable, UserConfig.autoLockIn * 1000L + 1000L);
      continue;
      UserConfig.lastPauseTime = 0;
    }
  }

  private void onPasscodeResume()
  {
    if (this.lockRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
      this.lockRunnable = null;
    }
    if (AndroidUtilities.needShowPasscode(true))
      showPasscodeActivity();
    if (UserConfig.lastPauseTime != 0)
    {
      UserConfig.lastPauseTime = 0;
      UserConfig.saveConfig(false);
    }
  }

  private void runLinkRequest(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, boolean paramBoolean, Integer paramInteger, String paramString7, int paramInt)
  {
    AlertDialog localAlertDialog = new AlertDialog(this, 1);
    localAlertDialog.setMessage(LocaleController.getString("Loading", 2131165920));
    localAlertDialog.setCanceledOnTouchOutside(false);
    localAlertDialog.setCancelable(false);
    int j = 0;
    int i;
    if (paramString1 != null)
    {
      paramString2 = new TLRPC.TL_contacts_resolveUsername();
      paramString2.username = paramString1;
      i = ConnectionsManager.getInstance().sendRequest(paramString2, new RequestDelegate(localAlertDialog, paramString7, paramString5, paramString4, paramInteger)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject, paramTL_error)
          {
            public void run()
            {
              if (!LaunchActivity.this.isFinishing());
              try
              {
                LaunchActivity.8.this.val$progressDialog.dismiss();
                TLRPC.TL_contacts_resolvedPeer localTL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer)this.val$response;
                if ((this.val$error != null) || (LaunchActivity.this.actionBarLayout == null) || ((LaunchActivity.8.this.val$game != null) && ((LaunchActivity.8.this.val$game == null) || (localTL_contacts_resolvedPeer.users.isEmpty()))))
                  break label1008;
                MessagesController.getInstance().putUsers(localTL_contacts_resolvedPeer.users, false);
                MessagesController.getInstance().putChats(localTL_contacts_resolvedPeer.chats, false);
                MessagesStorage.getInstance().putUsersAndChats(localTL_contacts_resolvedPeer.users, localTL_contacts_resolvedPeer.chats, false, true);
                if (LaunchActivity.8.this.val$game != null)
                {
                  localObject2 = new Bundle();
                  ((Bundle)localObject2).putBoolean("onlySelect", true);
                  ((Bundle)localObject2).putBoolean("cantSendToChannels", true);
                  ((Bundle)localObject2).putInt("dialogsType", 1);
                  ((Bundle)localObject2).putString("selectAlertString", LocaleController.getString("SendGameTo", 2131166413));
                  ((Bundle)localObject2).putString("selectAlertStringGroup", LocaleController.getString("SendGameToGroup", 2131166414));
                  localObject2 = new DialogsActivity((Bundle)localObject2);
                  ((DialogsActivity)localObject2).setDelegate(new DialogsActivity.DialogsActivityDelegate(localTL_contacts_resolvedPeer)
                  {
                    public void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean)
                    {
                      Object localObject = new TLRPC.TL_inputMediaGame();
                      ((TLRPC.TL_inputMediaGame)localObject).id = new TLRPC.TL_inputGameShortName();
                      ((TLRPC.TL_inputMediaGame)localObject).id.short_name = LaunchActivity.8.this.val$game;
                      ((TLRPC.TL_inputMediaGame)localObject).id.bot_id = MessagesController.getInputUser((TLRPC.User)this.val$res.users.get(0));
                      SendMessagesHelper.getInstance().sendGame(MessagesController.getInputPeer((int)paramLong), (TLRPC.TL_inputMediaGame)localObject, 0L, 0L);
                      localObject = new Bundle();
                      ((Bundle)localObject).putBoolean("scrollToTopOnResume", true);
                      int i = (int)paramLong;
                      int j = (int)(paramLong >> 32);
                      if (i != 0)
                        if (j == 1)
                          ((Bundle)localObject).putInt("chat_id", i);
                      while (true)
                      {
                        if (MessagesController.checkCanOpenChat((Bundle)localObject, paramDialogsActivity))
                        {
                          NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                          LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity((Bundle)localObject), true, false, true);
                        }
                        return;
                        if (i > 0)
                        {
                          ((Bundle)localObject).putInt("user_id", i);
                          continue;
                        }
                        if (i >= 0)
                          continue;
                        ((Bundle)localObject).putInt("chat_id", -i);
                        continue;
                        ((Bundle)localObject).putInt("enc_id", j);
                      }
                    }
                  });
                  if (AndroidUtilities.isTablet())
                    if ((LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() > 0) && ((LaunchActivity.this.layersActionBarLayout.fragmentsStack.get(LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity)))
                    {
                      bool = true;
                      LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject2, bool, true, true);
                      if (!PhotoViewer.getInstance().isVisible())
                        break label458;
                      PhotoViewer.getInstance().closePhoto(false, true);
                      LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                      if (!AndroidUtilities.isTablet())
                        break label478;
                      LaunchActivity.this.actionBarLayout.showLastFragment();
                      LaunchActivity.this.rightActionBarLayout.showLastFragment();
                      return;
                    }
                }
              }
              catch (Exception localException1)
              {
                while (true)
                {
                  FileLog.e(localException1);
                  continue;
                  boolean bool = false;
                  continue;
                  if ((LaunchActivity.this.actionBarLayout.fragmentsStack.size() > 1) && ((LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity)))
                  {
                    bool = true;
                    continue;
                  }
                  bool = false;
                  continue;
                  label458: if (!ArticleViewer.getInstance().isVisible())
                    continue;
                  ArticleViewer.getInstance().close(false, true);
                }
                label478: LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                return;
              }
              Object localObject1;
              if (LaunchActivity.8.this.val$botChat != null)
              {
                if (!localException1.users.isEmpty());
                for (TLRPC.User localUser = (TLRPC.User)localException1.users.get(0); (localUser == null) || ((localUser.bot) && (localUser.bot_nochats)); localObject1 = null)
                  try
                  {
                    Toast.makeText(LaunchActivity.this, LocaleController.getString("BotCantJoinGroups", 2131165391), 0).show();
                    return;
                  }
                  catch (Exception localException2)
                  {
                    FileLog.e(localException2);
                    return;
                  }
                localObject2 = new Bundle();
                ((Bundle)localObject2).putBoolean("onlySelect", true);
                ((Bundle)localObject2).putInt("dialogsType", 2);
                ((Bundle)localObject2).putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", 2131165293, new Object[] { UserObject.getUserName((TLRPC.User)localObject1), "%1$s" }));
                localObject2 = new DialogsActivity((Bundle)localObject2);
                ((DialogsActivity)localObject2).setDelegate(new DialogsActivity.DialogsActivityDelegate((TLRPC.User)localObject1)
                {
                  public void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean)
                  {
                    paramDialogsActivity = new Bundle();
                    paramDialogsActivity.putBoolean("scrollToTopOnResume", true);
                    paramDialogsActivity.putInt("chat_id", -(int)paramLong);
                    if ((LaunchActivity.mainFragmentsStack.isEmpty()) || (MessagesController.checkCanOpenChat(paramDialogsActivity, (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))))
                    {
                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                      MessagesController.getInstance().addUserToChat(-(int)paramLong, this.val$user, null, 0, LaunchActivity.8.this.val$botChat, null);
                      LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(paramDialogsActivity), true, false, true);
                    }
                  }
                });
                LaunchActivity.this.presentFragment((BaseFragment)localObject2);
                return;
              }
              Object localObject2 = new Bundle();
              long l;
              if (!((TLRPC.TL_contacts_resolvedPeer)localObject1).chats.isEmpty())
              {
                ((Bundle)localObject2).putInt("chat_id", ((TLRPC.Chat)((TLRPC.TL_contacts_resolvedPeer)localObject1).chats.get(0)).id);
                l = -((TLRPC.Chat)((TLRPC.TL_contacts_resolvedPeer)localObject1).chats.get(0)).id;
                if ((LaunchActivity.8.this.val$botUser == null) || (((TLRPC.TL_contacts_resolvedPeer)localObject1).users.size() <= 0) || (!((TLRPC.User)((TLRPC.TL_contacts_resolvedPeer)localObject1).users.get(0)).bot))
                  break label1040;
                ((Bundle)localObject2).putString("botUser", LaunchActivity.8.this.val$botUser);
              }
              label1040: for (int i = 1; ; i = 0)
              {
                if (LaunchActivity.8.this.val$messageId != null)
                  ((Bundle)localObject2).putInt("message_id", LaunchActivity.8.this.val$messageId.intValue());
                if (!LaunchActivity.mainFragmentsStack.isEmpty());
                for (localObject1 = (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1); ; localObject1 = null)
                {
                  if ((localObject1 != null) && (!MessagesController.checkCanOpenChat((Bundle)localObject2, (BaseFragment)localObject1)))
                    break label962;
                  if ((i == 0) || (localObject1 == null) || (!(localObject1 instanceof ChatActivity)) || (((ChatActivity)localObject1).getDialogId() != l))
                    break label964;
                  ((ChatActivity)localObject1).setBotUser(LaunchActivity.8.this.val$botUser);
                  return;
                  ((Bundle)localObject2).putInt("user_id", ((TLRPC.User)((TLRPC.TL_contacts_resolvedPeer)localObject1).users.get(0)).id);
                  l = ((TLRPC.User)((TLRPC.TL_contacts_resolvedPeer)localObject1).users.get(0)).id;
                  break;
                }
                label962: break;
                label964: localObject1 = new ChatActivity((Bundle)localObject2);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject1, false, true, true);
                return;
                try
                {
                  label1008: Toast.makeText(LaunchActivity.this, LocaleController.getString("NoUsernameFound", 2131166054), 0).show();
                  return;
                }
                catch (Exception localException3)
                {
                  FileLog.e(localException3);
                  return;
                }
              }
            }
          });
        }
      });
    }
    while (true)
    {
      if (i != 0)
      {
        localAlertDialog.setButton(-2, LocaleController.getString("Cancel", 2131165427), new DialogInterface.OnClickListener(i)
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
            try
            {
              paramDialogInterface.dismiss();
              return;
            }
            catch (Exception paramDialogInterface)
            {
              FileLog.e(paramDialogInterface);
            }
          }
        });
        localAlertDialog.show();
      }
      do
      {
        return;
        if (paramString2 != null)
        {
          if (paramInt == 0)
          {
            TLRPC.TL_messages_checkChatInvite localTL_messages_checkChatInvite = new TLRPC.TL_messages_checkChatInvite();
            localTL_messages_checkChatInvite.hash = paramString2;
            i = ConnectionsManager.getInstance().sendRequest(localTL_messages_checkChatInvite, new RequestDelegate(localAlertDialog, paramString2, paramString1, paramString3, paramString4, paramString5, paramString6, paramBoolean, paramInteger, paramString7)
            {
              public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
              {
                AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
                {
                  public void run()
                  {
                    if (!LaunchActivity.this.isFinishing());
                    Object localObject2;
                    try
                    {
                      LaunchActivity.9.this.val$progressDialog.dismiss();
                      if ((this.val$error == null) && (LaunchActivity.this.actionBarLayout != null))
                      {
                        Object localObject1 = (TLRPC.ChatInvite)this.val$response;
                        if ((((TLRPC.ChatInvite)localObject1).chat != null) && (!ChatObject.isLeftFromChat(((TLRPC.ChatInvite)localObject1).chat)))
                        {
                          MessagesController.getInstance().putChat(((TLRPC.ChatInvite)localObject1).chat, false);
                          localObject3 = new ArrayList();
                          ((ArrayList)localObject3).add(((TLRPC.ChatInvite)localObject1).chat);
                          MessagesStorage.getInstance().putUsersAndChats(null, (ArrayList)localObject3, false, true);
                          localObject3 = new Bundle();
                          ((Bundle)localObject3).putInt("chat_id", ((TLRPC.ChatInvite)localObject1).chat.id);
                          if ((LaunchActivity.mainFragmentsStack.isEmpty()) || (MessagesController.checkCanOpenChat((Bundle)localObject3, (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))))
                          {
                            localObject1 = new ChatActivity((Bundle)localObject3);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject1, false, true, true);
                          }
                          return;
                        }
                      }
                    }
                    catch (Exception localObject2)
                    {
                      while (true)
                        FileLog.e(localException);
                      if (((localException.chat == null) && ((!localException.channel) || (localException.megagroup))) || ((localException.chat != null) && ((!ChatObject.isChannel(localException.chat)) || (localException.chat.megagroup)) && (!LaunchActivity.mainFragmentsStack.isEmpty())))
                      {
                        localObject3 = (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1);
                        ((BaseFragment)localObject3).showDialog(new JoinGroupAlert(LaunchActivity.this, localException, LaunchActivity.9.this.val$group, (BaseFragment)localObject3));
                        return;
                      }
                      Object localObject3 = new AlertDialog.Builder(LaunchActivity.this);
                      ((AlertDialog.Builder)localObject3).setTitle(LocaleController.getString("AppName", 2131165319));
                      if (((!localException.megagroup) && (localException.channel)) || ((ChatObject.isChannel(localException.chat)) && (!localException.chat.megagroup)))
                      {
                        if (localException.chat != null);
                        for (localObject2 = localException.chat.title; ; localObject2 = ((TLRPC.ChatInvite)localObject2).title)
                        {
                          ((AlertDialog.Builder)localObject3).setMessage(LocaleController.formatString("ChannelJoinTo", 2131165471, new Object[] { localObject2 }));
                          ((AlertDialog.Builder)localObject3).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
                          {
                            public void onClick(DialogInterface paramDialogInterface, int paramInt)
                            {
                              LaunchActivity.this.runLinkRequest(LaunchActivity.9.this.val$username, LaunchActivity.9.this.val$group, LaunchActivity.9.this.val$sticker, LaunchActivity.9.this.val$botUser, LaunchActivity.9.this.val$botChat, LaunchActivity.9.this.val$message, LaunchActivity.9.this.val$hasUrl, LaunchActivity.9.this.val$messageId, LaunchActivity.9.this.val$game, 1);
                            }
                          });
                          ((AlertDialog.Builder)localObject3).setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
                          LaunchActivity.this.showAlertDialog((AlertDialog.Builder)localObject3);
                          return;
                        }
                      }
                      if (((TLRPC.ChatInvite)localObject2).chat != null);
                      for (localObject2 = ((TLRPC.ChatInvite)localObject2).chat.title; ; localObject2 = ((TLRPC.ChatInvite)localObject2).title)
                      {
                        ((AlertDialog.Builder)localObject3).setMessage(LocaleController.formatString("JoinToGroup", 2131165860, new Object[] { localObject2 }));
                        break;
                      }
                      localObject2 = new AlertDialog.Builder(LaunchActivity.this);
                      ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("AppName", 2131165319));
                      if (!this.val$error.text.startsWith("FLOOD_WAIT"))
                        break label589;
                    }
                    ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("FloodWait", 2131165715));
                    while (true)
                    {
                      ((AlertDialog.Builder)localObject2).setPositiveButton(LocaleController.getString("OK", 2131166153), null);
                      LaunchActivity.this.showAlertDialog((AlertDialog.Builder)localObject2);
                      return;
                      label589: ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("JoinToGroupErrorNotExist", 2131165862));
                    }
                  }
                });
              }
            }
            , 2);
            break;
          }
          i = j;
          if (paramInt != 1)
            break;
          paramString1 = new TLRPC.TL_messages_importChatInvite();
          paramString1.hash = paramString2;
          ConnectionsManager.getInstance().sendRequest(paramString1, new RequestDelegate(localAlertDialog)
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
              if (paramTL_error == null)
              {
                TLRPC.Updates localUpdates = (TLRPC.Updates)paramTLObject;
                MessagesController.getInstance().processUpdates(localUpdates, false);
              }
              AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
              {
                public void run()
                {
                  if (!LaunchActivity.this.isFinishing());
                  AlertDialog.Builder localBuilder;
                  try
                  {
                    LaunchActivity.10.this.val$progressDialog.dismiss();
                    if (this.val$error == null)
                    {
                      if (LaunchActivity.this.actionBarLayout != null)
                      {
                        Object localObject2 = (TLRPC.Updates)this.val$response;
                        if (!((TLRPC.Updates)localObject2).chats.isEmpty())
                        {
                          Object localObject1 = (TLRPC.Chat)((TLRPC.Updates)localObject2).chats.get(0);
                          ((TLRPC.Chat)localObject1).left = false;
                          ((TLRPC.Chat)localObject1).kicked = false;
                          MessagesController.getInstance().putUsers(((TLRPC.Updates)localObject2).users, false);
                          MessagesController.getInstance().putChats(((TLRPC.Updates)localObject2).chats, false);
                          localObject2 = new Bundle();
                          ((Bundle)localObject2).putInt("chat_id", ((TLRPC.Chat)localObject1).id);
                          if ((LaunchActivity.mainFragmentsStack.isEmpty()) || (MessagesController.checkCanOpenChat((Bundle)localObject2, (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))))
                          {
                            localObject1 = new ChatActivity((Bundle)localObject2);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject1, false, true, true);
                          }
                        }
                      }
                      return;
                    }
                  }
                  catch (Exception localBuilder)
                  {
                    while (true)
                      FileLog.e(localException);
                    localBuilder = new AlertDialog.Builder(LaunchActivity.this);
                    localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
                    if (!this.val$error.text.startsWith("FLOOD_WAIT"))
                      break label285;
                  }
                  localBuilder.setMessage(LocaleController.getString("FloodWait", 2131165715));
                  while (true)
                  {
                    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
                    LaunchActivity.this.showAlertDialog(localBuilder);
                    return;
                    label285: if (this.val$error.text.equals("USERS_TOO_MUCH"))
                    {
                      localBuilder.setMessage(LocaleController.getString("JoinToGroupErrorFull", 2131165861));
                      continue;
                    }
                    localBuilder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", 2131165862));
                  }
                }
              });
            }
          }
          , 2);
          i = j;
          break;
        }
        if (paramString3 == null)
          break label295;
      }
      while (mainFragmentsStack.isEmpty());
      paramString1 = new TLRPC.TL_inputStickerSetShortName();
      paramString1.short_name = paramString3;
      paramString2 = (BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1);
      paramString2.showDialog(new StickersAlert(this, paramString2, paramString1, null, null));
      return;
      label295: i = j;
      if (paramString6 == null)
        continue;
      paramString1 = new Bundle();
      paramString1.putBoolean("onlySelect", true);
      paramString1 = new DialogsActivity(paramString1);
      paramString1.setDelegate(new DialogsActivity.DialogsActivityDelegate(paramBoolean, paramString6)
      {
        public void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean)
        {
          Bundle localBundle = new Bundle();
          localBundle.putBoolean("scrollToTopOnResume", true);
          localBundle.putBoolean("hasUrl", this.val$hasUrl);
          int i = (int)paramLong;
          int j = (int)(paramLong >> 32);
          if (i != 0)
            if (j == 1)
              localBundle.putInt("chat_id", i);
          while (true)
          {
            if (MessagesController.checkCanOpenChat(localBundle, paramDialogsActivity))
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
              DraftQuery.saveDraft(paramLong, this.val$message, null, null, true);
              LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(localBundle), true, false, true);
            }
            return;
            if (i > 0)
            {
              localBundle.putInt("user_id", i);
              continue;
            }
            if (i >= 0)
              continue;
            localBundle.putInt("chat_id", -i);
            continue;
            localBundle.putInt("enc_id", j);
          }
        }
      });
      presentFragment(paramString1, false, true);
      i = j;
    }
  }

  private void sendLocation()
  {
    itman.Vidofilm.d.c.a(this).a();
  }

  private void showPasscodeActivity()
  {
    if (this.passcodeView == null)
      return;
    UserConfig.appLocked = true;
    if (PhotoViewer.getInstance().isVisible())
      PhotoViewer.getInstance().closePhoto(false, true);
    while (true)
    {
      this.passcodeView.onShow();
      UserConfig.isWaitingForPasscodeEnter = true;
      this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
      this.passcodeView.setDelegate(new PasscodeView.PasscodeViewDelegate()
      {
        public void didAcceptedPassword()
        {
          UserConfig.isWaitingForPasscodeEnter = false;
          if (LaunchActivity.this.passcodeSaveIntent != null)
          {
            LaunchActivity.this.handleIntent(LaunchActivity.this.passcodeSaveIntent, LaunchActivity.this.passcodeSaveIntentIsNew, LaunchActivity.this.passcodeSaveIntentIsRestore, true);
            LaunchActivity.access$802(LaunchActivity.this, null);
          }
          LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
          LaunchActivity.this.actionBarLayout.showLastFragment();
          if (AndroidUtilities.isTablet())
          {
            LaunchActivity.this.layersActionBarLayout.showLastFragment();
            LaunchActivity.this.rightActionBarLayout.showLastFragment();
          }
        }
      });
      return;
      if (!ArticleViewer.getInstance().isVisible())
        continue;
      ArticleViewer.getInstance().close(false, true);
    }
  }

  private void updateCurrentConnectionState()
  {
    String str = null;
    if (this.currentConnectionState == 2)
      str = LocaleController.getString("WaitingForNetwork", 2131166609);
    while (true)
    {
      this.actionBarLayout.setTitleOverlayText(str);
      return;
      if (this.currentConnectionState == 1)
      {
        str = LocaleController.getString("Connecting", 2131165571);
        continue;
      }
      if (this.currentConnectionState != 4)
        continue;
      str = LocaleController.getString("Updating", 2131166542);
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.appDidLogout)
    {
      if (this.drawerLayoutAdapter != null)
        this.drawerLayoutAdapter.notifyDataSetChanged();
      paramArrayOfObject = this.actionBarLayout.fragmentsStack.iterator();
      while (paramArrayOfObject.hasNext())
        ((BaseFragment)paramArrayOfObject.next()).onFragmentDestroy();
      this.actionBarLayout.fragmentsStack.clear();
      if (AndroidUtilities.isTablet())
      {
        paramArrayOfObject = this.layersActionBarLayout.fragmentsStack.iterator();
        while (paramArrayOfObject.hasNext())
          ((BaseFragment)paramArrayOfObject.next()).onFragmentDestroy();
        this.layersActionBarLayout.fragmentsStack.clear();
        paramArrayOfObject = this.rightActionBarLayout.fragmentsStack.iterator();
        while (paramArrayOfObject.hasNext())
          ((BaseFragment)paramArrayOfObject.next()).onFragmentDestroy();
        this.rightActionBarLayout.fragmentsStack.clear();
      }
      startActivity(new Intent(this, IntroActivity.class));
      onFinish();
      finish();
    }
    do
      while (true)
      {
        return;
        if (paramInt == NotificationCenter.closeOtherAppActivities)
        {
          if (paramArrayOfObject[0] == this)
            continue;
          onFinish();
          finish();
          return;
        }
        if (paramInt == NotificationCenter.didUpdatedConnectionState)
        {
          paramInt = ConnectionsManager.getInstance().getConnectionState();
          if (this.currentConnectionState == paramInt)
            continue;
          FileLog.d("switch to state " + paramInt);
          this.currentConnectionState = paramInt;
          updateCurrentConnectionState();
          return;
        }
        if (paramInt == NotificationCenter.mainUserInfoChanged)
        {
          this.drawerLayoutAdapter.notifyDataSetChanged();
          return;
        }
        Object localObject;
        if (paramInt == NotificationCenter.needShowAlert)
        {
          localObject = (Integer)paramArrayOfObject[0];
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
          localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
          localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
          if (((Integer)localObject).intValue() != 2)
            localBuilder.setNegativeButton(LocaleController.getString("MoreInfo", 2131165995), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                if (!LaunchActivity.mainFragmentsStack.isEmpty())
                  MessagesController.openByUserName("spambot", (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1), 1);
              }
            });
          if (((Integer)localObject).intValue() == 0)
            localBuilder.setMessage(LocaleController.getString("NobodyLikesSpam1", 2131166056));
          while (!mainFragmentsStack.isEmpty())
          {
            ((BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(localBuilder.create());
            return;
            if (((Integer)localObject).intValue() == 1)
            {
              localBuilder.setMessage(LocaleController.getString("NobodyLikesSpam2", 2131166057));
              continue;
            }
            if (((Integer)localObject).intValue() != 2)
              continue;
            localBuilder.setMessage((String)paramArrayOfObject[1]);
          }
          continue;
        }
        if (paramInt == NotificationCenter.wasUnableToFindCurrentLocation)
        {
          paramArrayOfObject = (HashMap)paramArrayOfObject[0];
          localObject = new AlertDialog.Builder(this);
          ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165319));
          ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166153), null);
          ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("ShareYouLocationUnableManually", 2131166460), new DialogInterface.OnClickListener(paramArrayOfObject)
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              if (LaunchActivity.mainFragmentsStack.isEmpty());
              do
                return;
              while (!AndroidUtilities.isGoogleMapsInstalled((BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1)));
              paramDialogInterface = new LocationActivity();
              paramDialogInterface.setDelegate(new LocationActivity.LocationActivityDelegate()
              {
                public void didSelectLocation(TLRPC.MessageMedia paramMessageMedia)
                {
                  Iterator localIterator = LaunchActivity.18.this.val$waitingForLocation.entrySet().iterator();
                  while (localIterator.hasNext())
                  {
                    MessageObject localMessageObject = (MessageObject)((Map.Entry)localIterator.next()).getValue();
                    SendMessagesHelper.getInstance().sendMessage(paramMessageMedia, localMessageObject.getDialogId(), localMessageObject, null, null);
                  }
                }
              });
              LaunchActivity.this.presentFragment(paramDialogInterface);
            }
          });
          ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("ShareYouLocationUnable", 2131166459));
          if (mainFragmentsStack.isEmpty())
            continue;
          ((BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(((AlertDialog.Builder)localObject).create());
          return;
        }
        if (paramInt != NotificationCenter.didSetNewWallpapper)
          break;
        if (this.sideMenu == null)
          continue;
        paramArrayOfObject = this.sideMenu.getChildAt(0);
        if (paramArrayOfObject == null)
          continue;
        paramArrayOfObject.invalidate();
        return;
      }
    while (paramInt != NotificationCenter.didSetPasscode);
    if ((UserConfig.passcodeHash.length() > 0) && (!UserConfig.allowScreenCapture))
      try
      {
        getWindow().setFlags(8192, 8192);
        return;
      }
      catch (Exception paramArrayOfObject)
      {
        FileLog.e(paramArrayOfObject);
        return;
      }
    try
    {
      getWindow().clearFlags(8192);
      return;
    }
    catch (Exception paramArrayOfObject)
    {
      FileLog.e(paramArrayOfObject);
    }
  }

  public void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean)
  {
    int i;
    int j;
    if (paramLong != 0L)
    {
      i = (int)paramLong;
      j = (int)(paramLong >> 32);
      localObject1 = new Bundle();
      ((Bundle)localObject1).putBoolean("scrollToTopOnResume", true);
      if (!AndroidUtilities.isTablet())
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
      if (i == 0)
        break label122;
      if (j != 1)
        break label85;
      ((Bundle)localObject1).putInt("chat_id", i);
    }
    while (!MessagesController.checkCanOpenChat((Bundle)localObject1, paramDialogsActivity))
    {
      return;
      label85: if (i > 0)
      {
        ((Bundle)localObject1).putInt("user_id", i);
        continue;
      }
      if (i >= 0)
        continue;
      ((Bundle)localObject1).putInt("chat_id", -i);
      continue;
      label122: ((Bundle)localObject1).putInt("enc_id", j);
    }
    Object localObject1 = new ChatActivity((Bundle)localObject1);
    if (this.videoPath != null)
    {
      if (Build.VERSION.SDK_INT >= 16)
      {
        if (AndroidUtilities.isTablet())
          if (this.tabletFullSize)
          {
            this.actionBarLayout.presentFragment((BaseFragment)localObject1, false, true, false);
            localObject2 = this.videoPath;
            if (paramDialogsActivity == null)
              break label346;
            paramBoolean = true;
            label200: if ((!((ChatActivity)localObject1).openVideoEditor((String)localObject2, paramBoolean, false)) && (!AndroidUtilities.isTablet()))
            {
              if (paramDialogsActivity == null)
                break label352;
              paramDialogsActivity.finishFragment(true);
            }
          }
        while (true)
        {
          this.photoPathsArray = null;
          this.videoPath = null;
          this.sendingText = null;
          this.documentsPathsArray = null;
          this.documentsOriginalPathsArray = null;
          this.contactsToSend = null;
          return;
          this.rightActionBarLayout.removeAllFragments();
          this.rightActionBarLayout.addFragmentToStack((BaseFragment)localObject1);
          this.rightActionBarLayout.setVisibility(0);
          this.rightActionBarLayout.showLastFragment();
          break;
          localObject2 = this.actionBarLayout;
          if (paramDialogsActivity != null);
          for (i = this.actionBarLayout.fragmentsStack.size() - 1; ; i = this.actionBarLayout.fragmentsStack.size())
          {
            ((ActionBarLayout)localObject2).addFragmentToStack((BaseFragment)localObject1, i);
            break;
          }
          label346: paramBoolean = false;
          break label200;
          label352: this.actionBarLayout.showLastFragment();
        }
      }
      localObject2 = this.actionBarLayout;
      if (paramDialogsActivity != null)
      {
        paramBoolean = true;
        label375: if (paramDialogsActivity != null)
          break label419;
      }
      label419: for (bool = true; ; bool = false)
      {
        ((ActionBarLayout)localObject2).presentFragment((BaseFragment)localObject1, paramBoolean, bool, true);
        SendMessagesHelper.prepareSendingVideo(this.videoPath, 0L, 0L, 0, 0, null, paramLong, null, null);
        break;
        paramBoolean = false;
        break label375;
      }
    }
    Object localObject2 = this.actionBarLayout;
    if (paramDialogsActivity != null)
    {
      paramBoolean = true;
      label438: if (paramDialogsActivity != null)
        break label660;
    }
    label660: for (boolean bool = true; ; bool = false)
    {
      ((ActionBarLayout)localObject2).presentFragment((BaseFragment)localObject1, paramBoolean, bool, true);
      if (this.photoPathsArray != null)
      {
        localObject1 = null;
        paramDialogsActivity = (DialogsActivity)localObject1;
        if (this.sendingText != null)
        {
          paramDialogsActivity = (DialogsActivity)localObject1;
          if (this.sendingText.length() <= 200)
          {
            paramDialogsActivity = (DialogsActivity)localObject1;
            if (this.photoPathsArray.size() == 1)
            {
              paramDialogsActivity = new ArrayList();
              paramDialogsActivity.add(this.sendingText);
              this.sendingText = null;
            }
          }
        }
        SendMessagesHelper.prepareSendingPhotos(null, this.photoPathsArray, paramLong, null, paramDialogsActivity, null, null);
      }
      if (this.sendingText != null)
        SendMessagesHelper.prepareSendingText(this.sendingText, paramLong);
      if ((this.documentsPathsArray != null) || (this.documentsUrisArray != null))
        SendMessagesHelper.prepareSendingDocuments(this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, this.documentsMimeType, paramLong, null, null);
      if ((this.contactsToSend == null) || (this.contactsToSend.isEmpty()))
        break;
      paramDialogsActivity = this.contactsToSend.iterator();
      while (paramDialogsActivity.hasNext())
      {
        localObject1 = (TLRPC.User)paramDialogsActivity.next();
        SendMessagesHelper.getInstance().sendMessage((TLRPC.User)localObject1, paramLong, null, null, null);
      }
      break;
      paramBoolean = false;
      break label438;
    }
  }

  public ActionBarLayout getActionBarLayout()
  {
    return this.actionBarLayout;
  }

  public ActionBarLayout getLayersActionBarLayout()
  {
    return this.layersActionBarLayout;
  }

  public ActionBarLayout getRightActionBarLayout()
  {
    return this.rightActionBarLayout;
  }

  public boolean needAddFragmentToStack(BaseFragment paramBaseFragment, ActionBarLayout paramActionBarLayout)
  {
    if (AndroidUtilities.isTablet())
    {
      DrawerLayoutContainer localDrawerLayoutContainer = this.drawerLayoutContainer;
      if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity)) && (this.layersActionBarLayout.getVisibility() != 0))
      {
        bool = true;
        localDrawerLayoutContainer.setAllowOpenDrawer(bool, true);
        if (!(paramBaseFragment instanceof DialogsActivity))
          break label154;
        if ((!((DialogsActivity)paramBaseFragment).isMainDialogList()) || (paramActionBarLayout == this.actionBarLayout))
          break label438;
        this.actionBarLayout.removeAllFragments();
        this.actionBarLayout.addFragmentToStack(paramBaseFragment);
        this.layersActionBarLayout.removeAllFragments();
        this.layersActionBarLayout.setVisibility(8);
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        if (!this.tabletFullSize)
        {
          this.shadowTabletSide.setVisibility(0);
          if (this.rightActionBarLayout.fragmentsStack.isEmpty())
            this.backgroundTablet.setVisibility(0);
        }
      }
      label154: 
      do
      {
        do
        {
          return false;
          bool = false;
          break;
          if (!(paramBaseFragment instanceof ChatActivity))
            break label357;
          if ((this.tabletFullSize) || (paramActionBarLayout == this.rightActionBarLayout))
            break label271;
          this.rightActionBarLayout.setVisibility(0);
          this.backgroundTablet.setVisibility(8);
          this.rightActionBarLayout.removeAllFragments();
          this.rightActionBarLayout.addFragmentToStack(paramBaseFragment);
        }
        while (this.layersActionBarLayout.fragmentsStack.isEmpty());
        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0)
          this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
        this.layersActionBarLayout.closeLastFragment(true);
        return false;
        if ((!this.tabletFullSize) || (paramActionBarLayout == this.actionBarLayout))
          break label438;
        this.actionBarLayout.addFragmentToStack(paramBaseFragment);
      }
      while (this.layersActionBarLayout.fragmentsStack.isEmpty());
      label271: 
      while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0)
        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
      this.layersActionBarLayout.closeLastFragment(true);
      return false;
      label357: if (paramActionBarLayout != this.layersActionBarLayout)
      {
        this.layersActionBarLayout.setVisibility(0);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
        if ((paramBaseFragment instanceof LoginActivity))
        {
          this.backgroundTablet.setVisibility(0);
          this.shadowTabletSide.setVisibility(8);
          this.shadowTablet.setBackgroundColor(0);
        }
        while (true)
        {
          this.layersActionBarLayout.addFragmentToStack(paramBaseFragment);
          return false;
          this.shadowTablet.setBackgroundColor(2130706432);
        }
      }
      label438: return true;
    }
    paramActionBarLayout = this.drawerLayoutContainer;
    if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity)));
    for (boolean bool = true; ; bool = false)
    {
      paramActionBarLayout.setAllowOpenDrawer(bool, false);
      return true;
    }
  }

  public boolean needCloseLastFragment(ActionBarLayout paramActionBarLayout)
  {
    if (AndroidUtilities.isTablet())
    {
      if ((paramActionBarLayout == this.actionBarLayout) && (paramActionBarLayout.fragmentsStack.size() <= 1))
      {
        onFinish();
        finish();
        return false;
      }
      if (paramActionBarLayout == this.rightActionBarLayout)
        if (!this.tabletFullSize)
          this.backgroundTablet.setVisibility(0);
    }
    while (true)
    {
      return true;
      if ((paramActionBarLayout != this.layersActionBarLayout) || (!this.actionBarLayout.fragmentsStack.isEmpty()) || (this.layersActionBarLayout.fragmentsStack.size() != 1))
        continue;
      onFinish();
      finish();
      return false;
      if (paramActionBarLayout.fragmentsStack.size() <= 1)
      {
        onFinish();
        finish();
        return false;
      }
      if ((paramActionBarLayout.fragmentsStack.size() < 2) || ((paramActionBarLayout.fragmentsStack.get(0) instanceof LoginActivity)))
        continue;
      this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
    }
  }

  public boolean needPresentFragment(BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2, ActionBarLayout paramActionBarLayout)
  {
    boolean bool5 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool2 = true;
    if (ArticleViewer.getInstance().isVisible())
      ArticleViewer.getInstance().close(false, true);
    if (AndroidUtilities.isTablet())
    {
      DrawerLayoutContainer localDrawerLayoutContainer = this.drawerLayoutContainer;
      boolean bool1;
      if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity)) && (this.layersActionBarLayout.getVisibility() != 0))
      {
        bool1 = true;
        localDrawerLayoutContainer.setAllowOpenDrawer(bool1, true);
        if ((!(paramBaseFragment instanceof DialogsActivity)) || (!((DialogsActivity)paramBaseFragment).isMainDialogList()) || (paramActionBarLayout == this.actionBarLayout))
          break label190;
        this.actionBarLayout.removeAllFragments();
        this.actionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
        this.layersActionBarLayout.removeAllFragments();
        this.layersActionBarLayout.setVisibility(8);
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        if (!this.tabletFullSize)
        {
          this.shadowTabletSide.setVisibility(0);
          if (this.rightActionBarLayout.fragmentsStack.isEmpty())
            this.backgroundTablet.setVisibility(0);
        }
      }
      label190: label357: 
      do
      {
        return false;
        bool1 = false;
        break;
        if (!(paramBaseFragment instanceof ChatActivity))
          break label735;
        if (((!this.tabletFullSize) && (paramActionBarLayout == this.rightActionBarLayout)) || ((this.tabletFullSize) && (paramActionBarLayout == this.actionBarLayout)))
        {
          if ((!this.tabletFullSize) || (paramActionBarLayout != this.actionBarLayout) || (this.actionBarLayout.fragmentsStack.size() != 1))
            paramBoolean1 = true;
          while (!this.layersActionBarLayout.fragmentsStack.isEmpty())
          {
            while (true)
              if (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0)
              {
                this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
                continue;
                paramBoolean1 = false;
                break;
              }
            paramActionBarLayout = this.layersActionBarLayout;
            if (paramBoolean2)
              break label357;
          }
          for (bool1 = bool2; ; bool1 = false)
          {
            paramActionBarLayout.closeLastFragment(bool1);
            if (!paramBoolean1)
              this.actionBarLayout.presentFragment(paramBaseFragment, false, paramBoolean2, false);
            return paramBoolean1;
          }
        }
        if ((this.tabletFullSize) || (paramActionBarLayout == this.rightActionBarLayout))
          break label491;
        this.rightActionBarLayout.setVisibility(0);
        this.backgroundTablet.setVisibility(8);
        this.rightActionBarLayout.removeAllFragments();
        this.rightActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, true, false);
      }
      while (this.layersActionBarLayout.fragmentsStack.isEmpty());
      while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0)
        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
      paramBaseFragment = this.layersActionBarLayout;
      if (!paramBoolean2);
      for (paramBoolean1 = bool5; ; paramBoolean1 = false)
      {
        paramBaseFragment.closeLastFragment(paramBoolean1);
        return false;
      }
      label491: if ((this.tabletFullSize) && (paramActionBarLayout != this.actionBarLayout))
      {
        paramActionBarLayout = this.actionBarLayout;
        if (this.actionBarLayout.fragmentsStack.size() > 1);
        for (paramBoolean1 = true; ; paramBoolean1 = false)
        {
          paramActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
          if (this.layersActionBarLayout.fragmentsStack.isEmpty())
            break;
          while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0)
            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
        }
        paramBaseFragment = this.layersActionBarLayout;
        if (!paramBoolean2);
        for (paramBoolean1 = bool3; ; paramBoolean1 = false)
        {
          paramBaseFragment.closeLastFragment(paramBoolean1);
          return false;
        }
      }
      if (!this.layersActionBarLayout.fragmentsStack.isEmpty())
      {
        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0)
          this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
        paramActionBarLayout = this.layersActionBarLayout;
        if (!paramBoolean2)
        {
          paramBoolean1 = true;
          paramActionBarLayout.closeLastFragment(paramBoolean1);
        }
      }
      else
      {
        paramActionBarLayout = this.actionBarLayout;
        if (this.actionBarLayout.fragmentsStack.size() <= 1)
          break label730;
      }
      label730: for (paramBoolean1 = bool4; ; paramBoolean1 = false)
      {
        paramActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
        return false;
        paramBoolean1 = false;
        break;
      }
      label735: if (paramActionBarLayout != this.layersActionBarLayout)
      {
        this.layersActionBarLayout.setVisibility(0);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
        if ((paramBaseFragment instanceof LoginActivity))
        {
          this.backgroundTablet.setVisibility(0);
          this.shadowTabletSide.setVisibility(8);
          this.shadowTablet.setBackgroundColor(0);
        }
        while (true)
        {
          this.layersActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
          return false;
          this.shadowTablet.setBackgroundColor(2130706432);
        }
      }
      return true;
    }
    paramActionBarLayout = this.drawerLayoutContainer;
    if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity)));
    for (paramBoolean1 = true; ; paramBoolean1 = false)
    {
      paramActionBarLayout.setAllowOpenDrawer(paramBoolean1, false);
      return true;
    }
  }

  public void onActionModeFinished(ActionMode paramActionMode)
  {
    super.onActionModeFinished(paramActionMode);
    if ((Build.VERSION.SDK_INT >= 23) && (paramActionMode.getType() == 1));
    do
    {
      return;
      this.actionBarLayout.onActionModeFinished(paramActionMode);
    }
    while (!AndroidUtilities.isTablet());
    this.rightActionBarLayout.onActionModeFinished(paramActionMode);
    this.layersActionBarLayout.onActionModeFinished(paramActionMode);
  }

  public void onActionModeStarted(ActionMode paramActionMode)
  {
    super.onActionModeStarted(paramActionMode);
    try
    {
      Menu localMenu = paramActionMode.getMenu();
      if ((localMenu != null) && (!this.actionBarLayout.extendActionMode(localMenu)) && (AndroidUtilities.isTablet()) && (!this.rightActionBarLayout.extendActionMode(localMenu)))
        this.layersActionBarLayout.extendActionMode(localMenu);
      if ((Build.VERSION.SDK_INT >= 23) && (paramActionMode.getType() == 1))
        return;
    }
    catch (Exception localException)
    {
      do
      {
        while (true)
          FileLog.e(localException);
        this.actionBarLayout.onActionModeStarted(paramActionMode);
      }
      while (!AndroidUtilities.isTablet());
      this.rightActionBarLayout.onActionModeStarted(paramActionMode);
      this.layersActionBarLayout.onActionModeStarted(paramActionMode);
    }
  }

  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((UserConfig.passcodeHash.length() != 0) && (UserConfig.lastPauseTime != 0))
    {
      UserConfig.lastPauseTime = 0;
      UserConfig.saveConfig(false);
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    ThemeEditorView localThemeEditorView = ThemeEditorView.getInstance();
    if (localThemeEditorView != null)
      localThemeEditorView.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (this.actionBarLayout.fragmentsStack.size() != 0)
      ((BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(paramInt1, paramInt2, paramIntent);
    if (AndroidUtilities.isTablet())
    {
      if (this.rightActionBarLayout.fragmentsStack.size() != 0)
        ((BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(paramInt1, paramInt2, paramIntent);
      if (this.layersActionBarLayout.fragmentsStack.size() != 0)
        ((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(paramInt1, paramInt2, paramIntent);
    }
  }

  public void onBackPressed()
  {
    int j = 0;
    if (this.passcodeView.getVisibility() == 0)
      finish();
    while (true)
    {
      return;
      if (PhotoViewer.getInstance().isVisible())
      {
        PhotoViewer.getInstance().closePhoto(true, false);
        return;
      }
      if (ArticleViewer.getInstance().isVisible())
      {
        ArticleViewer.getInstance().close(true, false);
        return;
      }
      if (this.drawerLayoutContainer.isDrawerOpened())
      {
        this.drawerLayoutContainer.closeDrawer(false);
        return;
      }
      if (!AndroidUtilities.isTablet())
        break;
      if (this.layersActionBarLayout.getVisibility() == 0)
      {
        this.layersActionBarLayout.onBackPressed();
        return;
      }
      int i = j;
      if (this.rightActionBarLayout.getVisibility() == 0)
      {
        i = j;
        if (!this.rightActionBarLayout.fragmentsStack.isEmpty())
          if (((BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onBackPressed())
            break label168;
      }
      label168: for (i = 1; i == 0; i = 0)
      {
        this.actionBarLayout.onBackPressed();
        return;
      }
    }
    this.actionBarLayout.onBackPressed();
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    AndroidUtilities.checkDisplaySize(this, paramConfiguration);
    super.onConfigurationChanged(paramConfiguration);
    checkLayout();
    EmbedBottomSheet localEmbedBottomSheet = EmbedBottomSheet.getInstance();
    if (localEmbedBottomSheet != null)
      localEmbedBottomSheet.onConfigurationChanged(paramConfiguration);
    paramConfiguration = ThemeEditorView.getInstance();
    if (paramConfiguration != null)
      paramConfiguration.onConfigurationChanged();
  }

  protected void onCreate(Bundle paramBundle)
  {
    boolean bool3 = true;
    ApplicationLoader.postInitApplication();
    NativeCrashManager.handleDumpFiles(this);
    AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
    try
    {
      a.a.a.a.c.a(new c.a(this).a(new i[] { new com.b.a.a.a() }).a(true).a());
      Object localObject1;
      if (!UserConfig.isClientActivated())
      {
        localObject1 = getIntent();
        if ((localObject1 != null) && (((Intent)localObject1).getAction() != null) && (("android.intent.action.SEND".equals(((Intent)localObject1).getAction())) || (((Intent)localObject1).getAction().equals("android.intent.action.SEND_MULTIPLE"))))
        {
          super.onCreate(paramBundle);
          finish();
          label119: return;
        }
      }
      requestWindowFeature(1);
      setTheme(2131361942);
      getWindow().setBackgroundDrawableResource(2130838109);
      if ((UserConfig.passcodeHash.length() > 0) && (!UserConfig.allowScreenCapture));
      int i;
      Object localObject4;
      try
      {
        getWindow().setFlags(8192, 8192);
        super.onCreate(paramBundle);
        if ((UserConfig.isClientActivated()) && (paramBundle == null))
        {
          if (b.a(ApplicationLoader.applicationContext).i().a() == null)
            new org.vidogram.VidogramUi.WebRTC.e(ApplicationLoader.applicationContext).a(UserConfig.getCurrentUser().first_name, UserConfig.getCurrentUser().last_name, UserConfig.getCurrentUser().id + "", UserConfig.getCurrentUser().phone);
          d.a(ApplicationLoader.applicationContext).a(false);
          itman.Vidofilm.a.a(this).a();
          sendLocation();
          itman.Vidofilm.d.e.a(ApplicationLoader.applicationContext).b();
          new org.vidogram.VidogramUi.WebRTC.e(this).a();
        }
        if (Build.VERSION.SDK_INT >= 24)
          AndroidUtilities.isInMultiwindow = isInMultiWindowMode();
        Theme.createChatResources(this, false);
        if ((UserConfig.passcodeHash.length() != 0) && (UserConfig.appLocked))
          UserConfig.lastPauseTime = ConnectionsManager.getInstance().getCurrentTime();
        i = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (i > 0)
          AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(i);
        this.actionBarLayout = new ActionBarLayout(this);
        this.drawerLayoutContainer = new DrawerLayoutContainer(this);
        setContentView(this.drawerLayoutContainer, new ViewGroup.LayoutParams(-1, -1));
        if (AndroidUtilities.isTablet())
        {
          getWindow().setSoftInputMode(16);
          localObject1 = new RelativeLayout(this)
          {
            private boolean inLayout;

            protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
            {
              int i = paramInt3 - paramInt1;
              if ((!AndroidUtilities.isInMultiwindow) && ((!AndroidUtilities.isSmallTablet()) || (getResources().getConfiguration().orientation == 2)))
              {
                paramInt3 = i / 100 * 35;
                paramInt1 = paramInt3;
                if (paramInt3 < AndroidUtilities.dp(320.0F))
                  paramInt1 = AndroidUtilities.dp(320.0F);
                LaunchActivity.this.shadowTabletSide.layout(paramInt1, 0, LaunchActivity.this.shadowTabletSide.getMeasuredWidth() + paramInt1, LaunchActivity.this.shadowTabletSide.getMeasuredHeight());
                LaunchActivity.this.actionBarLayout.layout(0, 0, LaunchActivity.this.actionBarLayout.getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getMeasuredHeight());
                LaunchActivity.this.rightActionBarLayout.layout(paramInt1, 0, LaunchActivity.this.rightActionBarLayout.getMeasuredWidth() + paramInt1, LaunchActivity.this.rightActionBarLayout.getMeasuredHeight());
              }
              while (true)
              {
                paramInt1 = (i - LaunchActivity.this.layersActionBarLayout.getMeasuredWidth()) / 2;
                paramInt2 = (paramInt4 - paramInt2 - LaunchActivity.this.layersActionBarLayout.getMeasuredHeight()) / 2;
                LaunchActivity.this.layersActionBarLayout.layout(paramInt1, paramInt2, LaunchActivity.this.layersActionBarLayout.getMeasuredWidth() + paramInt1, LaunchActivity.this.layersActionBarLayout.getMeasuredHeight() + paramInt2);
                LaunchActivity.this.backgroundTablet.layout(0, 0, LaunchActivity.this.backgroundTablet.getMeasuredWidth(), LaunchActivity.this.backgroundTablet.getMeasuredHeight());
                LaunchActivity.this.shadowTablet.layout(0, 0, LaunchActivity.this.shadowTablet.getMeasuredWidth(), LaunchActivity.this.shadowTablet.getMeasuredHeight());
                return;
                LaunchActivity.this.actionBarLayout.layout(0, 0, LaunchActivity.this.actionBarLayout.getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getMeasuredHeight());
              }
            }

            protected void onMeasure(int paramInt1, int paramInt2)
            {
              this.inLayout = true;
              int i = View.MeasureSpec.getSize(paramInt1);
              int j = View.MeasureSpec.getSize(paramInt2);
              setMeasuredDimension(i, j);
              if ((!AndroidUtilities.isInMultiwindow) && ((!AndroidUtilities.isSmallTablet()) || (getResources().getConfiguration().orientation == 2)))
              {
                LaunchActivity.access$002(LaunchActivity.this, false);
                paramInt2 = i / 100 * 35;
                paramInt1 = paramInt2;
                if (paramInt2 < AndroidUtilities.dp(320.0F))
                  paramInt1 = AndroidUtilities.dp(320.0F);
                LaunchActivity.this.actionBarLayout.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(j, 1073741824));
                LaunchActivity.this.shadowTabletSide.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(j, 1073741824));
                LaunchActivity.this.rightActionBarLayout.measure(View.MeasureSpec.makeMeasureSpec(i - paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(j, 1073741824));
              }
              while (true)
              {
                LaunchActivity.this.backgroundTablet.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(j, 1073741824));
                LaunchActivity.this.shadowTablet.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(j, 1073741824));
                LaunchActivity.this.layersActionBarLayout.measure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(530.0F), i), 1073741824), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(528.0F), j), 1073741824));
                this.inLayout = false;
                return;
                LaunchActivity.access$002(LaunchActivity.this, true);
                LaunchActivity.this.actionBarLayout.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(j, 1073741824));
              }
            }

            public void requestLayout()
            {
              if (this.inLayout)
                return;
              super.requestLayout();
            }
          };
          this.drawerLayoutContainer.addView((View)localObject1, LayoutHelper.createFrame(-1, -1.0F));
          this.backgroundTablet = new View(this);
          localObject4 = (BitmapDrawable)getResources().getDrawable(2130837662);
          ((BitmapDrawable)localObject4).setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
          this.backgroundTablet.setBackgroundDrawable((Drawable)localObject4);
          ((RelativeLayout)localObject1).addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
          ((RelativeLayout)localObject1).addView(this.actionBarLayout);
          this.rightActionBarLayout = new ActionBarLayout(this);
          this.rightActionBarLayout.init(rightFragmentsStack);
          this.rightActionBarLayout.setDelegate(this);
          ((RelativeLayout)localObject1).addView(this.rightActionBarLayout);
          this.shadowTabletSide = new FrameLayout(this);
          this.shadowTabletSide.setBackgroundColor(1076449908);
          ((RelativeLayout)localObject1).addView(this.shadowTabletSide);
          this.shadowTablet = new FrameLayout(this);
          localObject4 = this.shadowTablet;
          if (layerFragmentsStack.isEmpty())
          {
            i = 8;
            ((FrameLayout)localObject4).setVisibility(i);
            this.shadowTablet.setBackgroundColor(2130706432);
            ((RelativeLayout)localObject1).addView(this.shadowTablet);
            this.shadowTablet.setOnTouchListener(new View.OnTouchListener()
            {
              public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
              {
                if ((!LaunchActivity.this.actionBarLayout.fragmentsStack.isEmpty()) && (paramMotionEvent.getAction() == 1))
                {
                  float f1 = paramMotionEvent.getX();
                  float f2 = paramMotionEvent.getY();
                  paramView = new int[2];
                  LaunchActivity.this.layersActionBarLayout.getLocationOnScreen(paramView);
                  int i = paramView[0];
                  int j = paramView[1];
                  if ((LaunchActivity.this.layersActionBarLayout.checkTransitionAnimation()) || ((f1 > i) && (f1 < i + LaunchActivity.this.layersActionBarLayout.getWidth()) && (f2 > j) && (f2 < LaunchActivity.this.layersActionBarLayout.getHeight() + j)))
                    return false;
                  if (!LaunchActivity.this.layersActionBarLayout.fragmentsStack.isEmpty())
                  {
                    while (LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1 > 0)
                      LaunchActivity.this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)LaunchActivity.this.layersActionBarLayout.fragmentsStack.get(0));
                    LaunchActivity.this.layersActionBarLayout.closeLastFragment(true);
                  }
                  return true;
                }
                return false;
              }
            });
            this.shadowTablet.setOnClickListener(new View.OnClickListener()
            {
              public void onClick(View paramView)
              {
              }
            });
            this.layersActionBarLayout = new ActionBarLayout(this);
            this.layersActionBarLayout.setRemoveActionBarExtraHeight(true);
            this.layersActionBarLayout.setBackgroundView(this.shadowTablet);
            this.layersActionBarLayout.setUseAlphaAnimations(true);
            this.layersActionBarLayout.setBackgroundResource(2130837651);
            this.layersActionBarLayout.init(layerFragmentsStack);
            this.layersActionBarLayout.setDelegate(this);
            this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
            localObject4 = this.layersActionBarLayout;
            if (!layerFragmentsStack.isEmpty())
              break label1239;
            i = 8;
            ((ActionBarLayout)localObject4).setVisibility(i);
            ((RelativeLayout)localObject1).addView(this.layersActionBarLayout);
            this.sideMenu = new RecyclerListView(this);
            this.sideMenu.setBackgroundColor(Theme.getColor("chats_menuBackground"));
            this.sideMenu.setLayoutManager(new LinearLayoutManager(this, 1, false));
            localObject1 = this.sideMenu;
            localObject4 = new DrawerLayoutAdapter(this);
            this.drawerLayoutAdapter = ((DrawerLayoutAdapter)localObject4);
            ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject4);
            this.drawerLayoutContainer.setDrawerLayout(this.sideMenu);
            localObject1 = (FrameLayout.LayoutParams)this.sideMenu.getLayoutParams();
            localObject4 = AndroidUtilities.getRealScreenSize();
            if (!AndroidUtilities.isTablet())
              break label1267;
            i = AndroidUtilities.dp(320.0F);
            ((FrameLayout.LayoutParams)localObject1).width = i;
            ((FrameLayout.LayoutParams)localObject1).height = -1;
            this.sideMenu.setLayoutParams((ViewGroup.LayoutParams)localObject1);
            this.sideMenu.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
            {
              public void onItemClick(View paramView, int paramInt)
              {
                int i = LaunchActivity.this.drawerLayoutAdapter.getId(paramInt);
                if (paramInt == 0)
                {
                  paramView = new Bundle();
                  paramView.putInt("user_id", UserConfig.getClientUserId());
                  LaunchActivity.this.presentFragment(new ChatActivity(paramView));
                  LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                }
                do
                {
                  while (true)
                  {
                    return;
                    if (i == 2)
                    {
                      if (!MessagesController.isFeatureEnabled("chat_create", (BaseFragment)LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1)))
                        continue;
                      LaunchActivity.this.presentFragment(new GroupCreateActivity());
                      LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                      return;
                    }
                    if (i == 3)
                    {
                      paramView = new Bundle();
                      paramView.putBoolean("onlyUsers", true);
                      paramView.putBoolean("destroyAfterSelect", true);
                      paramView.putBoolean("createSecretChat", true);
                      paramView.putBoolean("allowBots", false);
                      LaunchActivity.this.presentFragment(new ContactsActivity(paramView));
                      LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                      return;
                    }
                    if (i != 4)
                      break;
                    if (!MessagesController.isFeatureEnabled("broadcast_create", (BaseFragment)LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1)))
                      continue;
                    paramView = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if ((!BuildVars.DEBUG_VERSION) && (paramView.getBoolean("channel_intro", false)))
                    {
                      paramView = new Bundle();
                      paramView.putInt("step", 0);
                      LaunchActivity.this.presentFragment(new ChannelCreateActivity(paramView));
                    }
                    while (true)
                    {
                      LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                      return;
                      LaunchActivity.this.presentFragment(new ChannelIntroActivity());
                      paramView.edit().putBoolean("channel_intro", true).commit();
                    }
                  }
                  if (i == 6)
                  {
                    LaunchActivity.this.presentFragment(new ContactsActivity(null));
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                    return;
                  }
                  if (i == 7)
                    try
                    {
                      paramView = new Intent("android.intent.action.SEND");
                      paramView.setType("text/plain");
                      paramView.putExtra("android.intent.extra.TEXT", ContactsController.getInstance().getInviteText());
                      LaunchActivity.this.startActivityForResult(Intent.createChooser(paramView, LocaleController.getString("InviteFriends", 2131165844)), 500);
                      LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                      return;
                    }
                    catch (Exception paramView)
                    {
                      while (true)
                        FileLog.e(paramView);
                    }
                  if (i == 8)
                  {
                    LaunchActivity.this.presentFragment(new SettingsActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                    return;
                  }
                  if (i == 9)
                  {
                    Browser.openUrl(LaunchActivity.this, LocaleController.getString("TelegramFaqUrl", 2131166503));
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                    return;
                  }
                  if (i != 10)
                    continue;
                  LaunchActivity.this.presentFragment(new CallLogActivity());
                  LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                  return;
                }
                while (i != 11);
                paramView = new Bundle();
                paramView.putBoolean("findId", true);
                LaunchActivity.this.presentFragment(new ChangeUsernameActivity(paramView));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
              }
            });
            this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
            this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
            this.actionBarLayout.init(mainFragmentsStack);
            this.actionBarLayout.setDelegate(this);
            Theme.loadWallpaper();
            this.passcodeView = new PasscodeView(this);
            this.drawerLayoutContainer.addView(this.passcodeView, LayoutHelper.createFrame(-1, -1.0F));
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, new Object[] { this });
            this.currentConnectionState = ConnectionsManager.getInstance().getConnectionState();
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.mainUserInfoChanged);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeOtherAppActivities);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didUpdatedConnectionState);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
            if (!this.actionBarLayout.fragmentsStack.isEmpty())
              break label2021;
            if (UserConfig.isClientActivated())
              break label1616;
            if (!b.a(getApplicationContext()).d())
              break label1598;
            localObject1 = getIntent();
            if ((localObject1 == null) || (((Intent)localObject1).getBooleanExtra("fromIntro", false)) || (!ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().isEmpty()))
              break label1300;
            startActivity(new Intent(this, IntroActivity.class));
            super.onCreate(paramBundle);
            finish();
            return;
          }
        }
      }
      catch (Exception localException1)
      {
        while (true)
        {
          FileLog.e(localException1);
          continue;
          i = 0;
          continue;
          label1239: i = 0;
          continue;
          this.drawerLayoutContainer.addView(this.actionBarLayout, new ViewGroup.LayoutParams(-1, -1));
          continue;
          label1267: i = Math.min(AndroidUtilities.dp(320.0F), Math.min(((Point)localObject4).x, ((Point)localObject4).y) - AndroidUtilities.dp(56.0F));
        }
        label1300: this.actionBarLayout.addFragmentToStack(new LoginActivity());
      }
      label1315: this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
      label1324: if (paramBundle != null);
      while (true)
      {
        try
        {
          while (true)
          {
            localObject2 = paramBundle.getString("fragment");
            if (localObject2 != null)
            {
              localObject4 = paramBundle.getBundle("args");
              i = ((String)localObject2).hashCode();
            }
            switch (i)
            {
            default:
              i = -1;
              switch (i)
              {
              default:
                checkLayout();
                localObject2 = getIntent();
                if (paramBundle == null)
                  break label2191;
                bool1 = bool3;
                handleIntent((Intent)localObject2, false, bool1, false);
                try
                {
                  paramBundle = Build.DISPLAY;
                  localObject2 = Build.USER;
                  if (paramBundle == null)
                    break label2196;
                  paramBundle = paramBundle.toLowerCase();
                  if (localObject2 == null)
                    break label2203;
                  localObject2 = paramBundle.toLowerCase();
                  if ((!paramBundle.contains("flyme")) && (!((String)localObject2).contains("flyme")))
                    break label119;
                  AndroidUtilities.incorrectDisplaySizeFix = true;
                  localObject2 = getWindow().getDecorView().getRootView();
                  paramBundle = ((View)localObject2).getViewTreeObserver();
                  localObject2 = new ViewTreeObserver.OnGlobalLayoutListener((View)localObject2)
                  {
                    public void onGlobalLayout()
                    {
                      int j = this.val$view.getMeasuredHeight();
                      int i = j;
                      if (Build.VERSION.SDK_INT >= 21)
                        i = j - AndroidUtilities.statusBarHeight;
                      if ((i > AndroidUtilities.dp(100.0F)) && (i < AndroidUtilities.displaySize.y) && (AndroidUtilities.dp(100.0F) + i > AndroidUtilities.displaySize.y))
                      {
                        AndroidUtilities.displaySize.y = i;
                        FileLog.e("fix display size y to " + AndroidUtilities.displaySize.y);
                      }
                    }
                  };
                  this.onGlobalLayoutListener = ((ViewTreeObserver.OnGlobalLayoutListener)localObject2);
                  paramBundle.addOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)localObject2);
                  return;
                }
                catch (Exception paramBundle)
                {
                  FileLog.e(paramBundle);
                  return;
                }
              case 0:
              case 1:
              case 2:
              case 3:
              case 4:
              case 5:
              case 6:
              }
            case 3052376:
            case 1434631203:
            case 98629247:
            case 738950403:
            case 3108362:
            case -1349522494:
            case -1529105743:
            }
          }
          label1598: this.actionBarLayout.addFragmentToStack(new LanguageSelectActivity());
          break label1315;
          label1616: Object localObject2 = new DialogsActivity(null);
          ((DialogsActivity)localObject2).setSideMenu(this.sideMenu);
          this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2);
          this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
          break label1324;
          if (!((String)localObject2).equals("chat"))
            continue;
          i = 0;
          continue;
          if (!((String)localObject2).equals("settings"))
            continue;
          i = 1;
          continue;
          if (!((String)localObject2).equals("group"))
            continue;
          i = 2;
          continue;
          if (!((String)localObject2).equals("channel"))
            continue;
          i = 3;
          continue;
          if (!((String)localObject2).equals("edit"))
            continue;
          i = 4;
          continue;
          if (!((String)localObject2).equals("chat_profile"))
            continue;
          i = 5;
          continue;
          if (!((String)localObject2).equals("wallpapers"))
            continue;
          i = 6;
          continue;
          if (localObject4 == null)
            continue;
          localObject2 = new ChatActivity((Bundle)localObject4);
          if (!this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2))
            continue;
          ((ChatActivity)localObject2).restoreSelfArgs(paramBundle);
          continue;
        }
        catch (Exception localObject3)
        {
          FileLog.e(localException2);
          continue;
          localObject3 = new SettingsActivity();
          this.actionBarLayout.addFragmentToStack((BaseFragment)localObject3);
          ((SettingsActivity)localObject3).restoreSelfArgs(paramBundle);
          continue;
          if (localObject4 == null)
            continue;
          localObject3 = new GroupCreateFinalActivity((Bundle)localObject4);
          if (!this.actionBarLayout.addFragmentToStack((BaseFragment)localObject3))
            continue;
          ((GroupCreateFinalActivity)localObject3).restoreSelfArgs(paramBundle);
          continue;
          if (localObject4 == null)
            continue;
          localObject3 = new ChannelCreateActivity((Bundle)localObject4);
          if (!this.actionBarLayout.addFragmentToStack((BaseFragment)localObject3))
            continue;
          ((ChannelCreateActivity)localObject3).restoreSelfArgs(paramBundle);
          continue;
          if (localObject4 == null)
            continue;
          localObject3 = new ChannelEditActivity((Bundle)localObject4);
          if (!this.actionBarLayout.addFragmentToStack((BaseFragment)localObject3))
            continue;
          ((ChannelEditActivity)localObject3).restoreSelfArgs(paramBundle);
          continue;
          if (localObject4 == null)
            continue;
          localObject3 = new ProfileActivity((Bundle)localObject4);
          if (!this.actionBarLayout.addFragmentToStack((BaseFragment)localObject3))
            continue;
          ((ProfileActivity)localObject3).restoreSelfArgs(paramBundle);
          continue;
          localObject3 = new WallpapersActivity();
          this.actionBarLayout.addFragmentToStack((BaseFragment)localObject3);
          ((WallpapersActivity)localObject3).restoreSelfArgs(paramBundle);
          continue;
        }
        label2021: Object localObject3 = (BaseFragment)this.actionBarLayout.fragmentsStack.get(0);
        if ((localObject3 instanceof DialogsActivity))
          ((DialogsActivity)localObject3).setSideMenu(this.sideMenu);
        if (!AndroidUtilities.isTablet())
          break label2216;
        if ((this.actionBarLayout.fragmentsStack.size() <= 1) && (this.layersActionBarLayout.fragmentsStack.isEmpty()));
        for (boolean bool2 = true; ; bool2 = false)
        {
          bool1 = bool2;
          if (this.layersActionBarLayout.fragmentsStack.size() == 1)
          {
            bool1 = bool2;
            if ((this.layersActionBarLayout.fragmentsStack.get(0) instanceof LoginActivity))
              bool1 = false;
          }
          bool2 = bool1;
          if (this.actionBarLayout.fragmentsStack.size() == 1)
          {
            bool2 = bool1;
            if ((this.actionBarLayout.fragmentsStack.get(0) instanceof LoginActivity))
              bool2 = false;
          }
          this.drawerLayoutContainer.setAllowOpenDrawer(bool2, false);
          break;
        }
        label2191: bool1 = false;
        continue;
        label2196: paramBundle = "";
        continue;
        label2203: localObject3 = "";
      }
    }
    catch (Exception localException3)
    {
      while (true)
      {
        continue;
        label2216: boolean bool1 = true;
      }
    }
  }

  protected void onDestroy()
  {
    PhotoViewer.getInstance().destroyPhotoViewer();
    SecretPhotoViewer.getInstance().destroyPhotoViewer();
    ArticleViewer.getInstance().destroyArticleViewer();
    StickerPreviewViewer.getInstance().destroy();
    Theme.destroyResources();
    Object localObject = EmbedBottomSheet.getInstance();
    if (localObject != null)
      ((EmbedBottomSheet)localObject).destroy();
    localObject = ThemeEditorView.getInstance();
    if (localObject != null)
      ((ThemeEditorView)localObject).destroy();
    try
    {
      if (this.visibleDialog != null)
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
    }
    catch (Exception localException2)
    {
      try
      {
        if (this.onGlobalLayoutListener != null)
        {
          localObject = getWindow().getDecorView().getRootView();
          if (Build.VERSION.SDK_INT >= 16)
            break label124;
          ((View)localObject).getViewTreeObserver().removeGlobalOnLayoutListener(this.onGlobalLayoutListener);
        }
        while (true)
        {
          super.onDestroy();
          onFinish();
          return;
          localException1 = localException1;
          FileLog.e(localException1);
          break;
          label124: localException1.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
      }
      catch (Exception localException2)
      {
        while (true)
          FileLog.e(localException2);
      }
    }
  }

  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 82) && (!UserConfig.isWaitingForPasscodeEnter))
    {
      if (PhotoViewer.getInstance().isVisible())
        return super.onKeyUp(paramInt, paramKeyEvent);
      if (ArticleViewer.getInstance().isVisible())
        return super.onKeyUp(paramInt, paramKeyEvent);
      if (!AndroidUtilities.isTablet())
        break label139;
      if ((this.layersActionBarLayout.getVisibility() != 0) || (this.layersActionBarLayout.fragmentsStack.isEmpty()))
        break label90;
      this.layersActionBarLayout.onKeyUp(paramInt, paramKeyEvent);
    }
    while (true)
    {
      return super.onKeyUp(paramInt, paramKeyEvent);
      label90: if ((this.rightActionBarLayout.getVisibility() == 0) && (!this.rightActionBarLayout.fragmentsStack.isEmpty()))
      {
        this.rightActionBarLayout.onKeyUp(paramInt, paramKeyEvent);
        continue;
      }
      this.actionBarLayout.onKeyUp(paramInt, paramKeyEvent);
      continue;
      label139: if (this.actionBarLayout.fragmentsStack.size() == 1)
      {
        if (!this.drawerLayoutContainer.isDrawerOpened())
        {
          if (getCurrentFocus() != null)
            AndroidUtilities.hideKeyboard(getCurrentFocus());
          this.drawerLayoutContainer.openDrawer(false);
          continue;
        }
        this.drawerLayoutContainer.closeDrawer(false);
        continue;
      }
      this.actionBarLayout.onKeyUp(paramInt, paramKeyEvent);
    }
  }

  public void onLowMemory()
  {
    super.onLowMemory();
    this.actionBarLayout.onLowMemory();
    if (AndroidUtilities.isTablet())
    {
      this.rightActionBarLayout.onLowMemory();
      this.layersActionBarLayout.onLowMemory();
    }
  }

  public void onMultiWindowModeChanged(boolean paramBoolean)
  {
    AndroidUtilities.isInMultiwindow = paramBoolean;
    checkLayout();
  }

  protected void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    handleIntent(paramIntent, true, false, false);
  }

  protected void onPause()
  {
    super.onPause();
    ApplicationLoader.mainInterfacePaused = true;
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ApplicationLoader.mainInterfacePausedStageQueue = true;
        ApplicationLoader.mainInterfacePausedStageQueueTime = 0L;
      }
    });
    onPasscodePause();
    this.actionBarLayout.onPause();
    if (AndroidUtilities.isTablet())
    {
      this.rightActionBarLayout.onPause();
      this.layersActionBarLayout.onPause();
    }
    if (this.passcodeView != null)
      this.passcodeView.onPause();
    ConnectionsManager.getInstance().setAppPaused(true, false);
    AndroidUtilities.unregisterUpdates();
    if (PhotoViewer.getInstance().isVisible())
      PhotoViewer.getInstance().onPause();
  }

  public boolean onPreIme()
  {
    if (PhotoViewer.getInstance().isVisible())
    {
      PhotoViewer.getInstance().closePhoto(true, false);
      return true;
    }
    if (ArticleViewer.getInstance().isVisible())
    {
      ArticleViewer.getInstance().close(true, false);
      return true;
    }
    return false;
  }

  public void onRebuildAllFragments(ActionBarLayout paramActionBarLayout)
  {
    if ((AndroidUtilities.isTablet()) && (paramActionBarLayout == this.layersActionBarLayout))
    {
      this.rightActionBarLayout.rebuildAllFragmentViews(true);
      this.rightActionBarLayout.showLastFragment();
      this.actionBarLayout.rebuildAllFragmentViews(true);
      this.actionBarLayout.showLastFragment();
    }
    this.drawerLayoutAdapter.notifyDataSetChanged();
  }

  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt);
    int j;
    int i;
    if ((paramInt == 3) || (paramInt == 4) || (paramInt == 5) || (paramInt == 19) || (paramInt == 20))
    {
      j = 1;
      i = j;
      if (paramArrayOfInt.length > 0)
      {
        i = j;
        if (paramArrayOfInt[0] == 0)
          if (paramInt == 4)
            ImageLoader.getInstance().checkMediaPaths();
      }
    }
    do
    {
      do
      {
        do
        {
          return;
          if (paramInt != 5)
            continue;
          ContactsController.getInstance().readContacts();
          return;
        }
        while (paramInt == 3);
        if (paramInt != 19)
        {
          i = j;
          if (paramInt != 20);
        }
        else
        {
          i = 0;
        }
        if (i != 0)
        {
          paramArrayOfString = new AlertDialog.Builder(this);
          paramArrayOfString.setTitle(LocaleController.getString("AppName", 2131165319));
          if (paramInt == 3)
            paramArrayOfString.setMessage(LocaleController.getString("PermissionNoAudio", 2131166255));
          while (true)
          {
            paramArrayOfString.setNegativeButton(LocaleController.getString("PermissionOpenSettings", 2131166260), new DialogInterface.OnClickListener()
            {
              @TargetApi(9)
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                try
                {
                  paramDialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                  paramDialogInterface.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                  LaunchActivity.this.startActivity(paramDialogInterface);
                  return;
                }
                catch (Exception paramDialogInterface)
                {
                  FileLog.e(paramDialogInterface);
                }
              }
            });
            paramArrayOfString.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
            paramArrayOfString.show();
            return;
            if (paramInt == 4)
            {
              paramArrayOfString.setMessage(LocaleController.getString("PermissionStorage", 2131166261));
              continue;
            }
            if (paramInt == 5)
            {
              paramArrayOfString.setMessage(LocaleController.getString("PermissionContacts", 2131166253));
              continue;
            }
            if ((paramInt != 19) && (paramInt != 20))
              continue;
            paramArrayOfString.setMessage(LocaleController.getString("PermissionNoCamera", 2131166257));
          }
          if ((paramInt == 2) && (paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0))
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
        }
        if (this.actionBarLayout.fragmentsStack.size() == 0)
          continue;
        ((BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
      }
      while (!AndroidUtilities.isTablet());
      if (this.rightActionBarLayout.fragmentsStack.size() == 0)
        continue;
      ((BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
    }
    while (this.layersActionBarLayout.fragmentsStack.size() == 0);
    ((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
  }

  protected void onResume()
  {
    super.onResume();
    ApplicationLoader.mainInterfacePaused = false;
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ApplicationLoader.mainInterfacePausedStageQueue = false;
        ApplicationLoader.mainInterfacePausedStageQueueTime = System.currentTimeMillis();
      }
    });
    onPasscodeResume();
    if (this.passcodeView.getVisibility() != 0)
    {
      this.actionBarLayout.onResume();
      if (AndroidUtilities.isTablet())
      {
        this.rightActionBarLayout.onResume();
        this.layersActionBarLayout.onResume();
      }
    }
    while (true)
    {
      AndroidUtilities.checkForCrashes(this);
      AndroidUtilities.checkForUpdates(this);
      ConnectionsManager.getInstance().setAppPaused(false, false);
      updateCurrentConnectionState();
      if (PhotoViewer.getInstance().isVisible())
        PhotoViewer.getInstance().onResume();
      return;
      this.actionBarLayout.dismissDialogs();
      if (AndroidUtilities.isTablet())
      {
        this.rightActionBarLayout.dismissDialogs();
        this.layersActionBarLayout.dismissDialogs();
      }
      this.passcodeView.onResume();
    }
  }

  protected void onSaveInstanceState(Bundle paramBundle)
  {
    while (true)
    {
      Bundle localBundle;
      try
      {
        super.onSaveInstanceState(paramBundle);
        if (!AndroidUtilities.isTablet())
          continue;
        if (this.layersActionBarLayout.fragmentsStack.isEmpty())
          continue;
        localBaseFragment = (BaseFragment)this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1);
        if (localBaseFragment == null)
          break;
        localBundle = localBaseFragment.getArguments();
        if ((!(localBaseFragment instanceof ChatActivity)) || (localBundle == null))
          continue;
        paramBundle.putBundle("args", localBundle);
        paramBundle.putString("fragment", "chat");
        localBaseFragment.saveSelfArgs(paramBundle);
        return;
        if (this.rightActionBarLayout.fragmentsStack.isEmpty())
          continue;
        localBaseFragment = (BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1);
        continue;
        if (this.actionBarLayout.fragmentsStack.isEmpty())
          break label414;
        localBaseFragment = (BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
        continue;
        if (this.actionBarLayout.fragmentsStack.isEmpty())
          break label414;
        localBaseFragment = (BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
        continue;
        if ((localBaseFragment instanceof SettingsActivity))
        {
          paramBundle.putString("fragment", "settings");
          continue;
        }
      }
      catch (Exception paramBundle)
      {
        FileLog.e(paramBundle);
        return;
      }
      if (((localBaseFragment instanceof GroupCreateFinalActivity)) && (localBundle != null))
      {
        paramBundle.putBundle("args", localBundle);
        paramBundle.putString("fragment", "group");
        continue;
      }
      if ((localBaseFragment instanceof WallpapersActivity))
      {
        paramBundle.putString("fragment", "wallpapers");
        continue;
      }
      if (((localBaseFragment instanceof ProfileActivity)) && (((ProfileActivity)localBaseFragment).isChat()) && (localBundle != null))
      {
        paramBundle.putBundle("args", localBundle);
        paramBundle.putString("fragment", "chat_profile");
        continue;
      }
      if (((localBaseFragment instanceof ChannelCreateActivity)) && (localBundle != null) && (localBundle.getInt("step") == 0))
      {
        paramBundle.putBundle("args", localBundle);
        paramBundle.putString("fragment", "channel");
        continue;
      }
      if ((!(localBaseFragment instanceof ChannelEditActivity)) || (localBundle == null))
        continue;
      paramBundle.putBundle("args", localBundle);
      paramBundle.putString("fragment", "edit");
      continue;
      label414: BaseFragment localBaseFragment = null;
    }
  }

  protected void onStart()
  {
    super.onStart();
    Browser.bindCustomTabsService(this);
  }

  protected void onStop()
  {
    super.onStop();
    Browser.unbindCustomTabsService(this);
  }

  public void presentFragment(BaseFragment paramBaseFragment)
  {
    this.actionBarLayout.presentFragment(paramBaseFragment);
  }

  public boolean presentFragment(BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2)
  {
    return this.actionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, true);
  }

  public void rebuildAllFragments(boolean paramBoolean)
  {
    if (this.layersActionBarLayout != null)
    {
      this.layersActionBarLayout.rebuildAllFragmentViews(paramBoolean);
      this.layersActionBarLayout.showLastFragment();
      return;
    }
    this.actionBarLayout.rebuildAllFragmentViews(paramBoolean);
    this.actionBarLayout.showLastFragment();
  }

  public AlertDialog showAlertDialog(AlertDialog.Builder paramBuilder)
  {
    try
    {
      if (this.visibleDialog != null)
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
    }
    catch (Exception localException)
    {
      try
      {
        while (true)
        {
          this.visibleDialog = paramBuilder.show();
          this.visibleDialog.setCanceledOnTouchOutside(true);
          this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
          {
            public void onDismiss(DialogInterface paramDialogInterface)
            {
              LaunchActivity.access$1502(LaunchActivity.this, null);
            }
          });
          paramBuilder = this.visibleDialog;
          return paramBuilder;
          localException = localException;
          FileLog.e(localException);
        }
      }
      catch (Exception paramBuilder)
      {
        FileLog.e(paramBuilder);
      }
    }
    return null;
  }

  private class VcardData
  {
    String name;
    ArrayList<String> phones = new ArrayList();

    private VcardData()
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.LaunchActivity
 * JD-Core Version:    0.6.0
 */