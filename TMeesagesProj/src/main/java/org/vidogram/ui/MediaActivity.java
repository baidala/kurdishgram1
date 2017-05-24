package org.vidogram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.System;
import android.support.v4.content.FileProvider;
import android.text.TextUtils.TruncateAt;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.b.a.a.a;
import com.b.a.a.o;
import com.google.firebase.crash.FirebaseCrash;
import itman.Vidofilm.a.u;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.vidogram.VidogramUi.WebRTC.e;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.SendMessagesHelper;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.UserObject;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.VideoEditedInfo;
import org.vidogram.messenger.browser.Browser;
import org.vidogram.messenger.query.MessagesQuery;
import org.vidogram.messenger.query.SharedMediaQuery;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.messenger.time.FastDateFormat;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatFull;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import org.vidogram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import org.vidogram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import org.vidogram.tgnet.TLRPC.TL_messages_search;
import org.vidogram.tgnet.TLRPC.TL_webPageEmpty;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.WebPage;
import org.vidogram.tgnet.TLRPC.messages_Messages;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.vidogram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BackDrawable;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Cells.CheckBoxCell;
import org.vidogram.ui.Cells.GraySectionCell;
import org.vidogram.ui.Cells.LoadingCell;
import org.vidogram.ui.Cells.SharedDocumentCell;
import org.vidogram.ui.Cells.SharedLinkCell;
import org.vidogram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate;
import org.vidogram.ui.Cells.SharedMediaSectionCell;
import org.vidogram.ui.Cells.SharedPhotoVideoCell;
import org.vidogram.ui.Cells.SharedPhotoVideoCell.SharedPhotoVideoCellDelegate;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.EmbedBottomSheet;
import org.vidogram.ui.Components.FragmentContextView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.NumberTextView;
import org.vidogram.ui.Components.RadialProgressView;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.vidogram.ui.Components.RecyclerListView.SectionsAdapter;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;
import org.vidogram.ui.Components.ShareAlert;

public class MediaActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, PhotoViewer.PhotoViewerProvider
{
  private static final int delete = 4;
  private static final int files_item = 2;
  private static final int forward = 3;
  private static final int links_item = 5;
  private static final int music_item = 6;
  private static final int quoteforward = 9707;
  private static final int shared_media_item = 1;
  private static final int videoCall = 9709;
  private ArrayList<View> actionModeViews = new ArrayList();
  private SharedDocumentsAdapter audioAdapter;
  private MediaSearchAdapter audioSearchAdapter;
  private int cantDeleteMessagesCount;
  private ArrayList<SharedPhotoVideoCell> cellCache = new ArrayList(6);
  private int columnsCount = 4;
  private long dialog_id;
  private SharedDocumentsAdapter documentsAdapter;
  private MediaSearchAdapter documentsSearchAdapter;
  private TextView dropDown;
  private ActionBarMenuItem dropDownContainer;
  private Drawable dropDownDrawable;
  private ImageView emptyImageView;
  private TextView emptyTextView;
  private LinearLayout emptyView;
  private FragmentContextView fragmentContextView;
  protected TLRPC.ChatFull info = null;
  private LinearLayoutManager layoutManager;
  private SharedLinksAdapter linksAdapter;
  private MediaSearchAdapter linksSearchAdapter;
  private RecyclerListView listView;
  private long mergeDialogId;
  private SharedPhotoVideoAdapter photoVideoAdapter;
  private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
  private RadialProgressView progressBar;
  private LinearLayout progressView;
  private boolean scrolling;
  private ActionBarMenuItem searchItem;
  private boolean searchWas;
  private boolean searching;
  private HashMap<Integer, MessageObject>[] selectedFiles = { new HashMap(), new HashMap() };
  private NumberTextView selectedMessagesCountTextView;
  private int selectedMode;
  private SharedMediaData[] sharedMediaData = new SharedMediaData[5];
  private int user_id;

  public MediaActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }

  private void CallFailure()
  {
    try
    {
      org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).a();
      if (getParentActivity() == null)
        return;
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
      localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
      localBuilder.setMessage(LocaleController.formatString("CallFailure", 2131166759, new Object[0]));
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
      localBuilder.setPositiveButton(LocaleController.getString("SendInvitation", 2131166785), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          MediaActivity.this.processSendingText(ContactsController.getInstance().getInviteText());
          a.a().a(new o().a("Vidogram"));
        }
      });
      showDialog(localBuilder.create());
      return;
    }
    catch (Exception localException)
    {
    }
  }

  private void ConnectionFailde()
  {
    int i = 0;
    if (getParentActivity() == null)
      return;
    if (Settings.System.getInt(getParentActivity().getContentResolver(), "airplane_mode_on", 0) != 0)
      i = 1;
    Object localObject2 = new AlertDialog.Builder(getParentActivity());
    if (i != 0)
    {
      localObject1 = LocaleController.getString("VoipOfflineAirplaneTitle", 2131166594);
      localObject2 = ((AlertDialog.Builder)localObject2).setTitle((CharSequence)localObject1);
      if (i == 0)
        break label165;
    }
    label165: for (Object localObject1 = LocaleController.getString("VoipOfflineAirplane", 2131166593); ; localObject1 = LocaleController.getString("VoipOffline", 2131166592))
    {
      localObject1 = ((AlertDialog.Builder)localObject2).setMessage((CharSequence)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166153), null);
      if (i != 0)
      {
        localObject2 = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
        if (((Intent)localObject2).resolveActivity(getParentActivity().getPackageManager()) != null)
          ((AlertDialog.Builder)localObject1).setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", 2131166595), new DialogInterface.OnClickListener((Intent)localObject2)
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              MediaActivity.this.getParentActivity().startActivity(this.val$settingsIntent);
            }
          });
      }
      ((AlertDialog.Builder)localObject1).show();
      return;
      localObject1 = LocaleController.getString("VoipOfflineTitle", 2131166596);
      break;
    }
  }

  private void VidogramCall()
  {
    u localu = null;
    do
    {
      try
      {
        int i = (int)this.dialog_id;
        Object localObject = localu;
        if (i == 0)
          continue;
        localObject = localu;
        if (i <= 0)
          continue;
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(i));
        continue;
        localu = org.vidogram.VidogramUi.b.a(getParentActivity()).a(this.user_id + "");
        ConnectionsManager.getInstance().getConnectionState();
        if (ConnectionsManager.getInstance().getConnectionState() != 3)
        {
          ConnectionFailde();
          return;
        }
      }
      catch (Exception localException)
      {
        FirebaseCrash.a(localException);
        return;
      }
      if (itman.Vidofilm.b.a(getParentActivity()).l() == null)
      {
        new e(getParentActivity()).a(localException);
        return;
      }
      if ((localu == null) && (MessagesController.getInstance().getUser(Integer.valueOf(this.user_id)).contact))
      {
        CallFailure();
        return;
      }
      new e(getParentActivity()).a(localException);
      return;
    }
    while (localException != null);
  }

  private void fixLayoutInternal()
  {
    int i = 0;
    if (this.listView == null)
      return;
    int j = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
    if ((!AndroidUtilities.isTablet()) && (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 2))
    {
      this.selectedMessagesCountTextView.setTextSize(18);
      label62: if (!AndroidUtilities.isTablet())
        break label200;
      this.columnsCount = 4;
      this.emptyTextView.setPadding(AndroidUtilities.dp(40.0F), 0, AndroidUtilities.dp(40.0F), AndroidUtilities.dp(128.0F));
    }
    while (true)
    {
      this.photoVideoAdapter.notifyDataSetChanged();
      if (this.dropDownContainer == null)
        break;
      if (!AndroidUtilities.isTablet())
      {
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.dropDownContainer.getLayoutParams();
        if (Build.VERSION.SDK_INT >= 21)
          i = AndroidUtilities.statusBarHeight;
        localLayoutParams.topMargin = i;
        this.dropDownContainer.setLayoutParams(localLayoutParams);
      }
      if ((AndroidUtilities.isTablet()) || (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2))
        break label274;
      this.dropDown.setTextSize(18.0F);
      return;
      this.selectedMessagesCountTextView.setTextSize(20);
      break label62;
      label200: if ((j == 3) || (j == 1))
      {
        this.columnsCount = 6;
        this.emptyTextView.setPadding(AndroidUtilities.dp(40.0F), 0, AndroidUtilities.dp(40.0F), 0);
        continue;
      }
      this.columnsCount = 4;
      this.emptyTextView.setPadding(AndroidUtilities.dp(40.0F), 0, AndroidUtilities.dp(40.0F), AndroidUtilities.dp(128.0F));
    }
    label274: this.dropDown.setTextSize(20.0F);
  }

  private void onItemClick(int paramInt1, View paramView, MessageObject paramMessageObject, int paramInt2)
  {
    if (paramMessageObject == null);
    label29: label83: label114: Object localObject1;
    label186: label249: Intent localIntent;
    label279: label286: label544: label762: Uri localUri;
    while (true)
    {
      return;
      if (this.actionBar.isActionModeShowed())
      {
        if (paramMessageObject.getDialogId() == this.dialog_id)
        {
          paramInt1 = 0;
          if (!this.selectedFiles[paramInt1].containsKey(Integer.valueOf(paramMessageObject.getId())))
            break label186;
          this.selectedFiles[paramInt1].remove(Integer.valueOf(paramMessageObject.getId()));
          if (!paramMessageObject.canDeleteMessage(null))
            this.cantDeleteMessagesCount -= 1;
          if ((!this.selectedFiles[0].isEmpty()) || (!this.selectedFiles[1].isEmpty()))
            break label249;
          this.actionBar.hideActionMode();
          localObject1 = this.actionBar.createActionMode().getItem(4);
          if (this.cantDeleteMessagesCount != 0)
            break label279;
        }
        for (int i = 0; ; i = 8)
        {
          ((ActionBarMenuItem)localObject1).setVisibility(i);
          this.scrolling = false;
          if (!(paramView instanceof SharedDocumentCell))
            break label286;
          ((SharedDocumentCell)paramView).setChecked(this.selectedFiles[paramInt1].containsKey(Integer.valueOf(paramMessageObject.getId())), true);
          return;
          paramInt1 = 1;
          break label29;
          if (this.selectedFiles[0].size() + this.selectedFiles[1].size() >= 100)
            break;
          this.selectedFiles[paramInt1].put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
          if (paramMessageObject.canDeleteMessage(null))
            break label83;
          this.cantDeleteMessagesCount += 1;
          break label83;
          this.selectedMessagesCountTextView.setNumber(this.selectedFiles[0].size() + this.selectedFiles[1].size(), true);
          break label114;
        }
        if ((paramView instanceof SharedPhotoVideoCell))
        {
          ((SharedPhotoVideoCell)paramView).setChecked(paramInt2, this.selectedFiles[paramInt1].containsKey(Integer.valueOf(paramMessageObject.getId())), true);
          return;
        }
        if (!(paramView instanceof SharedLinkCell))
          continue;
        ((SharedLinkCell)paramView).setChecked(this.selectedFiles[paramInt1].containsKey(Integer.valueOf(paramMessageObject.getId())), true);
        return;
      }
      if (this.selectedMode == 0)
      {
        PhotoViewer.getInstance().setParentActivity(getParentActivity());
        PhotoViewer.getInstance().openPhoto(this.sharedMediaData[this.selectedMode].messages, paramInt1, this.dialog_id, this.mergeDialogId, this);
        return;
      }
      if ((this.selectedMode != 1) && (this.selectedMode != 4))
        break label1056;
      if (!(paramView instanceof SharedDocumentCell))
        continue;
      paramView = (SharedDocumentCell)paramView;
      if (!paramView.isLoaded())
        break;
      if ((paramMessageObject.isMusic()) && (MediaController.getInstance().setPlaylist(this.sharedMediaData[this.selectedMode].messages, paramMessageObject)))
        continue;
      if (paramMessageObject.messageOwner.media != null)
        localObject2 = FileLoader.getAttachFileName(paramMessageObject.getDocument());
      while (true)
      {
        if ((paramMessageObject.messageOwner.attachPath == null) || (paramMessageObject.messageOwner.attachPath.length() == 0))
          break label1176;
        paramView = new File(paramMessageObject.messageOwner.attachPath);
        label520: if ((paramView != null) && ((paramView == null) || (paramView.exists())))
          break label1170;
        localObject1 = FileLoader.getPathToMessage(paramMessageObject.messageOwner);
        if ((localObject1 == null) || (!((File)localObject1).exists()))
          break label1174;
        if (!((File)localObject1).getName().endsWith("attheme"))
          break;
        paramView = Theme.applyThemeFile((File)localObject1, paramMessageObject.getDocumentName(), true);
        if (paramView != null)
        {
          presentFragment(new ThemePreviewActivity((File)localObject1, paramView));
          return;
          localObject2 = "";
          continue;
        }
        paramView = new AlertDialog.Builder(getParentActivity());
        paramView.setTitle(LocaleController.getString("AppName", 2131165319));
        paramView.setMessage(LocaleController.getString("IncorrectTheme", 2131165833));
        paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
        showDialog(paramView.create());
        return;
      }
      while (true)
      {
        try
        {
          localIntent = new Intent("android.intent.action.VIEW");
          localIntent.setFlags(1);
          paramView = MimeTypeMap.getSingleton();
          paramInt1 = ((String)localObject2).lastIndexOf('.');
          if (paramInt1 == -1)
            break label1165;
          localObject2 = paramView.getMimeTypeFromExtension(((String)localObject2).substring(paramInt1 + 1).toLowerCase());
          paramView = (View)localObject2;
          if (localObject2 != null)
            continue;
          localObject2 = paramMessageObject.getDocument().mime_type;
          if (localObject2 == null)
            break label1181;
          paramView = (View)localObject2;
          if (((String)localObject2).length() == 0)
            break label1181;
          if (Build.VERSION.SDK_INT < 24)
            break label954;
          localUri = FileProvider.a(getParentActivity(), "org.vidogram.messenger.provider", (File)localObject1);
          if (paramView == null)
            break label946;
          localObject2 = paramView;
          localIntent.setDataAndType(localUri, (String)localObject2);
          label801: if (paramView == null)
            break label998;
          try
          {
            getParentActivity().startActivityForResult(localIntent, 500);
            return;
          }
          catch (Exception paramView)
          {
            if (Build.VERSION.SDK_INT < 24)
              break label981;
          }
          localIntent.setDataAndType(FileProvider.a(getParentActivity(), "org.vidogram.messenger.provider", (File)localObject1), "text/plain");
          label848: getParentActivity().startActivityForResult(localIntent, 500);
          return;
        }
        catch (Exception paramView)
        {
        }
        if (getParentActivity() == null)
          break;
        paramView = new AlertDialog.Builder(getParentActivity());
        paramView.setTitle(LocaleController.getString("AppName", 2131165319));
        paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
        paramView.setMessage(LocaleController.formatString("NoHandleAppInstalled", 2131166030, new Object[] { paramMessageObject.getDocument().mime_type }));
        showDialog(paramView.create());
        return;
        label946: localObject2 = "text/plain";
      }
      label954: localUri = Uri.fromFile((File)localObject1);
      if (paramView == null)
        break label1186;
    }
    label1056: label1186: for (Object localObject2 = paramView; ; localObject2 = "text/plain")
    {
      localIntent.setDataAndType(localUri, (String)localObject2);
      break label801;
      label981: localIntent.setDataAndType(Uri.fromFile((File)localObject1), "text/plain");
      break label848;
      label998: getParentActivity().startActivityForResult(localIntent, 500);
      return;
      if (!paramView.isLoading())
      {
        FileLoader.getInstance().loadFile(paramView.getMessage().getDocument(), false, false);
        paramView.updateFileExistIcon();
        return;
      }
      FileLoader.getInstance().cancelLoadFile(paramView.getMessage().getDocument());
      paramView.updateFileExistIcon();
      return;
      if (this.selectedMode != 3)
        break;
      try
      {
        paramMessageObject = paramMessageObject.messageOwner.media.webpage;
        if ((paramMessageObject != null) && (!(paramMessageObject instanceof TLRPC.TL_webPageEmpty)))
          if ((Build.VERSION.SDK_INT >= 16) && (paramMessageObject.embed_url != null) && (paramMessageObject.embed_url.length() != 0))
          {
            openWebView(paramMessageObject);
            return;
          }
      }
      catch (Exception paramView)
      {
        FileLog.e(paramView);
        return;
      }
      for (paramMessageObject = paramMessageObject.url; ; paramMessageObject = null)
      {
        localObject1 = paramMessageObject;
        if (paramMessageObject == null)
          localObject1 = ((SharedLinkCell)paramView).getLink(0);
        if (localObject1 == null)
          break;
        Browser.openUrl(getParentActivity(), (String)localObject1);
        return;
      }
      paramView = null;
      break label762;
      localObject1 = paramView;
      break label544;
      break;
      paramView = null;
      break label520;
      paramView = null;
      break label762;
    }
  }

  private boolean onItemLongClick(MessageObject paramMessageObject, View paramView, int paramInt)
  {
    if (this.actionBar.isActionModeShowed())
      return false;
    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
    Object localObject = this.selectedFiles;
    if (paramMessageObject.getDialogId() == this.dialog_id)
    {
      i = 0;
      localObject[i].put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
      if (!paramMessageObject.canDeleteMessage(null))
        this.cantDeleteMessagesCount += 1;
      paramMessageObject = this.actionBar.createActionMode().getItem(4);
      if (this.cantDeleteMessagesCount != 0)
        break label208;
    }
    label208: for (int i = 0; ; i = 8)
    {
      paramMessageObject.setVisibility(i);
      this.selectedMessagesCountTextView.setNumber(1, false);
      paramMessageObject = new AnimatorSet();
      localObject = new ArrayList();
      i = 0;
      while (i < this.actionModeViews.size())
      {
        View localView = (View)this.actionModeViews.get(i);
        AndroidUtilities.clearDrawableAnimation(localView);
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localView, "scaleY", new float[] { 0.1F, 1.0F }));
        i += 1;
      }
      i = 1;
      break;
    }
    paramMessageObject.playTogether((Collection)localObject);
    paramMessageObject.setDuration(250L);
    paramMessageObject.start();
    this.scrolling = false;
    if ((paramView instanceof SharedDocumentCell))
      ((SharedDocumentCell)paramView).setChecked(true, true);
    while (true)
    {
      this.actionBar.showActionMode();
      return true;
      if ((paramView instanceof SharedPhotoVideoCell))
      {
        ((SharedPhotoVideoCell)paramView).setChecked(paramInt, true, true);
        continue;
      }
      if (!(paramView instanceof SharedLinkCell))
        continue;
      ((SharedLinkCell)paramView).setChecked(true, true);
    }
  }

  private void openWebView(TLRPC.WebPage paramWebPage)
  {
    EmbedBottomSheet.show(getParentActivity(), paramWebPage.site_name, paramWebPage.description, paramWebPage.url, paramWebPage.embed_url, paramWebPage.embed_width, paramWebPage.embed_height);
  }

  private void switchToCurrentSelectedMode()
  {
    if ((this.searching) && (this.searchWas))
      if (this.listView != null)
      {
        if (this.selectedMode == 1)
        {
          this.listView.setAdapter(this.documentsSearchAdapter);
          this.documentsSearchAdapter.notifyDataSetChanged();
        }
      }
      else if (this.emptyTextView != null)
      {
        this.emptyTextView.setText(LocaleController.getString("NoResult", 2131166045));
        this.emptyTextView.setTextSize(1, 20.0F);
        this.emptyImageView.setVisibility(8);
      }
    label347: ActionBarMenuItem localActionBarMenuItem;
    int i;
    label481: 
    do
    {
      return;
      if (this.selectedMode == 3)
      {
        this.listView.setAdapter(this.linksSearchAdapter);
        this.linksSearchAdapter.notifyDataSetChanged();
        break;
      }
      if (this.selectedMode != 4)
        break;
      this.listView.setAdapter(this.audioSearchAdapter);
      this.audioSearchAdapter.notifyDataSetChanged();
      break;
      this.emptyTextView.setTextSize(1, 17.0F);
      this.emptyImageView.setVisibility(0);
      if (this.selectedMode == 0)
      {
        this.listView.setAdapter(this.photoVideoAdapter);
        this.dropDown.setText(LocaleController.getString("SharedMediaTitle", 2131166463));
        this.emptyImageView.setImageResource(2130838088);
        if ((int)this.dialog_id == 0)
        {
          this.emptyTextView.setText(LocaleController.getString("NoMediaSecret", 2131166035));
          this.searchItem.setVisibility(8);
          if ((!this.sharedMediaData[this.selectedMode].loading) || (!this.sharedMediaData[this.selectedMode].messages.isEmpty()))
            break label347;
          this.progressView.setVisibility(0);
          this.listView.setEmptyView(null);
          this.emptyView.setVisibility(8);
        }
        while (true)
        {
          this.listView.setVisibility(0);
          this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0F));
          return;
          this.emptyTextView.setText(LocaleController.getString("NoMedia", 2131166033));
          break;
          this.progressView.setVisibility(8);
          this.listView.setEmptyView(this.emptyView);
        }
      }
      if ((this.selectedMode != 1) && (this.selectedMode != 4))
        continue;
      if (this.selectedMode == 1)
      {
        this.listView.setAdapter(this.documentsAdapter);
        this.dropDown.setText(LocaleController.getString("DocumentsTitle", 2131165660));
        this.emptyImageView.setImageResource(2130838089);
        if ((int)this.dialog_id == 0)
        {
          this.emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", 2131166049));
          localActionBarMenuItem = this.searchItem;
          if (this.sharedMediaData[this.selectedMode].messages.isEmpty())
            break label773;
          i = 0;
          localActionBarMenuItem.setVisibility(i);
          if ((!this.sharedMediaData[this.selectedMode].loading) && (this.sharedMediaData[this.selectedMode].endReached[0] == 0) && (this.sharedMediaData[this.selectedMode].messages.isEmpty()))
          {
            SharedMediaData.access$402(this.sharedMediaData[this.selectedMode], true);
            long l = this.dialog_id;
            if (this.selectedMode != 1)
              break label779;
            i = 1;
            SharedMediaQuery.loadMedia(l, 0, 50, 0, i, true, this.classGuid);
          }
          this.listView.setVisibility(0);
          if ((!this.sharedMediaData[this.selectedMode].loading) || (!this.sharedMediaData[this.selectedMode].messages.isEmpty()))
            break label784;
          this.progressView.setVisibility(0);
          this.listView.setEmptyView(null);
          this.emptyView.setVisibility(8);
        }
      }
      while (true)
      {
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0F));
        return;
        this.emptyTextView.setText(LocaleController.getString("NoSharedFiles", 2131166048));
        break;
        if (this.selectedMode != 4)
          break;
        this.listView.setAdapter(this.audioAdapter);
        this.dropDown.setText(LocaleController.getString("AudioTitle", 2131165370));
        this.emptyImageView.setImageResource(2130838091);
        if ((int)this.dialog_id == 0)
        {
          this.emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", 2131166047));
          break;
        }
        this.emptyTextView.setText(LocaleController.getString("NoSharedAudio", 2131166046));
        break;
        i = 8;
        break label481;
        i = 4;
        break label566;
        this.progressView.setVisibility(8);
        this.listView.setEmptyView(this.emptyView);
      }
    }
    while (this.selectedMode != 3);
    label566: this.listView.setAdapter(this.linksAdapter);
    label773: label779: label784: this.dropDown.setText(LocaleController.getString("LinksTitle", 2131165919));
    this.emptyImageView.setImageResource(2130838090);
    if ((int)this.dialog_id == 0)
    {
      this.emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", 2131166051));
      localActionBarMenuItem = this.searchItem;
      if (this.sharedMediaData[3].messages.isEmpty())
        break label1088;
      i = 0;
      label899: localActionBarMenuItem.setVisibility(i);
      if ((!this.sharedMediaData[this.selectedMode].loading) && (this.sharedMediaData[this.selectedMode].endReached[0] == 0) && (this.sharedMediaData[this.selectedMode].messages.isEmpty()))
      {
        SharedMediaData.access$402(this.sharedMediaData[this.selectedMode], true);
        SharedMediaQuery.loadMedia(this.dialog_id, 0, 50, 0, 3, true, this.classGuid);
      }
      this.listView.setVisibility(0);
      if ((!this.sharedMediaData[this.selectedMode].loading) || (!this.sharedMediaData[this.selectedMode].messages.isEmpty()))
        break label1094;
      this.progressView.setVisibility(0);
      this.listView.setEmptyView(null);
      this.emptyView.setVisibility(8);
    }
    while (true)
    {
      this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0F));
      return;
      this.emptyTextView.setText(LocaleController.getString("NoSharedLinks", 2131166050));
      break;
      label1088: i = 8;
      break label899;
      label1094: this.progressView.setVisibility(8);
      this.listView.setEmptyView(this.emptyView);
    }
  }

  public boolean allowCaption()
  {
    return true;
  }

  public boolean cancelButtonPressed()
  {
    return true;
  }

  public View createView(Context paramContext)
  {
    Object localObject1 = new FrameLayout(paramContext);
    this.fragmentView = ((View)localObject1);
    if (getArguments().getBoolean("CallHistory", false))
    {
      this.user_id = (int)getArguments().getLong("dialog_id", 0L);
      this.actionBar.setBackButtonDrawable(new BackDrawable(false));
      this.actionBar.setTitle(LocaleController.getString("CallHistory", 2131166761));
      this.actionBar.setAllowOverlayTitle(false);
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            MediaActivity.this.finishFragment();
          do
            return;
          while ((paramInt != 9709) || (MediaActivity.this.user_id == 0));
          MediaActivity.this.VidogramCall();
        }
      });
      ((FrameLayout)localObject1).addView(new org.vidogram.VidogramUi.a.b().a(paramContext, getArguments().getLong("dialog_id", 0L) + ""));
      this.actionBar.createMenu().addItemWithWidth(9709, 2130837867, AndroidUtilities.dp(50.0F));
      return this.fragmentView;
    }
    this.actionBar.setBackButtonDrawable(new BackDrawable(false));
    this.actionBar.setTitle("");
    this.actionBar.setAllowOverlayTitle(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        int j;
        Object localObject1;
        int i;
        if (paramInt == -1)
          if (MediaActivity.this.actionBar.isActionModeShowed())
          {
            paramInt = 1;
            while (paramInt >= 0)
            {
              MediaActivity.this.selectedFiles[paramInt].clear();
              paramInt -= 1;
            }
            MediaActivity.access$902(MediaActivity.this, 0);
            MediaActivity.this.actionBar.hideActionMode();
            j = MediaActivity.this.listView.getChildCount();
            paramInt = 0;
            if (paramInt < j)
            {
              localObject1 = MediaActivity.this.listView.getChildAt(paramInt);
              if ((localObject1 instanceof SharedDocumentCell))
                ((SharedDocumentCell)localObject1).setChecked(false, true);
              while (true)
              {
                paramInt += 1;
                break;
                if ((localObject1 instanceof SharedPhotoVideoCell))
                {
                  i = 0;
                  while (i < 6)
                  {
                    ((SharedPhotoVideoCell)localObject1).setChecked(i, false, true);
                    i += 1;
                  }
                  continue;
                }
                if (!(localObject1 instanceof SharedLinkCell))
                  continue;
                ((SharedLinkCell)localObject1).setChecked(false, true);
              }
            }
          }
          else
          {
            MediaActivity.this.finishFragment();
          }
        Object localObject3;
        boolean[] arrayOfBoolean;
        Object localObject2;
        Object localObject4;
        Object localObject5;
        while (true)
        {
          return;
          if (paramInt == 1)
          {
            if (MediaActivity.this.selectedMode == 0)
              continue;
            MediaActivity.access$1202(MediaActivity.this, 0);
            MediaActivity.this.switchToCurrentSelectedMode();
            return;
          }
          if (paramInt == 2)
          {
            if (MediaActivity.this.selectedMode == 1)
              continue;
            MediaActivity.access$1202(MediaActivity.this, 1);
            MediaActivity.this.switchToCurrentSelectedMode();
            return;
          }
          if (paramInt == 5)
          {
            if (MediaActivity.this.selectedMode == 3)
              continue;
            MediaActivity.access$1202(MediaActivity.this, 3);
            MediaActivity.this.switchToCurrentSelectedMode();
            return;
          }
          if (paramInt == 6)
          {
            if (MediaActivity.this.selectedMode == 4)
              continue;
            MediaActivity.access$1202(MediaActivity.this, 4);
            MediaActivity.this.switchToCurrentSelectedMode();
            return;
          }
          if (paramInt == 4)
          {
            if (MediaActivity.this.getParentActivity() == null)
              continue;
            localObject3 = new AlertDialog.Builder(MediaActivity.this.getParentActivity());
            ((AlertDialog.Builder)localObject3).setMessage(LocaleController.formatString("AreYouSureDeleteMessages", 2131165342, new Object[] { LocaleController.formatPluralString("items", MediaActivity.access$800(MediaActivity.this)[0].size() + MediaActivity.access$800(MediaActivity.this)[1].size()) }));
            ((AlertDialog.Builder)localObject3).setTitle(LocaleController.getString("AppName", 2131165319));
            arrayOfBoolean = new boolean[1];
            paramInt = (int)MediaActivity.this.dialog_id;
            if (paramInt == 0)
              break;
            if (paramInt > 0)
            {
              localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(paramInt));
              localObject2 = null;
            }
            while (true)
            {
              if ((localObject1 == null) && (ChatObject.isChannel((TLRPC.Chat)localObject2)))
                break label757;
              int k = ConnectionsManager.getInstance().getCurrentTime();
              if (((localObject1 == null) || (((TLRPC.User)localObject1).id == UserConfig.getClientUserId())) && (localObject2 == null))
                break label757;
              i = 1;
              paramInt = 0;
              j = paramInt;
              if (i < 0)
                break;
              localObject4 = MediaActivity.this.selectedFiles[i].entrySet().iterator();
              while (true)
              {
                if (!((Iterator)localObject4).hasNext())
                  break label1137;
                localObject5 = (MessageObject)((Map.Entry)((Iterator)localObject4).next()).getValue();
                if (((MessageObject)localObject5).messageOwner.action != null)
                  continue;
                if (((MessageObject)localObject5).isOut())
                {
                  if (k - ((MessageObject)localObject5).messageOwner.date > 172800)
                    break label1134;
                  paramInt = 1;
                  label582: continue;
                  localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(-paramInt));
                  localObject1 = null;
                  break;
                }
              }
              paramInt = 0;
            }
          }
        }
        label679: label692: label852: label862: label1134: label1137: 
        while (true)
        {
          if (paramInt != 0)
          {
            j = paramInt;
            if (j != 0)
            {
              localObject4 = new FrameLayout(MediaActivity.this.getParentActivity());
              localObject5 = new CheckBoxCell(MediaActivity.this.getParentActivity(), true);
              ((CheckBoxCell)localObject5).setBackgroundDrawable(Theme.getSelectorDrawable(false));
              if (localObject2 == null)
                break label819;
              ((CheckBoxCell)localObject5).setText(LocaleController.getString("DeleteForAll", 2131165640), "", false, false);
              if (!LocaleController.isRTL)
                break label852;
              paramInt = AndroidUtilities.dp(16.0F);
              if (!LocaleController.isRTL)
                break label862;
            }
          }
          for (i = AndroidUtilities.dp(8.0F); ; i = AndroidUtilities.dp(16.0F))
          {
            ((CheckBoxCell)localObject5).setPadding(paramInt, 0, i, 0);
            ((FrameLayout)localObject4).addView((View)localObject5, LayoutHelper.createFrame(-1, 48.0F, 51, 0.0F, 0.0F, 0.0F, 0.0F));
            ((CheckBoxCell)localObject5).setOnClickListener(new View.OnClickListener(arrayOfBoolean)
            {
              public void onClick(View paramView)
              {
                paramView = (CheckBoxCell)paramView;
                boolean[] arrayOfBoolean = this.val$deleteForAll;
                if (this.val$deleteForAll[0] == 0);
                for (int i = 1; ; i = 0)
                {
                  arrayOfBoolean[0] = i;
                  paramView.setChecked(this.val$deleteForAll[0], true);
                  return;
                }
              }
            });
            ((AlertDialog.Builder)localObject3).setView((View)localObject4);
            label757: ((AlertDialog.Builder)localObject3).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(arrayOfBoolean)
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                paramInt = 1;
                while (paramInt >= 0)
                {
                  ArrayList localArrayList = new ArrayList(MediaActivity.this.selectedFiles[paramInt].keySet());
                  Object localObject1 = null;
                  paramDialogInterface = null;
                  int j = 0;
                  int i = j;
                  Object localObject2;
                  if (!localArrayList.isEmpty())
                  {
                    localObject2 = (MessageObject)MediaActivity.this.selectedFiles[paramInt].get(localArrayList.get(0));
                    i = j;
                    if (((MessageObject)localObject2).messageOwner.to_id.channel_id != 0)
                      i = ((MessageObject)localObject2).messageOwner.to_id.channel_id;
                  }
                  if ((int)MediaActivity.this.dialog_id == 0)
                    paramDialogInterface = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(MediaActivity.this.dialog_id >> 32)));
                  if (paramDialogInterface != null)
                  {
                    localObject2 = new ArrayList();
                    Iterator localIterator = MediaActivity.this.selectedFiles[paramInt].entrySet().iterator();
                    while (true)
                    {
                      localObject1 = localObject2;
                      if (!localIterator.hasNext())
                        break;
                      localObject1 = (MessageObject)((Map.Entry)localIterator.next()).getValue();
                      if ((((MessageObject)localObject1).messageOwner.random_id == 0L) || (((MessageObject)localObject1).type == 10))
                        continue;
                      ((ArrayList)localObject2).add(Long.valueOf(((MessageObject)localObject1).messageOwner.random_id));
                    }
                  }
                  MessagesController.getInstance().deleteMessages(localArrayList, (ArrayList)localObject1, paramDialogInterface, i, this.val$deleteForAll[0]);
                  MediaActivity.this.selectedFiles[paramInt].clear();
                  paramInt -= 1;
                }
                MediaActivity.this.actionBar.hideActionMode();
                MediaActivity.this.actionBar.closeSearchField();
                MediaActivity.access$902(MediaActivity.this, 0);
              }
            });
            ((AlertDialog.Builder)localObject3).setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
            MediaActivity.this.showDialog(((AlertDialog.Builder)localObject3).create());
            return;
            i -= 1;
            break;
            ((CheckBoxCell)localObject5).setText(LocaleController.formatString("DeleteForUser", 2131165641, new Object[] { UserObject.getFirstName((TLRPC.User)localObject1) }), "", false, false);
            break label679;
            paramInt = AndroidUtilities.dp(8.0F);
            break label692;
          }
          if (paramInt == 9707)
          {
            localObject1 = new ArrayList();
            paramInt = 1;
            while (paramInt >= 0)
            {
              localObject2 = new ArrayList(MediaActivity.this.selectedFiles[paramInt].keySet());
              Collections.sort((List)localObject2);
              localObject2 = ((ArrayList)localObject2).iterator();
              while (((Iterator)localObject2).hasNext())
              {
                localObject3 = (Integer)((Iterator)localObject2).next();
                if (((Integer)localObject3).intValue() <= 0)
                  continue;
                ((ArrayList)localObject1).add(MediaActivity.this.selectedFiles[paramInt].get(localObject3));
              }
              MediaActivity.this.selectedFiles[paramInt].clear();
              paramInt -= 1;
            }
            MediaActivity.access$902(MediaActivity.this, 0);
            MediaActivity.this.actionBar.hideActionMode();
            MediaActivity.this.listView.invalidateViews();
            if (MediaActivity.this.getParentActivity() == null)
              break;
            MediaActivity.this.showDialog(new ShareAlert(MediaActivity.this.getParentActivity(), (ArrayList)localObject1, null, false, null, true));
            return;
          }
          if (paramInt != 3)
            break;
          localObject1 = new Bundle();
          ((Bundle)localObject1).putBoolean("onlySelect", true);
          ((Bundle)localObject1).putInt("dialogsType", 1);
          localObject1 = new DialogsActivity((Bundle)localObject1);
          ((DialogsActivity)localObject1).setDelegate(new DialogsActivity.DialogsActivityDelegate()
          {
            public void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean)
            {
              int i = (int)paramLong;
              if (i != 0)
              {
                Object localObject1 = new Bundle();
                ((Bundle)localObject1).putBoolean("scrollToTopOnResume", true);
                if (i > 0)
                  ((Bundle)localObject1).putInt("user_id", i);
                do
                {
                  while (!MessagesController.checkCanOpenChat((Bundle)localObject1, paramDialogsActivity))
                  {
                    return;
                    if (i >= 0)
                      continue;
                    ((Bundle)localObject1).putInt("chat_id", -i);
                  }
                  paramDialogsActivity = new ArrayList();
                  i = 1;
                  while (i >= 0)
                  {
                    Object localObject2 = new ArrayList(MediaActivity.this.selectedFiles[i].keySet());
                    Collections.sort((List)localObject2);
                    localObject2 = ((ArrayList)localObject2).iterator();
                    while (((Iterator)localObject2).hasNext())
                    {
                      Integer localInteger = (Integer)((Iterator)localObject2).next();
                      if (localInteger.intValue() <= 0)
                        continue;
                      paramDialogsActivity.add(MediaActivity.this.selectedFiles[i].get(localInteger));
                    }
                    MediaActivity.this.selectedFiles[i].clear();
                    i -= 1;
                  }
                  MediaActivity.access$902(MediaActivity.this, 0);
                  MediaActivity.this.actionBar.hideActionMode();
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                  localObject1 = new ChatActivity((Bundle)localObject1);
                  MediaActivity.this.presentFragment((BaseFragment)localObject1, true);
                  ((ChatActivity)localObject1).showReplyPanel(true, null, paramDialogsActivity, null, false);
                }
                while (AndroidUtilities.isTablet());
                MediaActivity.this.removeSelfFromStack();
                return;
              }
              paramDialogsActivity.finishFragment();
            }
          });
          MediaActivity.this.presentFragment((BaseFragment)localObject1);
          return;
          break label582;
        }
      }
    });
    int i = 1;
    while (i >= 0)
    {
      this.selectedFiles[i].clear();
      i -= 1;
    }
    this.cantDeleteMessagesCount = 0;
    this.actionModeViews.clear();
    localObject1 = this.actionBar.createMenu();
    this.searchItem = ((ActionBarMenu)localObject1).addItem(0, 2130837741).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public void onSearchCollapse()
      {
        MediaActivity.this.dropDownContainer.setVisibility(0);
        if (MediaActivity.this.selectedMode == 1)
          MediaActivity.this.documentsSearchAdapter.search(null);
        while (true)
        {
          MediaActivity.access$1902(MediaActivity.this, false);
          MediaActivity.access$2302(MediaActivity.this, false);
          MediaActivity.this.switchToCurrentSelectedMode();
          return;
          if (MediaActivity.this.selectedMode == 3)
          {
            MediaActivity.this.linksSearchAdapter.search(null);
            continue;
          }
          if (MediaActivity.this.selectedMode != 4)
            continue;
          MediaActivity.this.audioSearchAdapter.search(null);
        }
      }

      public void onSearchExpand()
      {
        MediaActivity.this.dropDownContainer.setVisibility(8);
        MediaActivity.access$1902(MediaActivity.this, true);
      }

      public void onTextChanged(EditText paramEditText)
      {
        paramEditText = paramEditText.getText().toString();
        if (paramEditText.length() != 0)
        {
          MediaActivity.access$2302(MediaActivity.this, true);
          MediaActivity.this.switchToCurrentSelectedMode();
        }
        if (MediaActivity.this.selectedMode == 1)
          if (MediaActivity.this.documentsSearchAdapter != null);
        do
          while (true)
          {
            return;
            MediaActivity.this.documentsSearchAdapter.search(paramEditText);
            return;
            if (MediaActivity.this.selectedMode != 3)
              break;
            if (MediaActivity.this.linksSearchAdapter == null)
              continue;
            MediaActivity.this.linksSearchAdapter.search(paramEditText);
            return;
          }
        while ((MediaActivity.this.selectedMode != 4) || (MediaActivity.this.audioSearchAdapter == null));
        MediaActivity.this.audioSearchAdapter.search(paramEditText);
      }
    });
    this.searchItem.getSearchField().setHint(LocaleController.getString("Search", 2131166381));
    this.searchItem.setVisibility(8);
    this.dropDownContainer = new ActionBarMenuItem(paramContext, (ActionBarMenu)localObject1, 0, 0);
    this.dropDownContainer.setSubMenuOpenSide(1);
    this.dropDownContainer.addSubItem(1, LocaleController.getString("SharedMediaTitle", 2131166463));
    this.dropDownContainer.addSubItem(2, LocaleController.getString("DocumentsTitle", 2131165660));
    Object localObject2;
    float f;
    label448: int j;
    int k;
    if ((int)this.dialog_id != 0)
    {
      this.dropDownContainer.addSubItem(5, LocaleController.getString("LinksTitle", 2131165919));
      this.dropDownContainer.addSubItem(6, LocaleController.getString("AudioTitle", 2131165370));
      localObject1 = this.actionBar;
      localObject2 = this.dropDownContainer;
      if (!AndroidUtilities.isTablet())
        break label1265;
      f = 64.0F;
      ((ActionBar)localObject1).addView((View)localObject2, 0, LayoutHelper.createFrame(-2, -1.0F, 51, f, 0.0F, 40.0F, 0.0F));
      this.dropDownContainer.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          MediaActivity.this.dropDownContainer.toggleSubMenu();
        }
      });
      this.dropDown = new TextView(paramContext);
      this.dropDown.setGravity(3);
      this.dropDown.setSingleLine(true);
      this.dropDown.setLines(1);
      this.dropDown.setMaxLines(1);
      this.dropDown.setEllipsize(TextUtils.TruncateAt.END);
      this.dropDown.setTextColor(Theme.getColor("actionBarDefaultTitle"));
      this.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.dropDownDrawable = paramContext.getResources().getDrawable(2130837748).mutate();
      this.dropDownDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultTitle"), PorterDuff.Mode.MULTIPLY));
      this.dropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, this.dropDownDrawable, null);
      this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
      this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0F), 0);
      this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0F, 16, 16.0F, 0.0F, 0.0F, 0.0F));
      localObject1 = this.actionBar.createActionMode();
      this.selectedMessagesCountTextView = new NumberTextView(((ActionBarMenu)localObject1).getContext());
      this.selectedMessagesCountTextView.setTextSize(18);
      this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.selectedMessagesCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
      this.selectedMessagesCountTextView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          return true;
        }
      });
      ((ActionBarMenu)localObject1).addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0F, 65, 0, 0, 0));
      if ((int)this.dialog_id != 0)
      {
        this.actionModeViews.add(((ActionBarMenu)localObject1).addItemWithWidth(3, 2130837736, AndroidUtilities.dp(54.0F)));
        this.actionModeViews.add(((ActionBarMenu)localObject1).addItemWithWidth(9707, 2130837719, AndroidUtilities.dp(54.0F)));
      }
      this.actionModeViews.add(((ActionBarMenu)localObject1).addItemWithWidth(4, 2130837734, AndroidUtilities.dp(54.0F)));
      this.photoVideoAdapter = new SharedPhotoVideoAdapter(paramContext);
      this.documentsAdapter = new SharedDocumentsAdapter(paramContext, 1);
      this.audioAdapter = new SharedDocumentsAdapter(paramContext, 4);
      this.documentsSearchAdapter = new MediaSearchAdapter(paramContext, 1);
      this.audioSearchAdapter = new MediaSearchAdapter(paramContext, 4);
      this.linksSearchAdapter = new MediaSearchAdapter(paramContext, 3);
      this.linksAdapter = new SharedLinksAdapter(paramContext);
      localObject1 = new FrameLayout(paramContext);
      this.fragmentView = ((View)localObject1);
      i = -1;
      j = 0;
      k = j;
      if (this.layoutManager != null)
      {
        k = this.layoutManager.findFirstVisibleItemPosition();
        if (k == this.layoutManager.getItemCount() - 1)
          break label1280;
        localObject2 = (RecyclerListView.Holder)this.listView.findViewHolderForAdapterPosition(k);
        if (localObject2 == null)
          break label1272;
        i = ((RecyclerListView.Holder)localObject2).itemView.getTop();
        j = k;
        label1034: k = i;
        i = j;
      }
    }
    while (true)
    {
      this.listView = new RecyclerListView(paramContext);
      this.listView.setClipToPadding(false);
      this.listView.setSectionsType(2);
      localObject2 = this.listView;
      LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(paramContext, 1, false);
      this.layoutManager = localLinearLayoutManager;
      ((RecyclerListView)localObject2).setLayoutManager(localLinearLayoutManager);
      ((FrameLayout)localObject1).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          if (((MediaActivity.this.selectedMode == 1) || (MediaActivity.this.selectedMode == 4)) && ((paramView instanceof SharedDocumentCell)))
            MediaActivity.this.onItemClick(paramInt, paramView, ((SharedDocumentCell)paramView).getMessage(), 0);
          do
            return;
          while ((MediaActivity.this.selectedMode != 3) || (!(paramView instanceof SharedLinkCell)));
          MediaActivity.this.onItemClick(paramInt, paramView, ((SharedLinkCell)paramView).getMessage(), 0);
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
        {
          boolean bool = true;
          if ((paramInt == 1) && (MediaActivity.this.searching) && (MediaActivity.this.searchWas))
            AndroidUtilities.hideKeyboard(MediaActivity.this.getParentActivity().getCurrentFocus());
          paramRecyclerView = MediaActivity.this;
          if (paramInt != 0);
          while (true)
          {
            MediaActivity.access$2502(paramRecyclerView, bool);
            return;
            bool = false;
          }
        }

        public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
        {
          paramInt2 = 2;
          if ((MediaActivity.this.searching) && (MediaActivity.this.searchWas));
          label208: 
          do
          {
            int i;
            while (true)
            {
              return;
              i = MediaActivity.this.layoutManager.findFirstVisibleItemPosition();
              if (i != -1)
                break;
              paramInt1 = 0;
              int j = paramRecyclerView.getAdapter().getItemCount();
              if ((paramInt1 == 0) || (paramInt1 + i <= j - 2) || (MediaActivity.SharedMediaData.access$400(MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode])))
                continue;
              if (MediaActivity.this.selectedMode != 0)
                break label208;
              paramInt1 = 0;
            }
            while (true)
            {
              if (MediaActivity.SharedMediaData.access$300(MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode])[0] != 0)
                break label258;
              MediaActivity.SharedMediaData.access$402(MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode], true);
              SharedMediaQuery.loadMedia(MediaActivity.this.dialog_id, 0, 50, MediaActivity.SharedMediaData.access$200(MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode])[0], paramInt1, true, MediaActivity.this.classGuid);
              return;
              paramInt1 = Math.abs(MediaActivity.this.layoutManager.findLastVisibleItemPosition() - i) + 1;
              break;
              if (MediaActivity.this.selectedMode == 1)
              {
                paramInt1 = 1;
                continue;
              }
              paramInt1 = paramInt2;
              if (MediaActivity.this.selectedMode == 2)
                continue;
              if (MediaActivity.this.selectedMode == 4)
              {
                paramInt1 = 4;
                continue;
              }
              paramInt1 = 3;
            }
          }
          while ((MediaActivity.this.mergeDialogId == 0L) || (MediaActivity.SharedMediaData.access$300(MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode])[1] != 0));
          label258: MediaActivity.SharedMediaData.access$402(MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode], true);
          SharedMediaQuery.loadMedia(MediaActivity.this.mergeDialogId, 0, 50, MediaActivity.SharedMediaData.access$200(MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode])[1], paramInt1, true, MediaActivity.this.classGuid);
        }
      });
      this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramView, int paramInt)
        {
          MessageObject localMessageObject;
          if (((MediaActivity.this.selectedMode == 1) || (MediaActivity.this.selectedMode == 4)) && ((paramView instanceof SharedDocumentCell)))
          {
            localMessageObject = ((SharedDocumentCell)paramView).getMessage();
            return MediaActivity.this.onItemLongClick(localMessageObject, paramView, 0);
          }
          if ((MediaActivity.this.selectedMode == 3) && ((paramView instanceof SharedLinkCell)))
          {
            localMessageObject = ((SharedLinkCell)paramView).getMessage();
            return MediaActivity.this.onItemLongClick(localMessageObject, paramView, 0);
          }
          return false;
        }
      });
      if (i != -1)
        this.layoutManager.scrollToPositionWithOffset(i, k);
      i = 0;
      while (i < 6)
      {
        this.cellCache.add(new SharedPhotoVideoCell(paramContext));
        i += 1;
      }
      localObject1 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(this.dialog_id >> 32)));
      if ((localObject1 == null) || (AndroidUtilities.getPeerLayerVersion(((TLRPC.EncryptedChat)localObject1).layer) < 46))
        break;
      this.dropDownContainer.addSubItem(6, LocaleController.getString("AudioTitle", 2131165370));
      break;
      label1265: f = 56.0F;
      break label448;
      label1272: j = -1;
      i = 0;
      break label1034;
      label1280: i = -1;
      k = j;
    }
    this.emptyView = new LinearLayout(paramContext);
    this.emptyView.setOrientation(1);
    this.emptyView.setGravity(17);
    this.emptyView.setVisibility(8);
    this.emptyView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    ((FrameLayout)localObject1).addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.emptyView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        return true;
      }
    });
    this.emptyImageView = new ImageView(paramContext);
    this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
    this.emptyTextView = new TextView(paramContext);
    this.emptyTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
    this.emptyTextView.setGravity(17);
    this.emptyTextView.setTextSize(1, 17.0F);
    this.emptyTextView.setPadding(AndroidUtilities.dp(40.0F), 0, AndroidUtilities.dp(40.0F), AndroidUtilities.dp(128.0F));
    this.emptyView.addView(this.emptyTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
    this.progressView = new LinearLayout(paramContext);
    this.progressView.setGravity(17);
    this.progressView.setOrientation(1);
    this.progressView.setVisibility(8);
    this.progressView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    ((FrameLayout)localObject1).addView(this.progressView, LayoutHelper.createFrame(-1, -1.0F));
    this.progressBar = new RadialProgressView(paramContext);
    this.progressView.addView(this.progressBar, LayoutHelper.createLinear(-2, -2));
    switchToCurrentSelectedMode();
    if (!AndroidUtilities.isTablet())
    {
      paramContext = new FragmentContextView(paramContext, this);
      this.fragmentContextView = paramContext;
      ((FrameLayout)localObject1).addView(paramContext, LayoutHelper.createFrame(-1, 39.0F, 51, 0.0F, -36.0F, 0.0F, 0.0F));
    }
    return (View)(View)this.fragmentView;
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    int j;
    boolean bool;
    int i;
    Object localObject2;
    if (paramInt == NotificationCenter.mediaDidLoaded)
    {
      long l = ((Long)paramArrayOfObject[0]).longValue();
      if (((Integer)paramArrayOfObject[3]).intValue() == this.classGuid)
      {
        j = ((Integer)paramArrayOfObject[4]).intValue();
        SharedMediaData.access$402(this.sharedMediaData[j], false);
        SharedMediaData.access$3202(this.sharedMediaData[j], ((Integer)paramArrayOfObject[1]).intValue());
        localObject1 = (ArrayList)paramArrayOfObject[2];
        if ((int)this.dialog_id == 0)
        {
          bool = true;
          if (l != this.dialog_id)
            break label159;
        }
        label159: for (paramInt = 0; ; paramInt = 1)
        {
          i = 0;
          while (i < ((ArrayList)localObject1).size())
          {
            localObject2 = (MessageObject)((ArrayList)localObject1).get(i);
            this.sharedMediaData[j].addMessage((MessageObject)localObject2, false, bool);
            i += 1;
          }
          bool = false;
          break;
        }
        this.sharedMediaData[j].endReached[paramInt] = ((Boolean)paramArrayOfObject[5]).booleanValue();
        if ((paramInt == 0) && (this.sharedMediaData[this.selectedMode].messages.isEmpty()) && (this.mergeDialogId != 0L))
        {
          SharedMediaData.access$402(this.sharedMediaData[this.selectedMode], true);
          SharedMediaQuery.loadMedia(this.mergeDialogId, 0, 50, this.sharedMediaData[this.selectedMode].max_id[1], j, true, this.classGuid);
        }
        if (!this.sharedMediaData[this.selectedMode].loading)
        {
          if (this.progressView != null)
            this.progressView.setVisibility(8);
          if ((this.selectedMode == j) && (this.listView != null) && (this.listView.getEmptyView() == null))
            this.listView.setEmptyView(this.emptyView);
        }
        this.scrolling = true;
        if ((this.selectedMode != 0) || (j != 0))
          break label422;
        if (this.photoVideoAdapter != null)
          this.photoVideoAdapter.notifyDataSetChanged();
        if ((this.selectedMode == 1) || (this.selectedMode == 3) || (this.selectedMode == 4))
        {
          paramArrayOfObject = this.searchItem;
          if ((this.sharedMediaData[this.selectedMode].messages.isEmpty()) || (this.searching))
            break label515;
        }
      }
      label515: for (paramInt = 0; ; paramInt = 8)
      {
        paramArrayOfObject.setVisibility(paramInt);
        return;
        label422: if ((this.selectedMode == 1) && (j == 1))
        {
          if (this.documentsAdapter == null)
            break;
          this.documentsAdapter.notifyDataSetChanged();
          break;
        }
        if ((this.selectedMode == 3) && (j == 3))
        {
          if (this.linksAdapter == null)
            break;
          this.linksAdapter.notifyDataSetChanged();
          break;
        }
        if ((this.selectedMode != 4) || (j != 4) || (this.audioAdapter == null))
          break;
        this.audioAdapter.notifyDataSetChanged();
        break;
      }
    }
    if (paramInt == NotificationCenter.messagesDeleted)
      if ((int)this.dialog_id >= 0)
        break label1151;
    label697: label873: label880: label1148: label1151: for (Object localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-(int)this.dialog_id)); ; localObject1 = null)
    {
      paramInt = ((Integer)paramArrayOfObject[1]).intValue();
      if (ChatObject.isChannel((TLRPC.Chat)localObject1))
        if ((paramInt == 0) && (this.mergeDialogId != 0L))
          paramInt = 1;
      while (true)
      {
        paramArrayOfObject = (ArrayList)paramArrayOfObject[0];
        j = 0;
        paramArrayOfObject = paramArrayOfObject.iterator();
        if (!paramArrayOfObject.hasNext())
          break label697;
        localObject1 = (Integer)paramArrayOfObject.next();
        localObject2 = this.sharedMediaData;
        int m = localObject2.length;
        i = 0;
        int k = j;
        while (true)
        {
          j = k;
          if (i >= m)
            break;
          if (localObject2[i].deleteMessage(((Integer)localObject1).intValue(), paramInt))
            k = 1;
          i += 1;
        }
        if (paramInt != ((TLRPC.Chat)localObject1).id)
          break;
        paramInt = 0;
        continue;
        if (paramInt != 0)
          break;
        paramInt = 0;
      }
      if (j == 0)
        break;
      this.scrolling = true;
      if (this.photoVideoAdapter != null)
        this.photoVideoAdapter.notifyDataSetChanged();
      if (this.documentsAdapter != null)
        this.documentsAdapter.notifyDataSetChanged();
      if (this.linksAdapter != null)
        this.linksAdapter.notifyDataSetChanged();
      if (this.audioAdapter != null)
        this.audioAdapter.notifyDataSetChanged();
      if ((this.selectedMode != 1) && (this.selectedMode != 3) && (this.selectedMode != 4))
        break;
      paramArrayOfObject = this.searchItem;
      if ((!this.sharedMediaData[this.selectedMode].messages.isEmpty()) && (!this.searching));
      for (paramInt = 0; ; paramInt = 8)
      {
        paramArrayOfObject.setVisibility(paramInt);
        return;
      }
      if (paramInt == NotificationCenter.didReceivedNewMessages)
      {
        if (((Long)paramArrayOfObject[0]).longValue() != this.dialog_id)
          break;
        paramArrayOfObject = (ArrayList)paramArrayOfObject[1];
        if ((int)this.dialog_id == 0)
        {
          bool = true;
          paramInt = 0;
          paramArrayOfObject = paramArrayOfObject.iterator();
          while (true)
            if (paramArrayOfObject.hasNext())
            {
              localObject1 = (MessageObject)paramArrayOfObject.next();
              if (((MessageObject)localObject1).messageOwner.media == null)
                continue;
              i = SharedMediaQuery.getMediaType(((MessageObject)localObject1).messageOwner);
              if (i == -1)
                break;
              if (!this.sharedMediaData[i].addMessage((MessageObject)localObject1, true, bool))
                break label1148;
              paramInt = 1;
            }
        }
      }
      while (true)
      {
        break label880;
        bool = false;
        break label873;
        if (paramInt == 0)
          break;
        this.scrolling = true;
        if (this.photoVideoAdapter != null)
          this.photoVideoAdapter.notifyDataSetChanged();
        if (this.documentsAdapter != null)
          this.documentsAdapter.notifyDataSetChanged();
        if (this.linksAdapter != null)
          this.linksAdapter.notifyDataSetChanged();
        if (this.audioAdapter != null)
          this.audioAdapter.notifyDataSetChanged();
        if ((this.selectedMode != 1) && (this.selectedMode != 3) && (this.selectedMode != 4))
          break;
        paramArrayOfObject = this.searchItem;
        if ((!this.sharedMediaData[this.selectedMode].messages.isEmpty()) && (!this.searching));
        for (paramInt = 0; ; paramInt = 8)
        {
          paramArrayOfObject.setVisibility(paramInt);
          return;
        }
        if (paramInt != NotificationCenter.messageReceivedByServer)
          break;
        localObject1 = (Integer)paramArrayOfObject[0];
        paramArrayOfObject = (Integer)paramArrayOfObject[1];
        localObject2 = this.sharedMediaData;
        i = localObject2.length;
        paramInt = 0;
        while (paramInt < i)
        {
          localObject2[paramInt].replaceMid(((Integer)localObject1).intValue(), paramArrayOfObject.intValue());
          paramInt += 1;
        }
        break;
      }
    }
  }

  public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    if ((paramMessageObject == null) || (this.listView == null) || (this.selectedMode != 0))
      return null;
    int j = this.listView.getChildCount();
    paramInt = 0;
    if (paramInt < j)
    {
      paramFileLocation = this.listView.getChildAt(paramInt);
      Object localObject;
      int i;
      if ((paramFileLocation instanceof SharedPhotoVideoCell))
      {
        localObject = (SharedPhotoVideoCell)paramFileLocation;
        i = 0;
      }
      while (true)
      {
        MessageObject localMessageObject;
        if (i < 6)
        {
          localMessageObject = ((SharedPhotoVideoCell)localObject).getMessageObject(i);
          if (localMessageObject != null);
        }
        else
        {
          paramInt += 1;
          break;
        }
        paramFileLocation = ((SharedPhotoVideoCell)localObject).getImageView(i);
        if (localMessageObject.getId() == paramMessageObject.getId())
        {
          paramMessageObject = new int[2];
          paramFileLocation.getLocationInWindow(paramMessageObject);
          localObject = new PhotoViewer.PlaceProviderObject();
          ((PhotoViewer.PlaceProviderObject)localObject).viewX = paramMessageObject[0];
          i = paramMessageObject[1];
          if (Build.VERSION.SDK_INT >= 21);
          for (paramInt = 0; ; paramInt = AndroidUtilities.statusBarHeight)
          {
            ((PhotoViewer.PlaceProviderObject)localObject).viewY = (i - paramInt);
            ((PhotoViewer.PlaceProviderObject)localObject).parentView = this.listView;
            ((PhotoViewer.PlaceProviderObject)localObject).imageReceiver = paramFileLocation.getImageReceiver();
            ((PhotoViewer.PlaceProviderObject)localObject).thumb = ((PhotoViewer.PlaceProviderObject)localObject).imageReceiver.getBitmap();
            ((PhotoViewer.PlaceProviderObject)localObject).parentView.getLocationInWindow(paramMessageObject);
            ((PhotoViewer.PlaceProviderObject)localObject).clipTopAddition = AndroidUtilities.dp(40.0F);
            return localObject;
          }
        }
        i += 1;
      }
    }
    return (PhotoViewer.PlaceProviderObject)null;
  }

  public int getSelectedCount()
  {
    return 0;
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    11 local11 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        int i = MediaActivity.this.listView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          View localView = MediaActivity.this.listView.getChildAt(paramInt);
          if ((localView instanceof SharedPhotoVideoCell))
            ((SharedPhotoVideoCell)localView).updateCheckboxColor();
          paramInt += 1;
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.progressView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.dropDown, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.dropDown, 0, null, null, new Drawable[] { this.dropDownDrawable }, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, null, null, null, null, "actionBarActionModeDefaultIcon");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, null, null, null, null, "actionBarActionModeDefault");
    ThemeDescription localThemeDescription17 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, null, null, null, null, "actionBarActionModeDefaultTop");
    ThemeDescription localThemeDescription18 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, "actionBarActionModeDefaultSelector");
    ThemeDescription localThemeDescription19 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
    ThemeDescription localThemeDescription20 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
    ThemeDescription localThemeDescription21 = new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "actionBarActionModeDefaultIcon");
    ThemeDescription localThemeDescription22 = new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
    ThemeDescription localThemeDescription23 = new ThemeDescription(this.emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2");
    ThemeDescription localThemeDescription24 = new ThemeDescription(this.listView, 0, new Class[] { GraySectionCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText2");
    ThemeDescription localThemeDescription25 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { GraySectionCell.class }, null, null, null, "graySection");
    ThemeDescription localThemeDescription26 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { SharedDocumentCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription27 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { SharedDocumentCell.class }, new String[] { "dateTextView" }, null, null, null, "windowBackgroundWhiteGrayText3");
    ThemeDescription localThemeDescription28 = new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[] { SharedDocumentCell.class }, new String[] { "progressView" }, null, null, null, "sharedMedia_startStopLoadIcon");
    ThemeDescription localThemeDescription29 = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[] { SharedDocumentCell.class }, new String[] { "statusImageView" }, null, null, null, "sharedMedia_startStopLoadIcon");
    ThemeDescription localThemeDescription30 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[] { SharedDocumentCell.class }, new String[] { "checkBox" }, null, null, null, "checkbox");
    ThemeDescription localThemeDescription31 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { SharedDocumentCell.class }, new String[] { "checkBox" }, null, null, null, "checkboxCheck");
    ThemeDescription localThemeDescription32 = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[] { SharedDocumentCell.class }, new String[] { "thumbImageView" }, null, null, null, "files_folderIcon");
    ThemeDescription localThemeDescription33 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { SharedDocumentCell.class }, new String[] { "extTextView" }, null, null, null, "files_iconText");
    ThemeDescription localThemeDescription34 = new ThemeDescription(this.listView, 0, new Class[] { GraySectionCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText2");
    ThemeDescription localThemeDescription35 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { GraySectionCell.class }, null, null, null, "graySection");
    ThemeDescription localThemeDescription36 = new ThemeDescription(this.listView, 0, new Class[] { LoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle");
    ThemeDescription localThemeDescription37 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[] { SharedLinkCell.class }, new String[] { "checkBox" }, null, null, null, "checkbox");
    ThemeDescription localThemeDescription38 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { SharedLinkCell.class }, new String[] { "checkBox" }, null, null, null, "checkboxCheck");
    ThemeDescription localThemeDescription39 = new ThemeDescription(this.listView, 0, new Class[] { SharedLinkCell.class }, new String[] { "titleTextPaint" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription40 = new ThemeDescription(this.listView, 0, new Class[] { SharedLinkCell.class }, null, null, null, "windowBackgroundWhiteLinkText");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.linkSelectionPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localThemeDescription16, localThemeDescription17, localThemeDescription18, localThemeDescription19, localThemeDescription20, localThemeDescription21, localThemeDescription22, localThemeDescription23, localThemeDescription24, localThemeDescription25, localThemeDescription26, localThemeDescription27, localThemeDescription28, localThemeDescription29, localThemeDescription30, localThemeDescription31, localThemeDescription32, localThemeDescription33, localThemeDescription34, localThemeDescription35, localThemeDescription36, localThemeDescription37, localThemeDescription38, localThemeDescription39, localThemeDescription40, new ThemeDescription(localRecyclerListView, 0, new Class[] { SharedLinkCell.class }, localPaint, null, null, "windowBackgroundWhiteLinkSelection"), new ThemeDescription(this.listView, 0, new Class[] { SharedLinkCell.class }, new String[] { "letterDrawable" }, null, null, null, "sharedMedia_linkPlaceholderText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { SharedLinkCell.class }, new String[] { "letterDrawable" }, null, null, null, "sharedMedia_linkPlaceholder"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { SharedMediaSectionCell.class }, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[] { SharedMediaSectionCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { SharedMediaSectionCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[] { SharedPhotoVideoCell.class }, null, null, local11, "checkbox"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { SharedPhotoVideoCell.class }, null, null, local11, "checkboxCheck"), new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { FragmentContextView.class }, new String[] { "frameLayout" }, null, null, null, "inappPlayerBackground"), new ThemeDescription(this.fragmentContextView, 0, new Class[] { FragmentContextView.class }, new String[] { "playButton" }, null, null, null, "inappPlayerPlayPause"), new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { FragmentContextView.class }, new String[] { "titleTextView" }, null, null, null, "inappPlayerTitle"), new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { FragmentContextView.class }, new String[] { "frameLayout" }, null, null, null, "inappPlayerPerformer"), new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { FragmentContextView.class }, new String[] { "closeButton" }, null, null, null, "inappPlayerClose") };
  }

  public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    return null;
  }

  public boolean isPhotoChecked(int paramInt)
  {
    return false;
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.listView != null)
      this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          MediaActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
          MediaActivity.this.fixLayoutInternal();
          return true;
        }
      });
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    if (getArguments().getBoolean("CallHistory", false))
      return true;
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
    this.dialog_id = getArguments().getLong("dialog_id", 0L);
    int i = 0;
    if (i < this.sharedMediaData.length)
    {
      this.sharedMediaData[i] = new SharedMediaData(null);
      int[] arrayOfInt = this.sharedMediaData[i].max_id;
      if ((int)this.dialog_id == 0);
      for (int j = -2147483648; ; j = 2147483647)
      {
        arrayOfInt[0] = j;
        if ((this.mergeDialogId != 0L) && (this.info != null))
        {
          this.sharedMediaData[i].max_id[1] = this.info.migrated_from_max_id;
          this.sharedMediaData[i].endReached[1] = 0;
        }
        i += 1;
        break;
      }
    }
    SharedMediaData.access$402(this.sharedMediaData[0], true);
    SharedMediaQuery.loadMedia(this.dialog_id, 0, 50, 0, 0, true, this.classGuid);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
  }

  public void onPause()
  {
    super.onPause();
    if (this.dropDownContainer != null)
      this.dropDownContainer.closeSubMenu();
  }

  public void onResume()
  {
    super.onResume();
    this.scrolling = true;
    if (this.photoVideoAdapter != null)
      this.photoVideoAdapter.notifyDataSetChanged();
    if (this.documentsAdapter != null)
      this.documentsAdapter.notifyDataSetChanged();
    if (this.linksAdapter != null)
      this.linksAdapter.notifyDataSetChanged();
    fixLayoutInternal();
  }

  public boolean processSendingText(CharSequence paramCharSequence)
  {
    int k = 0;
    paramCharSequence = AndroidUtilities.getTrimmedString(paramCharSequence);
    if (paramCharSequence.length() != 0)
    {
      int j = (int)Math.ceil(paramCharSequence.length() / 4096.0F);
      int i = 0;
      while (i < j)
      {
        CharSequence[] arrayOfCharSequence = new CharSequence[1];
        arrayOfCharSequence[0] = paramCharSequence.subSequence(i * 4096, Math.min((i + 1) * 4096, paramCharSequence.length()));
        ArrayList localArrayList = MessagesQuery.getEntities(arrayOfCharSequence);
        SendMessagesHelper.getInstance().sendMessage(arrayOfCharSequence[0].toString(), this.user_id, null, null, false, localArrayList, null, null);
        i += 1;
      }
      k = 1;
    }
    return k;
  }

  public boolean scaleToFill()
  {
    return false;
  }

  public void sendButtonPressed(int paramInt, VideoEditedInfo paramVideoEditedInfo)
  {
  }

  public void setChatInfo(TLRPC.ChatFull paramChatFull)
  {
    this.info = paramChatFull;
    if ((this.info != null) && (this.info.migrated_from_chat_id != 0))
      this.mergeDialogId = (-this.info.migrated_from_chat_id);
  }

  public void setMergeDialogId(long paramLong)
  {
    this.mergeDialogId = paramLong;
  }

  public void setPhotoChecked(int paramInt)
  {
  }

  public void updatePhotoAtIndex(int paramInt)
  {
  }

  public void willHidePhotoViewer()
  {
  }

  public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
  }

  public class MediaSearchAdapter extends RecyclerListView.SelectionAdapter
  {
    private int currentType;
    protected ArrayList<MessageObject> globalSearch = new ArrayList();
    private int lastReqId;
    private Context mContext;
    private int reqId = 0;
    private ArrayList<MessageObject> searchResult = new ArrayList();
    private Timer searchTimer;

    public MediaSearchAdapter(Context paramInt, int arg3)
    {
      this.mContext = paramInt;
      int i;
      this.currentType = i;
    }

    private void processSearch(String paramString)
    {
      AndroidUtilities.runOnUIThread(new Runnable(paramString)
      {
        public void run()
        {
          Object localObject;
          if (!MediaActivity.SharedMediaData.access$3300(MediaActivity.this.sharedMediaData[MediaActivity.MediaSearchAdapter.this.currentType]).isEmpty())
          {
            if ((MediaActivity.MediaSearchAdapter.this.currentType != 1) && (MediaActivity.MediaSearchAdapter.this.currentType != 4))
              break label194;
            localObject = (MessageObject)MediaActivity.SharedMediaData.access$3300(MediaActivity.this.sharedMediaData[MediaActivity.MediaSearchAdapter.this.currentType]).get(MediaActivity.SharedMediaData.access$3300(MediaActivity.this.sharedMediaData[MediaActivity.MediaSearchAdapter.this.currentType]).size() - 1);
            MediaActivity.MediaSearchAdapter.this.queryServerSearch(this.val$query, ((MessageObject)localObject).getId(), ((MessageObject)localObject).getDialogId());
          }
          while (true)
          {
            if ((MediaActivity.MediaSearchAdapter.this.currentType == 1) || (MediaActivity.MediaSearchAdapter.this.currentType == 4))
            {
              localObject = new ArrayList();
              ((ArrayList)localObject).addAll(MediaActivity.SharedMediaData.access$3300(MediaActivity.this.sharedMediaData[MediaActivity.MediaSearchAdapter.this.currentType]));
              Utilities.searchQueue.postRunnable(new Runnable((ArrayList)localObject)
              {
                public void run()
                {
                  Object localObject2 = MediaActivity.MediaSearchAdapter.3.this.val$query.trim().toLowerCase();
                  if (((String)localObject2).length() == 0)
                  {
                    MediaActivity.MediaSearchAdapter.this.updateSearchResults(new ArrayList());
                    return;
                  }
                  Object localObject1 = LocaleController.getInstance().getTranslitString((String)localObject2);
                  if ((((String)localObject2).equals(localObject1)) || (((String)localObject1).length() == 0))
                    localObject1 = null;
                  while (true)
                  {
                    int i;
                    label115: MessageObject localMessageObject;
                    int j;
                    label141: String str;
                    if (localObject1 != null)
                    {
                      i = 1;
                      String[] arrayOfString = new String[i + 1];
                      arrayOfString[0] = localObject2;
                      if (localObject1 != null)
                        arrayOfString[1] = localObject1;
                      localObject2 = new ArrayList();
                      i = 0;
                      if (i >= this.val$copy.size())
                        break label378;
                      localMessageObject = (MessageObject)this.val$copy.get(i);
                      j = 0;
                      if (j >= arrayOfString.length)
                        break label207;
                      str = arrayOfString[j];
                      localObject1 = localMessageObject.getDocumentName();
                      if ((localObject1 != null) && (((String)localObject1).length() != 0))
                        break label186;
                    }
                    label391: label394: label400: label404: 
                    while (true)
                    {
                      j += 1;
                      break label141;
                      i = 0;
                      break;
                      label186: if (((String)localObject1).toLowerCase().contains(str))
                      {
                        ((ArrayList)localObject2).add(localMessageObject);
                        label207: i += 1;
                        break label115;
                      }
                      if (MediaActivity.MediaSearchAdapter.this.currentType != 4)
                        continue;
                      label252: int k;
                      label254: boolean bool;
                      if (localMessageObject.type == 0)
                      {
                        localObject1 = localMessageObject.messageOwner.media.webpage.document;
                        k = 0;
                        if (k >= ((TLRPC.Document)localObject1).attributes.size())
                          break label400;
                        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject1).attributes.get(k);
                        if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio))
                          break label371;
                        if (localDocumentAttribute.performer == null)
                          break label394;
                        bool = localDocumentAttribute.performer.toLowerCase().contains(str);
                        label311: if ((bool) || (localDocumentAttribute.title == null))
                          break label391;
                        bool = localDocumentAttribute.title.toLowerCase().contains(str);
                      }
                      while (true)
                      {
                        if (!bool)
                          break label404;
                        ((ArrayList)localObject2).add(localMessageObject);
                        break;
                        localObject1 = localMessageObject.messageOwner.media.document;
                        break label252;
                        label371: k += 1;
                        break label254;
                        label378: MediaActivity.MediaSearchAdapter.this.updateSearchResults((ArrayList)localObject2);
                        return;
                        continue;
                        bool = false;
                        break label311;
                        bool = false;
                      }
                    }
                  }
                }
              });
            }
            return;
            label194: if (MediaActivity.MediaSearchAdapter.this.currentType != 3)
              continue;
            MediaActivity.MediaSearchAdapter.this.queryServerSearch(this.val$query, 0, MediaActivity.this.dialog_id);
          }
        }
      });
    }

    private void updateSearchResults(ArrayList<MessageObject> paramArrayList)
    {
      AndroidUtilities.runOnUIThread(new Runnable(paramArrayList)
      {
        public void run()
        {
          MediaActivity.MediaSearchAdapter.access$5102(MediaActivity.MediaSearchAdapter.this, this.val$documents);
          MediaActivity.MediaSearchAdapter.this.notifyDataSetChanged();
        }
      });
    }

    public MessageObject getItem(int paramInt)
    {
      if (paramInt < this.searchResult.size())
        return (MessageObject)this.searchResult.get(paramInt);
      return (MessageObject)this.globalSearch.get(paramInt - this.searchResult.size());
    }

    public int getItemCount()
    {
      int j = this.searchResult.size();
      int k = this.globalSearch.size();
      int i = j;
      if (k != 0)
        i = j + k;
      return i;
    }

    public int getItemViewType(int paramInt)
    {
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return paramViewHolder.getItemViewType() != this.searchResult.size() + this.globalSearch.size();
    }

    public boolean isGlobalSearch(int paramInt)
    {
      int i = this.searchResult.size();
      int j = this.globalSearch.size();
      if ((paramInt >= 0) && (paramInt < i));
      do
        return false;
      while ((paramInt <= i) || (paramInt > i + j));
      return true;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool4 = true;
      boolean bool3 = true;
      boolean bool5 = true;
      boolean bool2 = true;
      HashMap[] arrayOfHashMap;
      if ((this.currentType == 1) || (this.currentType == 4))
      {
        paramViewHolder = (SharedDocumentCell)paramViewHolder.itemView;
        localMessageObject = getItem(paramInt);
        if (paramInt != getItemCount() - 1)
        {
          bool1 = true;
          paramViewHolder.setDocument(localMessageObject, bool1);
          if (!MediaActivity.this.actionBar.isActionModeShowed())
            break label155;
          arrayOfHashMap = MediaActivity.this.selectedFiles;
          if (localMessageObject.getDialogId() != MediaActivity.this.dialog_id)
            break label145;
          paramInt = 0;
          label102: bool3 = arrayOfHashMap[paramInt].containsKey(Integer.valueOf(localMessageObject.getId()));
          if (MediaActivity.this.scrolling)
            break label150;
          bool1 = bool2;
          label132: paramViewHolder.setChecked(bool3, bool1);
        }
      }
      label145: label150: label155: 
      do
      {
        return;
        bool1 = false;
        break;
        paramInt = 1;
        break label102;
        bool1 = false;
        break label132;
        if (!MediaActivity.this.scrolling);
        for (bool1 = bool4; ; bool1 = false)
        {
          paramViewHolder.setChecked(false, bool1);
          return;
        }
      }
      while (this.currentType != 3);
      paramViewHolder = (SharedLinkCell)paramViewHolder.itemView;
      MessageObject localMessageObject = getItem(paramInt);
      if (paramInt != getItemCount() - 1)
      {
        bool1 = true;
        paramViewHolder.setLink(localMessageObject, bool1);
        if (!MediaActivity.this.actionBar.isActionModeShowed())
          break label315;
        arrayOfHashMap = MediaActivity.this.selectedFiles;
        if (localMessageObject.getDialogId() != MediaActivity.this.dialog_id)
          break label305;
        paramInt = 0;
        label262: bool2 = arrayOfHashMap[paramInt].containsKey(Integer.valueOf(localMessageObject.getId()));
        if (MediaActivity.this.scrolling)
          break label310;
      }
      label305: label310: for (boolean bool1 = bool3; ; bool1 = false)
      {
        paramViewHolder.setChecked(bool2, bool1);
        return;
        bool1 = false;
        break;
        paramInt = 1;
        break label262;
      }
      label315: if (!MediaActivity.this.scrolling);
      for (bool1 = bool5; ; bool1 = false)
      {
        paramViewHolder.setChecked(false, bool1);
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      if ((this.currentType == 1) || (this.currentType == 4))
        paramViewGroup = new SharedDocumentCell(this.mContext);
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new SharedLinkCell(this.mContext);
        ((SharedLinkCell)paramViewGroup).setDelegate(new SharedLinkCell.SharedLinkCellDelegate()
        {
          public boolean canPerformActions()
          {
            return !MediaActivity.this.actionBar.isActionModeShowed();
          }

          public void needOpenWebView(TLRPC.WebPage paramWebPage)
          {
            MediaActivity.this.openWebView(paramWebPage);
          }
        });
      }
    }

    public void queryServerSearch(String paramString, int paramInt, long paramLong)
    {
      int i = (int)paramLong;
      if (i == 0)
        return;
      if (this.reqId != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
        this.reqId = 0;
      }
      if ((paramString == null) || (paramString.length() == 0))
      {
        this.globalSearch.clear();
        this.lastReqId = 0;
        notifyDataSetChanged();
        return;
      }
      TLRPC.TL_messages_search localTL_messages_search = new TLRPC.TL_messages_search();
      localTL_messages_search.offset = 0;
      localTL_messages_search.limit = 50;
      localTL_messages_search.max_id = paramInt;
      if (this.currentType == 1)
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterDocument();
      while (true)
      {
        localTL_messages_search.q = paramString;
        localTL_messages_search.peer = MessagesController.getInputPeer(i);
        if (localTL_messages_search.peer == null)
          break;
        i = this.lastReqId + 1;
        this.lastReqId = i;
        this.reqId = ConnectionsManager.getInstance().sendRequest(localTL_messages_search, new RequestDelegate(paramInt, i)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            ArrayList localArrayList = new ArrayList();
            if (paramTL_error == null)
            {
              paramTLObject = (TLRPC.messages_Messages)paramTLObject;
              int i = 0;
              if (i < paramTLObject.messages.size())
              {
                paramTL_error = (TLRPC.Message)paramTLObject.messages.get(i);
                if ((this.val$max_id != 0) && (paramTL_error.id > this.val$max_id));
                while (true)
                {
                  i += 1;
                  break;
                  localArrayList.add(new MessageObject(paramTL_error, null, false));
                }
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable(localArrayList)
            {
              public void run()
              {
                if (MediaActivity.MediaSearchAdapter.1.this.val$currentReqId == MediaActivity.MediaSearchAdapter.this.lastReqId)
                {
                  MediaActivity.MediaSearchAdapter.this.globalSearch = this.val$messageObjects;
                  MediaActivity.MediaSearchAdapter.this.notifyDataSetChanged();
                }
                MediaActivity.MediaSearchAdapter.access$4502(MediaActivity.MediaSearchAdapter.this, 0);
              }
            });
          }
        }
        , 2);
        ConnectionsManager.getInstance().bindRequestToGuid(this.reqId, MediaActivity.this.classGuid);
        return;
        if (this.currentType == 3)
        {
          localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterUrl();
          continue;
        }
        if (this.currentType != 4)
          continue;
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterMusic();
      }
    }

    public void search(String paramString)
    {
      try
      {
        if (this.searchTimer != null)
          this.searchTimer.cancel();
        if (paramString == null)
        {
          this.searchResult.clear();
          notifyDataSetChanged();
          return;
        }
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask(paramString)
        {
          public void run()
          {
            try
            {
              MediaActivity.MediaSearchAdapter.this.searchTimer.cancel();
              MediaActivity.MediaSearchAdapter.access$4702(MediaActivity.MediaSearchAdapter.this, null);
              MediaActivity.MediaSearchAdapter.this.processSearch(this.val$query);
              return;
            }
            catch (Exception localException)
            {
              while (true)
                FileLog.e(localException);
            }
          }
        }
        , 200L, 300L);
      }
    }
  }

  private class SharedDocumentsAdapter extends RecyclerListView.SectionsAdapter
  {
    private int currentType;
    private Context mContext;

    public SharedDocumentsAdapter(Context paramInt, int arg3)
    {
      this.mContext = paramInt;
      int i;
      this.currentType = i;
    }

    public int getCountForSection(int paramInt)
    {
      if (paramInt < MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[this.currentType]).size())
        return ((ArrayList)MediaActivity.SharedMediaData.access$3600(MediaActivity.this.sharedMediaData[this.currentType]).get(MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[this.currentType]).get(paramInt))).size() + 1;
      return 1;
    }

    public Object getItem(int paramInt1, int paramInt2)
    {
      return null;
    }

    public int getItemViewType(int paramInt1, int paramInt2)
    {
      if (paramInt1 < MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[this.currentType]).size())
      {
        if (paramInt2 == 0)
          return 0;
        return 1;
      }
      return 2;
    }

    public String getLetter(int paramInt)
    {
      return null;
    }

    public int getPositionForScrollProgress(float paramFloat)
    {
      return 0;
    }

    public int getSectionCount()
    {
      int j = 1;
      int k = MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[this.currentType]).size();
      int i;
      if (!MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[this.currentType]).isEmpty())
      {
        i = j;
        if (MediaActivity.SharedMediaData.access$300(MediaActivity.this.sharedMediaData[this.currentType])[0] != 0)
        {
          i = j;
          if (MediaActivity.SharedMediaData.access$300(MediaActivity.this.sharedMediaData[this.currentType])[1] == 0);
        }
      }
      else
      {
        i = 0;
      }
      return i + k;
    }

    public View getSectionHeaderView(int paramInt, View paramView)
    {
      if (paramView == null)
        paramView = new GraySectionCell(this.mContext);
      while (true)
      {
        if (paramInt < MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[this.currentType]).size())
        {
          Object localObject = (String)MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[this.currentType]).get(paramInt);
          localObject = (MessageObject)((ArrayList)MediaActivity.SharedMediaData.access$3600(MediaActivity.this.sharedMediaData[this.currentType]).get(localObject)).get(0);
          ((GraySectionCell)paramView).setText(LocaleController.getInstance().formatterMonthYear.format(((MessageObject)localObject).messageOwner.date * 1000L).toUpperCase());
        }
        return paramView;
      }
    }

    public boolean isEnabled(int paramInt1, int paramInt2)
    {
      return paramInt2 != 0;
    }

    public void onBindViewHolder(int paramInt1, int paramInt2, RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool3 = true;
      boolean bool2 = true;
      Object localObject;
      if (paramViewHolder.getItemViewType() != 2)
      {
        localObject = (String)MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[this.currentType]).get(paramInt1);
        localObject = (ArrayList)MediaActivity.SharedMediaData.access$3600(MediaActivity.this.sharedMediaData[this.currentType]).get(localObject);
      }
      switch (paramViewHolder.getItemViewType())
      {
      default:
        return;
      case 0:
        localObject = (MessageObject)((ArrayList)localObject).get(0);
        ((GraySectionCell)paramViewHolder.itemView).setText(LocaleController.getInstance().formatterMonthYear.format(((MessageObject)localObject).messageOwner.date * 1000L).toUpperCase());
        return;
      case 1:
      }
      paramViewHolder = (SharedDocumentCell)paramViewHolder.itemView;
      MessageObject localMessageObject = (MessageObject)((ArrayList)localObject).get(paramInt2 - 1);
      if ((paramInt2 != ((ArrayList)localObject).size()) || ((paramInt1 == MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[this.currentType]).size() - 1) && (MediaActivity.SharedMediaData.access$400(MediaActivity.this.sharedMediaData[this.currentType]))))
      {
        bool1 = true;
        paramViewHolder.setDocument(localMessageObject, bool1);
        if (!MediaActivity.this.actionBar.isActionModeShowed())
          break label316;
        localObject = MediaActivity.this.selectedFiles;
        if (localMessageObject.getDialogId() != MediaActivity.this.dialog_id)
          break label305;
        paramInt1 = 0;
        label259: bool3 = localObject[paramInt1].containsKey(Integer.valueOf(localMessageObject.getId()));
        if (MediaActivity.this.scrolling)
          break label310;
      }
      label305: label310: for (boolean bool1 = bool2; ; bool1 = false)
      {
        paramViewHolder.setChecked(bool3, bool1);
        return;
        bool1 = false;
        break;
        paramInt1 = 1;
        break label259;
      }
      label316: if (!MediaActivity.this.scrolling);
      for (bool1 = bool3; ; bool1 = false)
      {
        paramViewHolder.setChecked(false, bool1);
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new LoadingCell(this.mContext);
      case 0:
      case 1:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new GraySectionCell(this.mContext);
        continue;
        paramViewGroup = new SharedDocumentCell(this.mContext);
      }
    }
  }

  private class SharedLinksAdapter extends RecyclerListView.SectionsAdapter
  {
    private Context mContext;

    public SharedLinksAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
    }

    public int getCountForSection(int paramInt)
    {
      if (paramInt < MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[3]).size())
        return ((ArrayList)MediaActivity.SharedMediaData.access$3600(MediaActivity.this.sharedMediaData[3]).get(MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[3]).get(paramInt))).size() + 1;
      return 1;
    }

    public Object getItem(int paramInt1, int paramInt2)
    {
      return null;
    }

    public int getItemViewType(int paramInt1, int paramInt2)
    {
      if (paramInt1 < MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[3]).size())
      {
        if (paramInt2 == 0)
          return 0;
        return 1;
      }
      return 2;
    }

    public String getLetter(int paramInt)
    {
      return null;
    }

    public int getPositionForScrollProgress(float paramFloat)
    {
      return 0;
    }

    public int getSectionCount()
    {
      int j = 1;
      int k = MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[3]).size();
      int i;
      if (!MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[3]).isEmpty())
      {
        i = j;
        if (MediaActivity.SharedMediaData.access$300(MediaActivity.this.sharedMediaData[3])[0] != 0)
        {
          i = j;
          if (MediaActivity.SharedMediaData.access$300(MediaActivity.this.sharedMediaData[3])[1] == 0);
        }
      }
      else
      {
        i = 0;
      }
      return i + k;
    }

    public View getSectionHeaderView(int paramInt, View paramView)
    {
      if (paramView == null)
        paramView = new GraySectionCell(this.mContext);
      while (true)
      {
        if (paramInt < MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[3]).size())
        {
          Object localObject = (String)MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[3]).get(paramInt);
          localObject = (MessageObject)((ArrayList)MediaActivity.SharedMediaData.access$3600(MediaActivity.this.sharedMediaData[3]).get(localObject)).get(0);
          ((GraySectionCell)paramView).setText(LocaleController.getInstance().formatterMonthYear.format(((MessageObject)localObject).messageOwner.date * 1000L).toUpperCase());
        }
        return paramView;
      }
    }

    public boolean isEnabled(int paramInt1, int paramInt2)
    {
      return paramInt2 != 0;
    }

    public void onBindViewHolder(int paramInt1, int paramInt2, RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool3 = true;
      boolean bool2 = true;
      Object localObject;
      if (paramViewHolder.getItemViewType() != 2)
      {
        localObject = (String)MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[3]).get(paramInt1);
        localObject = (ArrayList)MediaActivity.SharedMediaData.access$3600(MediaActivity.this.sharedMediaData[3]).get(localObject);
      }
      switch (paramViewHolder.getItemViewType())
      {
      default:
        return;
      case 0:
        localObject = (MessageObject)((ArrayList)localObject).get(0);
        ((GraySectionCell)paramViewHolder.itemView).setText(LocaleController.getInstance().formatterMonthYear.format(((MessageObject)localObject).messageOwner.date * 1000L).toUpperCase());
        return;
      case 1:
      }
      paramViewHolder = (SharedLinkCell)paramViewHolder.itemView;
      MessageObject localMessageObject = (MessageObject)((ArrayList)localObject).get(paramInt2 - 1);
      if ((paramInt2 != ((ArrayList)localObject).size()) || ((paramInt1 == MediaActivity.SharedMediaData.access$3500(MediaActivity.this.sharedMediaData[3]).size() - 1) && (MediaActivity.SharedMediaData.access$400(MediaActivity.this.sharedMediaData[3]))))
      {
        bool1 = true;
        paramViewHolder.setLink(localMessageObject, bool1);
        if (!MediaActivity.this.actionBar.isActionModeShowed())
          break label306;
        localObject = MediaActivity.this.selectedFiles;
        if (localMessageObject.getDialogId() != MediaActivity.this.dialog_id)
          break label295;
        paramInt1 = 0;
        label249: bool3 = localObject[paramInt1].containsKey(Integer.valueOf(localMessageObject.getId()));
        if (MediaActivity.this.scrolling)
          break label300;
      }
      label295: label300: for (boolean bool1 = bool2; ; bool1 = false)
      {
        paramViewHolder.setChecked(bool3, bool1);
        return;
        bool1 = false;
        break;
        paramInt1 = 1;
        break label249;
      }
      label306: if (!MediaActivity.this.scrolling);
      for (bool1 = bool3; ; bool1 = false)
      {
        paramViewHolder.setChecked(false, bool1);
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new LoadingCell(this.mContext);
      case 0:
      case 1:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new GraySectionCell(this.mContext);
        continue;
        paramViewGroup = new SharedLinkCell(this.mContext);
        ((SharedLinkCell)paramViewGroup).setDelegate(new SharedLinkCell.SharedLinkCellDelegate()
        {
          public boolean canPerformActions()
          {
            return !MediaActivity.this.actionBar.isActionModeShowed();
          }

          public void needOpenWebView(TLRPC.WebPage paramWebPage)
          {
            MediaActivity.this.openWebView(paramWebPage);
          }
        });
      }
    }
  }

  private class SharedMediaData
  {
    private boolean[] endReached = { 0, 1 };
    private boolean loading;
    private int[] max_id = { 0, 0 };
    private ArrayList<MessageObject> messages = new ArrayList();
    private HashMap<Integer, MessageObject>[] messagesDict = { new HashMap(), new HashMap() };
    private HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap();
    private ArrayList<String> sections = new ArrayList();
    private int totalCount;

    private SharedMediaData()
    {
    }

    public boolean addMessage(MessageObject paramMessageObject, boolean paramBoolean1, boolean paramBoolean2)
    {
      if (paramMessageObject.getDialogId() == MediaActivity.this.dialog_id);
      for (int i = 0; this.messagesDict[i].containsKey(Integer.valueOf(paramMessageObject.getId())); i = 1)
        return false;
      ArrayList localArrayList2 = (ArrayList)this.sectionArrays.get(paramMessageObject.monthKey);
      ArrayList localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.sectionArrays.put(paramMessageObject.monthKey, localArrayList1);
        if (paramBoolean1)
          this.sections.add(0, paramMessageObject.monthKey);
      }
      else
      {
        if (!paramBoolean1)
          break label198;
        localArrayList1.add(0, paramMessageObject);
        this.messages.add(0, paramMessageObject);
        label130: this.messagesDict[i].put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
        if (paramBoolean2)
          break label217;
        if (paramMessageObject.getId() > 0)
          this.max_id[i] = Math.min(paramMessageObject.getId(), this.max_id[i]);
      }
      while (true)
      {
        return true;
        this.sections.add(paramMessageObject.monthKey);
        break;
        label198: localArrayList1.add(paramMessageObject);
        this.messages.add(paramMessageObject);
        break label130;
        label217: this.max_id[i] = Math.max(paramMessageObject.getId(), this.max_id[i]);
      }
    }

    public boolean deleteMessage(int paramInt1, int paramInt2)
    {
      MessageObject localMessageObject = (MessageObject)this.messagesDict[paramInt2].get(Integer.valueOf(paramInt1));
      if (localMessageObject == null)
        return false;
      ArrayList localArrayList = (ArrayList)this.sectionArrays.get(localMessageObject.monthKey);
      if (localArrayList == null)
        return false;
      localArrayList.remove(localMessageObject);
      this.messages.remove(localMessageObject);
      this.messagesDict[paramInt2].remove(Integer.valueOf(localMessageObject.getId()));
      if (localArrayList.isEmpty())
      {
        this.sectionArrays.remove(localMessageObject.monthKey);
        this.sections.remove(localMessageObject.monthKey);
      }
      this.totalCount -= 1;
      return true;
    }

    public void replaceMid(int paramInt1, int paramInt2)
    {
      MessageObject localMessageObject = (MessageObject)this.messagesDict[0].get(Integer.valueOf(paramInt1));
      if (localMessageObject != null)
      {
        this.messagesDict[0].remove(Integer.valueOf(paramInt1));
        this.messagesDict[0].put(Integer.valueOf(paramInt2), localMessageObject);
        localMessageObject.messageOwner.id = paramInt2;
      }
    }
  }

  private class SharedPhotoVideoAdapter extends RecyclerListView.SectionsAdapter
  {
    private Context mContext;

    public SharedPhotoVideoAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
    }

    public int getCountForSection(int paramInt)
    {
      if (paramInt < MediaActivity.access$2700(MediaActivity.this)[0].sections.size())
        return (int)Math.ceil(((ArrayList)MediaActivity.access$2700(MediaActivity.this)[0].sectionArrays.get(MediaActivity.access$2700(MediaActivity.this)[0].sections.get(paramInt))).size() / MediaActivity.this.columnsCount) + 1;
      return 1;
    }

    public Object getItem(int paramInt1, int paramInt2)
    {
      return null;
    }

    public int getItemViewType(int paramInt1, int paramInt2)
    {
      if (paramInt1 < MediaActivity.access$2700(MediaActivity.this)[0].sections.size())
      {
        if (paramInt2 == 0)
          return 0;
        return 1;
      }
      return 2;
    }

    public String getLetter(int paramInt)
    {
      return null;
    }

    public int getPositionForScrollProgress(float paramFloat)
    {
      return 0;
    }

    public int getSectionCount()
    {
      int j = 1;
      int k = MediaActivity.access$2700(MediaActivity.this)[0].sections.size();
      int i;
      if (!MediaActivity.access$2700(MediaActivity.this)[0].sections.isEmpty())
      {
        i = j;
        if (MediaActivity.access$2700(MediaActivity.this)[0].endReached[0] != 0)
        {
          i = j;
          if (MediaActivity.access$2700(MediaActivity.this)[0].endReached[1] == 0);
        }
      }
      else
      {
        i = 0;
      }
      return i + k;
    }

    public View getSectionHeaderView(int paramInt, View paramView)
    {
      if (paramView == null)
      {
        paramView = new SharedMediaSectionCell(this.mContext);
        paramView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
      while (true)
      {
        if (paramInt < MediaActivity.access$2700(MediaActivity.this)[0].sections.size())
        {
          Object localObject = (String)MediaActivity.access$2700(MediaActivity.this)[0].sections.get(paramInt);
          localObject = (MessageObject)((ArrayList)MediaActivity.access$2700(MediaActivity.this)[0].sectionArrays.get(localObject)).get(0);
          ((SharedMediaSectionCell)paramView).setText(LocaleController.getInstance().formatterMonthYear.format(((MessageObject)localObject).messageOwner.date * 1000L).toUpperCase());
        }
        return paramView;
      }
    }

    public boolean isEnabled(int paramInt1, int paramInt2)
    {
      return false;
    }

    public void onBindViewHolder(int paramInt1, int paramInt2, RecyclerView.ViewHolder paramViewHolder)
    {
      Object localObject;
      if (paramViewHolder.getItemViewType() != 2)
      {
        localObject = (String)MediaActivity.access$2700(MediaActivity.this)[0].sections.get(paramInt1);
        localObject = (ArrayList)MediaActivity.access$2700(MediaActivity.this)[0].sectionArrays.get(localObject);
      }
      switch (paramViewHolder.getItemViewType())
      {
      default:
        return;
      case 0:
        localObject = (MessageObject)((ArrayList)localObject).get(0);
        ((SharedMediaSectionCell)paramViewHolder.itemView).setText(LocaleController.getInstance().formatterMonthYear.format(((MessageObject)localObject).messageOwner.date * 1000L).toUpperCase());
        return;
      case 1:
      }
      paramViewHolder = (SharedPhotoVideoCell)paramViewHolder.itemView;
      paramViewHolder.setItemsCount(MediaActivity.this.columnsCount);
      paramInt1 = 0;
      if (paramInt1 < MediaActivity.this.columnsCount)
      {
        int i = (paramInt2 - 1) * MediaActivity.this.columnsCount + paramInt1;
        boolean bool1;
        if (i < ((ArrayList)localObject).size())
        {
          MessageObject localMessageObject = (MessageObject)((ArrayList)localObject).get(i);
          if (paramInt2 == 1)
          {
            bool1 = true;
            label201: paramViewHolder.setIsFirst(bool1);
            paramViewHolder.setItem(paramInt1, MediaActivity.access$2700(MediaActivity.this)[0].messages.indexOf(localMessageObject), localMessageObject);
            if (!MediaActivity.this.actionBar.isActionModeShowed())
              break label337;
            HashMap[] arrayOfHashMap = MediaActivity.this.selectedFiles;
            if (localMessageObject.getDialogId() != MediaActivity.this.dialog_id)
              break label325;
            i = 0;
            label272: boolean bool2 = arrayOfHashMap[i].containsKey(Integer.valueOf(localMessageObject.getId()));
            if (MediaActivity.this.scrolling)
              break label331;
            bool1 = true;
            label303: paramViewHolder.setChecked(paramInt1, bool2, bool1);
          }
        }
        while (true)
        {
          paramInt1 += 1;
          break;
          bool1 = false;
          break label201;
          label325: i = 1;
          break label272;
          label331: bool1 = false;
          break label303;
          label337: if (!MediaActivity.this.scrolling);
          for (bool1 = true; ; bool1 = false)
          {
            paramViewHolder.setChecked(paramInt1, false, bool1);
            break;
          }
          paramViewHolder.setItem(paramInt1, i, null);
        }
      }
      paramViewHolder.requestLayout();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
      case 0:
        for (paramViewGroup = new LoadingCell(this.mContext); ; paramViewGroup = new SharedMediaSectionCell(this.mContext))
          return new RecyclerListView.Holder(paramViewGroup);
      case 1:
      }
      if (!MediaActivity.this.cellCache.isEmpty())
      {
        paramViewGroup = (View)MediaActivity.this.cellCache.get(0);
        MediaActivity.this.cellCache.remove(0);
      }
      while (true)
      {
        ((SharedPhotoVideoCell)paramViewGroup).setDelegate(new SharedPhotoVideoCell.SharedPhotoVideoCellDelegate()
        {
          public void didClickItem(SharedPhotoVideoCell paramSharedPhotoVideoCell, int paramInt1, MessageObject paramMessageObject, int paramInt2)
          {
            MediaActivity.this.onItemClick(paramInt1, paramSharedPhotoVideoCell, paramMessageObject, paramInt2);
          }

          public boolean didLongClickItem(SharedPhotoVideoCell paramSharedPhotoVideoCell, int paramInt1, MessageObject paramMessageObject, int paramInt2)
          {
            return MediaActivity.this.onItemLongClick(paramMessageObject, paramSharedPhotoVideoCell, paramInt2);
          }
        });
        break;
        paramViewGroup = new SharedPhotoVideoCell(this.mContext);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.MediaActivity
 * JD-Core Version:    0.6.0
 */