package org.vidogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Build.VERSION;
import android.provider.Settings.System;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MediaController.AlbumEntry;
import org.vidogram.messenger.MediaController.PhotoEntry;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.VideoEditedInfo;
import org.vidogram.messenger.camera.CameraController;
import org.vidogram.messenger.camera.CameraController.VideoTakeCallback;
import org.vidogram.messenger.camera.CameraSession;
import org.vidogram.messenger.camera.CameraView;
import org.vidogram.messenger.camera.CameraView.CameraViewDelegate;
import org.vidogram.messenger.query.SearchQuery;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.State;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.TL_topPeer;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BottomSheet;
import org.vidogram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface;
import org.vidogram.ui.ActionBar.BottomSheet.ContainerView;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Cells.PhotoAttachCameraCell;
import org.vidogram.ui.Cells.PhotoAttachPhotoCell;
import org.vidogram.ui.Cells.PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate;
import org.vidogram.ui.Cells.ShadowSectionCell;
import org.vidogram.ui.ChatActivity;
import org.vidogram.ui.PhotoViewer;
import org.vidogram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.vidogram.ui.PhotoViewer.PhotoViewerProvider;
import org.vidogram.ui.PhotoViewer.PlaceProviderObject;

public class ChatAttachAlert extends BottomSheet
  implements NotificationCenter.NotificationCenterDelegate, BottomSheet.BottomSheetDelegateInterface, PhotoViewer.PhotoViewerProvider
{
  private ListAdapter adapter;
  private int[] animateCameraValues = new int[5];
  private LinearLayoutManager attachPhotoLayoutManager;
  private RecyclerListView attachPhotoRecyclerView;
  private ViewGroup attachView;
  private ChatActivity baseFragment;
  private boolean cameraAnimationInProgress;
  private File cameraFile;
  private FrameLayout cameraIcon;
  private boolean cameraInitied;
  private float cameraOpenProgress;
  private boolean cameraOpened;
  private FrameLayout cameraPanel;
  private ArrayList<Object> cameraPhoto;
  private CameraView cameraView;
  private int[] cameraViewLocation = new int[2];
  private int cameraViewOffsetX;
  private int cameraViewOffsetY;
  private Paint ciclePaint = new Paint(1);
  private AnimatorSet currentHintAnimation;
  private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
  private ChatAttachViewDelegate delegate;
  private boolean deviceHasGoodCamera;
  private boolean dragging;
  private boolean flashAnimationInProgress;
  private ImageView[] flashModeButton = new ImageView[2];
  private Runnable hideHintRunnable;
  private boolean hintShowed;
  private TextView hintTextView;
  private boolean ignoreLayout;
  private ArrayList<InnerAnimator> innerAnimators = new ArrayList();
  private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5F);
  private float lastY;
  private LinearLayoutManager layoutManager;
  private View lineView;
  private RecyclerListView listView;
  private boolean loading = true;
  private boolean maybeStartDraging;
  private boolean mediaCaptured;
  private boolean paused;
  private PhotoAttachAdapter photoAttachAdapter;
  private boolean pressed;
  private EmptyTextProgressView progressView;
  private TextView recordTime;
  private boolean requestingPermissions;
  private boolean revealAnimationInProgress;
  private float revealRadius;
  private int revealX;
  private int revealY;
  private int scrollOffsetY;
  private AttachButton sendPhotosButton;
  private Drawable shadowDrawable;
  private ShutterButton shutterButton;
  private ImageView switchCameraButton;
  private boolean takingPhoto;
  private boolean useRevealAnimation;
  private Runnable videoRecordRunnable;
  private int videoRecordTime;
  private int[] viewPosition = new int[2];
  private View[] views = new View[20];
  private ArrayList<RecyclerListView.Holder> viewsCache = new ArrayList(8);

  public ChatAttachAlert(Context paramContext, ChatActivity paramChatActivity)
  {
    super(paramContext, false);
    this.baseFragment = paramChatActivity;
    this.ciclePaint.setColor(Theme.getColor("dialogBackground"));
    setDelegate(this);
    setUseRevealAnimation(true);
    checkCamera(false);
    if (this.deviceHasGoodCamera)
      CameraController.getInstance().initCamera();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.albumsDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadInlineHints);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.cameraInitied);
    this.shadowDrawable = paramContext.getResources().getDrawable(2130838062).mutate();
    Object localObject1 = new RecyclerListView(paramContext)
    {
      private int lastHeight;
      private int lastWidth;

      public void onDraw(Canvas paramCanvas)
      {
        if ((ChatAttachAlert.this.useRevealAnimation) && (Build.VERSION.SDK_INT <= 19))
        {
          paramCanvas.save();
          paramCanvas.clipRect(ChatAttachAlert.backgroundPaddingLeft, ChatAttachAlert.this.scrollOffsetY, getMeasuredWidth() - ChatAttachAlert.backgroundPaddingLeft, getMeasuredHeight());
          if (ChatAttachAlert.this.revealAnimationInProgress)
            paramCanvas.drawCircle(ChatAttachAlert.this.revealX, ChatAttachAlert.this.revealY, ChatAttachAlert.this.revealRadius, ChatAttachAlert.this.ciclePaint);
          while (true)
          {
            paramCanvas.restore();
            return;
            paramCanvas.drawRect(ChatAttachAlert.backgroundPaddingLeft, ChatAttachAlert.this.scrollOffsetY, getMeasuredWidth() - ChatAttachAlert.backgroundPaddingLeft, getMeasuredHeight(), ChatAttachAlert.this.ciclePaint);
          }
        }
        ChatAttachAlert.this.shadowDrawable.setBounds(0, ChatAttachAlert.this.scrollOffsetY - ChatAttachAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
        ChatAttachAlert.this.shadowDrawable.draw(paramCanvas);
      }

      public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
      {
        if (ChatAttachAlert.this.cameraAnimationInProgress)
          return true;
        if (ChatAttachAlert.this.cameraOpened)
          return ChatAttachAlert.this.processTouchEvent(paramMotionEvent);
        if ((paramMotionEvent.getAction() == 0) && (ChatAttachAlert.this.scrollOffsetY != 0) && (paramMotionEvent.getY() < ChatAttachAlert.this.scrollOffsetY))
        {
          ChatAttachAlert.this.dismiss();
          return true;
        }
        return super.onInterceptTouchEvent(paramMotionEvent);
      }

      protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        int m = paramInt4 - paramInt2;
        int i;
        int j;
        if (ChatAttachAlert.this.listView.getChildCount() > 0)
        {
          View localView = ChatAttachAlert.this.listView.getChildAt(ChatAttachAlert.this.listView.getChildCount() - 1);
          RecyclerListView.Holder localHolder = (RecyclerListView.Holder)ChatAttachAlert.this.listView.findContainingViewHolder(localView);
          if (localHolder != null)
          {
            i = localHolder.getAdapterPosition();
            j = localView.getTop();
          }
        }
        while (true)
        {
          if ((i >= 0) && (m - this.lastHeight != 0))
          {
            int k = j + m - this.lastHeight - getPaddingTop();
            j = i;
            i = k;
          }
          while (true)
          {
            super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
            if (j != -1)
            {
              ChatAttachAlert.access$902(ChatAttachAlert.this, true);
              ChatAttachAlert.this.layoutManager.scrollToPositionWithOffset(j, i);
              super.onLayout(false, paramInt1, paramInt2, paramInt3, paramInt4);
              ChatAttachAlert.access$902(ChatAttachAlert.this, false);
            }
            this.lastHeight = m;
            this.lastWidth = (paramInt3 - paramInt1);
            ChatAttachAlert.this.updateLayout();
            ChatAttachAlert.this.checkCameraViewPosition();
            return;
            i = 0;
            j = -1;
          }
          j = 0;
          i = -1;
        }
      }

      protected void onMeasure(int paramInt1, int paramInt2)
      {
        paramInt2 = View.MeasureSpec.getSize(paramInt2);
        int j = paramInt2;
        if (Build.VERSION.SDK_INT >= 21)
          j = paramInt2 - AndroidUtilities.statusBarHeight;
        int i = ChatAttachAlert.backgroundPaddingTop;
        int k = AndroidUtilities.dp(294.0F);
        if (SearchQuery.inlineBots.isEmpty())
        {
          paramInt2 = 0;
          k = k + i + paramInt2;
          if (k != AndroidUtilities.dp(294.0F))
            break label187;
        }
        label187: for (i = 0; ; i = Math.max(0, j - AndroidUtilities.dp(294.0F)))
        {
          paramInt2 = i;
          if (i != 0)
          {
            paramInt2 = i;
            if (k < j)
              paramInt2 = i - (j - k);
          }
          i = paramInt2;
          if (paramInt2 == 0)
            i = ChatAttachAlert.backgroundPaddingTop;
          if (getPaddingTop() != i)
          {
            ChatAttachAlert.access$902(ChatAttachAlert.this, true);
            setPadding(ChatAttachAlert.backgroundPaddingLeft, i, ChatAttachAlert.backgroundPaddingLeft, 0);
            ChatAttachAlert.access$902(ChatAttachAlert.this, false);
          }
          super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(Math.min(k, j), 1073741824));
          return;
          paramInt2 = (int)Math.ceil(SearchQuery.inlineBots.size() / 4.0F) * AndroidUtilities.dp(100.0F) + AndroidUtilities.dp(12.0F);
          break;
        }
      }

      public boolean onTouchEvent(MotionEvent paramMotionEvent)
      {
        if (ChatAttachAlert.this.cameraAnimationInProgress);
        do
        {
          return true;
          if (ChatAttachAlert.this.cameraOpened)
            return ChatAttachAlert.this.processTouchEvent(paramMotionEvent);
        }
        while ((!ChatAttachAlert.this.isDismissed()) && (super.onTouchEvent(paramMotionEvent)));
        return false;
      }

      public void requestLayout()
      {
        if (ChatAttachAlert.this.ignoreLayout)
          return;
        super.requestLayout();
      }

      public void setTranslationY(float paramFloat)
      {
        super.setTranslationY(paramFloat);
        ChatAttachAlert.this.checkCameraViewPosition();
      }
    };
    this.listView = ((RecyclerListView)localObject1);
    this.containerView = ((ViewGroup)localObject1);
    this.listView.setWillNotDraw(false);
    this.listView.setClipToPadding(false);
    localObject1 = this.listView;
    Object localObject2 = new LinearLayoutManager(getContext());
    this.layoutManager = ((LinearLayoutManager)localObject2);
    ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
    this.layoutManager.setOrientation(1);
    localObject1 = this.listView;
    localObject2 = new ListAdapter(paramContext);
    this.adapter = ((ListAdapter)localObject2);
    ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setEnabled(true);
    this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
    this.listView.addItemDecoration(new RecyclerView.ItemDecoration()
    {
      public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
      {
        paramRect.left = 0;
        paramRect.right = 0;
        paramRect.top = 0;
        paramRect.bottom = 0;
      }
    });
    this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
      {
        if (ChatAttachAlert.this.listView.getChildCount() <= 0)
          return;
        if ((ChatAttachAlert.this.hintShowed) && (ChatAttachAlert.this.layoutManager.findLastVisibleItemPosition() > 1))
        {
          ChatAttachAlert.this.hideHint();
          ChatAttachAlert.access$3402(ChatAttachAlert.this, false);
          ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("bothint", true).commit();
        }
        ChatAttachAlert.this.updateLayout();
        ChatAttachAlert.this.checkCameraViewPosition();
      }
    });
    this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
    this.attachView = new FrameLayout(paramContext)
    {
      protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        int i = 0;
        paramInt1 = paramInt3 - paramInt1;
        paramInt2 = paramInt4 - paramInt2;
        paramInt3 = AndroidUtilities.dp(8.0F);
        ChatAttachAlert.this.attachPhotoRecyclerView.layout(0, paramInt3, paramInt1, ChatAttachAlert.this.attachPhotoRecyclerView.getMeasuredHeight() + paramInt3);
        ChatAttachAlert.this.progressView.layout(0, paramInt3, paramInt1, ChatAttachAlert.this.progressView.getMeasuredHeight() + paramInt3);
        ChatAttachAlert.this.lineView.layout(0, AndroidUtilities.dp(96.0F), paramInt1, AndroidUtilities.dp(96.0F) + ChatAttachAlert.this.lineView.getMeasuredHeight());
        ChatAttachAlert.this.hintTextView.layout(paramInt1 - ChatAttachAlert.this.hintTextView.getMeasuredWidth() - AndroidUtilities.dp(5.0F), paramInt2 - ChatAttachAlert.this.hintTextView.getMeasuredHeight() - AndroidUtilities.dp(5.0F), paramInt1 - AndroidUtilities.dp(5.0F), paramInt2 - AndroidUtilities.dp(5.0F));
        paramInt2 = (paramInt1 - AndroidUtilities.dp(360.0F)) / 3;
        paramInt1 = i;
        while (paramInt1 < 8)
        {
          paramInt3 = AndroidUtilities.dp(paramInt1 / 4 * 95 + 105);
          paramInt4 = AndroidUtilities.dp(10.0F) + paramInt1 % 4 * (AndroidUtilities.dp(85.0F) + paramInt2);
          ChatAttachAlert.this.views[paramInt1].layout(paramInt4, paramInt3, ChatAttachAlert.this.views[paramInt1].getMeasuredWidth() + paramInt4, ChatAttachAlert.this.views[paramInt1].getMeasuredHeight() + paramInt3);
          paramInt1 += 1;
        }
      }

      protected void onMeasure(int paramInt1, int paramInt2)
      {
        super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(294.0F), 1073741824));
      }
    };
    localObject1 = this.views;
    localObject2 = new RecyclerListView(paramContext);
    this.attachPhotoRecyclerView = ((RecyclerListView)localObject2);
    localObject1[8] = localObject2;
    this.attachPhotoRecyclerView.setVerticalScrollBarEnabled(true);
    localObject1 = this.attachPhotoRecyclerView;
    localObject2 = new PhotoAttachAdapter(paramContext);
    this.photoAttachAdapter = ((PhotoAttachAdapter)localObject2);
    ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
    this.attachPhotoRecyclerView.setClipToPadding(false);
    this.attachPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(8.0F), 0);
    this.attachPhotoRecyclerView.setItemAnimator(null);
    this.attachPhotoRecyclerView.setLayoutAnimation(null);
    this.attachPhotoRecyclerView.setOverScrollMode(2);
    this.attachView.addView(this.attachPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0F));
    this.attachPhotoLayoutManager = new LinearLayoutManager(paramContext)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    };
    this.attachPhotoLayoutManager.setOrientation(0);
    this.attachPhotoRecyclerView.setLayoutManager(this.attachPhotoLayoutManager);
    this.attachPhotoRecyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        if ((ChatAttachAlert.this.baseFragment == null) || (ChatAttachAlert.this.baseFragment.getParentActivity() == null));
        label149: label150: 
        while (true)
        {
          return;
          if ((!ChatAttachAlert.this.deviceHasGoodCamera) || (paramInt != 0))
          {
            if (!ChatAttachAlert.this.deviceHasGoodCamera)
              break label149;
            paramInt -= 1;
          }
          while (true)
          {
            if (MediaController.allPhotosAlbumEntry == null)
              break label150;
            paramView = MediaController.allPhotosAlbumEntry.photos;
            if ((paramInt < 0) || (paramInt >= paramView.size()))
              break;
            PhotoViewer.getInstance().setParentActivity(ChatAttachAlert.this.baseFragment.getParentActivity());
            PhotoViewer.getInstance().setParentAlert(ChatAttachAlert.this);
            PhotoViewer.getInstance().openPhotoForSelect(paramView, paramInt, 0, ChatAttachAlert.this, ChatAttachAlert.this.baseFragment);
            AndroidUtilities.hideKeyboard(ChatAttachAlert.this.baseFragment.getFragmentView().findFocus());
            return;
            ChatAttachAlert.this.openCamera();
            return;
          }
        }
      }
    });
    this.attachPhotoRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
      {
        ChatAttachAlert.this.checkCameraViewPosition();
      }
    });
    localObject1 = this.views;
    localObject2 = new EmptyTextProgressView(paramContext);
    this.progressView = ((EmptyTextProgressView)localObject2);
    localObject1[9] = localObject2;
    if ((Build.VERSION.SDK_INT >= 23) && (getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0))
    {
      this.progressView.setText(LocaleController.getString("PermissionStorage", 2131166261));
      this.progressView.setTextSize(16);
    }
    while (true)
    {
      this.attachView.addView(this.progressView, LayoutHelper.createFrame(-1, 80.0F));
      this.attachPhotoRecyclerView.setEmptyView(this.progressView);
      localObject1 = this.views;
      localObject2 = new View(getContext())
      {
        public boolean hasOverlappingRendering()
        {
          return false;
        }
      };
      this.lineView = ((View)localObject2);
      localObject1[10] = localObject2;
      this.lineView.setBackgroundColor(Theme.getColor("dialogGrayLine"));
      this.attachView.addView(this.lineView, new FrameLayout.LayoutParams(-1, 1, 51));
      localObject1 = LocaleController.getString("ChatCamera", 2131165531);
      localObject2 = LocaleController.getString("ChatGallery", 2131165533);
      String str1 = LocaleController.getString("ChatVideo", 2131165538);
      String str2 = LocaleController.getString("AttachMusic", 2131165366);
      String str3 = LocaleController.getString("ChatDocument", 2131165532);
      String str4 = LocaleController.getString("AttachContact", 2131165361);
      String str5 = LocaleController.getString("ChatLocation", 2131165536);
      i = 0;
      while (i < 8)
      {
        AttachButton localAttachButton = new AttachButton(paramContext);
        localAttachButton.setTextAndIcon(new CharSequence[] { localObject1, localObject2, str1, str2, str3, str4, str5, "" }[i], Theme.chat_attachButtonDrawables[i]);
        this.attachView.addView(localAttachButton, LayoutHelper.createFrame(85, 90, 51));
        localAttachButton.setTag(Integer.valueOf(i));
        this.views[i] = localAttachButton;
        if (i == 7)
        {
          this.sendPhotosButton = localAttachButton;
          this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0F), 0, 0);
        }
        localAttachButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            ChatAttachAlert.this.delegate.didPressedButton(((Integer)paramView.getTag()).intValue());
          }
        });
        i += 1;
      }
      this.progressView.setText(LocaleController.getString("NoPhotos", 2131166039));
      this.progressView.setTextSize(20);
    }
    this.hintTextView = new TextView(paramContext);
    this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0F), Theme.getColor("chat_gifSaveHintBackground")));
    this.hintTextView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
    this.hintTextView.setTextSize(1, 14.0F);
    this.hintTextView.setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
    this.hintTextView.setText(LocaleController.getString("AttachBotsHelp", 2131165360));
    this.hintTextView.setGravity(16);
    this.hintTextView.setVisibility(4);
    this.hintTextView.setCompoundDrawablesWithIntrinsicBounds(2130838045, 0, 0, 0);
    this.hintTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0F));
    this.attachView.addView(this.hintTextView, LayoutHelper.createFrame(-2, 32.0F, 85, 5.0F, 0.0F, 5.0F, 5.0F));
    int i = 0;
    while (i < 8)
    {
      this.viewsCache.add(this.photoAttachAdapter.createHolder());
      i += 1;
    }
    if (this.loading)
      this.progressView.showProgress();
    while (Build.VERSION.SDK_INT >= 16)
    {
      this.recordTime = new TextView(paramContext);
      this.recordTime.setBackgroundResource(2130838081);
      this.recordTime.getBackground().setColorFilter(new PorterDuffColorFilter(1711276032, PorterDuff.Mode.MULTIPLY));
      this.recordTime.setText("00:00");
      this.recordTime.setTextSize(1, 15.0F);
      this.recordTime.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.recordTime.setAlpha(0.0F);
      this.recordTime.setTextColor(-1);
      this.recordTime.setPadding(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(5.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(5.0F));
      this.container.addView(this.recordTime, LayoutHelper.createFrame(-2, -2.0F, 49, 0.0F, 16.0F, 0.0F, 0.0F));
      this.cameraPanel = new FrameLayout(paramContext)
      {
        protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
        {
          paramInt2 = getMeasuredWidth() / 2;
          paramInt1 = getMeasuredHeight() / 2;
          ChatAttachAlert.this.shutterButton.layout(paramInt2 - ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2, paramInt1 - ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2, ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2 + paramInt2, ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2 + paramInt1);
          if (getMeasuredWidth() == AndroidUtilities.dp(100.0F))
          {
            paramInt3 = getMeasuredWidth() / 2;
            int i = paramInt1 / 2 + paramInt1 + AndroidUtilities.dp(17.0F);
            paramInt1 = paramInt1 / 2 - AndroidUtilities.dp(17.0F);
            paramInt2 = paramInt3;
            paramInt4 = paramInt3;
            paramInt3 = i;
          }
          while (true)
          {
            ChatAttachAlert.this.switchCameraButton.layout(paramInt4 - ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2, paramInt3 - ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2, paramInt4 + ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2, paramInt3 + ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2);
            paramInt3 = 0;
            while (paramInt3 < 2)
            {
              ChatAttachAlert.this.flashModeButton[paramInt3].layout(paramInt2 - ChatAttachAlert.this.flashModeButton[paramInt3].getMeasuredWidth() / 2, paramInt1 - ChatAttachAlert.this.flashModeButton[paramInt3].getMeasuredHeight() / 2, ChatAttachAlert.this.flashModeButton[paramInt3].getMeasuredWidth() / 2 + paramInt2, ChatAttachAlert.this.flashModeButton[paramInt3].getMeasuredHeight() / 2 + paramInt1);
              paramInt3 += 1;
            }
            paramInt4 = paramInt2 / 2 + paramInt2 + AndroidUtilities.dp(17.0F);
            paramInt1 = paramInt2 / 2;
            paramInt2 = AndroidUtilities.dp(17.0F);
            paramInt3 = getMeasuredHeight() / 2;
            paramInt2 = paramInt1 - paramInt2;
            paramInt1 = paramInt3;
          }
        }
      };
      this.cameraPanel.setVisibility(8);
      this.cameraPanel.setAlpha(0.0F);
      this.container.addView(this.cameraPanel, LayoutHelper.createFrame(-1, 100, 83));
      this.shutterButton = new ShutterButton(paramContext);
      this.cameraPanel.addView(this.shutterButton, LayoutHelper.createFrame(84, 84, 17));
      this.shutterButton.setDelegate(new ShutterButton.ShutterButtonDelegate(paramChatActivity)
      {
        public void shutterCancel()
        {
          if (ChatAttachAlert.this.mediaCaptured)
            return;
          ChatAttachAlert.this.cameraFile.delete();
          ChatAttachAlert.this.resetRecordState();
          CameraController.getInstance().stopVideoRecording(ChatAttachAlert.this.cameraView.getCameraSession(), true);
        }

        public boolean shutterLongPressed()
        {
          if ((ChatAttachAlert.this.mediaCaptured) || (ChatAttachAlert.this.takingPhoto) || (ChatAttachAlert.this.baseFragment == null) || (ChatAttachAlert.this.baseFragment.getParentActivity() == null) || (ChatAttachAlert.this.cameraView == null))
            return false;
          if ((Build.VERSION.SDK_INT >= 23) && (ChatAttachAlert.this.baseFragment.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") != 0))
          {
            ChatAttachAlert.access$5002(ChatAttachAlert.this, true);
            ChatAttachAlert.this.baseFragment.getParentActivity().requestPermissions(new String[] { "android.permission.RECORD_AUDIO" }, 21);
            return false;
          }
          int i = 0;
          while (i < 2)
          {
            ChatAttachAlert.this.flashModeButton[i].setAlpha(0.0F);
            i += 1;
          }
          ChatAttachAlert.this.switchCameraButton.setAlpha(0.0F);
          ChatAttachAlert.access$5102(ChatAttachAlert.this, AndroidUtilities.generateVideoPath());
          ChatAttachAlert.this.recordTime.setAlpha(1.0F);
          ChatAttachAlert.this.recordTime.setText("00:00");
          ChatAttachAlert.access$5302(ChatAttachAlert.this, 0);
          ChatAttachAlert.access$5402(ChatAttachAlert.this, new Runnable()
          {
            public void run()
            {
              if (ChatAttachAlert.this.videoRecordRunnable == null)
                return;
              ChatAttachAlert.access$5308(ChatAttachAlert.this);
              ChatAttachAlert.this.recordTime.setText(String.format("%02d:%02d", new Object[] { Integer.valueOf(ChatAttachAlert.access$5300(ChatAttachAlert.this) / 60), Integer.valueOf(ChatAttachAlert.access$5300(ChatAttachAlert.this) % 60) }));
              AndroidUtilities.runOnUIThread(ChatAttachAlert.this.videoRecordRunnable, 1000L);
            }
          });
          AndroidUtilities.lockOrientation(this.val$parentFragment.getParentActivity());
          CameraController.getInstance().recordVideo(ChatAttachAlert.this.cameraView.getCameraSession(), ChatAttachAlert.this.cameraFile, new CameraController.VideoTakeCallback()
          {
            public void onFinishVideoRecording(Bitmap paramBitmap)
            {
              if ((ChatAttachAlert.this.cameraFile == null) || (ChatAttachAlert.this.baseFragment == null))
                return;
              PhotoViewer.getInstance().setParentActivity(ChatAttachAlert.this.baseFragment.getParentActivity());
              PhotoViewer.getInstance().setParentAlert(ChatAttachAlert.this);
              ChatAttachAlert.access$5502(ChatAttachAlert.this, new ArrayList());
              ChatAttachAlert.this.cameraPhoto.add(new MediaController.PhotoEntry(0, 0, 0L, ChatAttachAlert.this.cameraFile.getAbsolutePath(), 0, true));
              PhotoViewer.getInstance().openPhotoForSelect(ChatAttachAlert.this.cameraPhoto, 0, 2, new PhotoViewer.EmptyPhotoViewerProvider(paramBitmap)
              {
                @TargetApi(16)
                public boolean cancelButtonPressed()
                {
                  if ((ChatAttachAlert.this.cameraOpened) && (ChatAttachAlert.this.cameraView != null) && (ChatAttachAlert.this.cameraFile != null))
                  {
                    ChatAttachAlert.this.cameraFile.delete();
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        if ((ChatAttachAlert.this.cameraView != null) && (!ChatAttachAlert.this.isDismissed()) && (Build.VERSION.SDK_INT >= 21))
                          ChatAttachAlert.this.cameraView.setSystemUiVisibility(1028);
                      }
                    }
                    , 1000L);
                    CameraController.getInstance().startPreview(ChatAttachAlert.this.cameraView.getCameraSession());
                    ChatAttachAlert.access$5102(ChatAttachAlert.this, null);
                  }
                  return true;
                }

                public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
                {
                  return this.val$thumb;
                }

                public void sendButtonPressed(int paramInt, VideoEditedInfo paramVideoEditedInfo)
                {
                  if (ChatAttachAlert.this.cameraFile == null)
                    return;
                  AndroidUtilities.addMediaToGallery(ChatAttachAlert.this.cameraFile.getAbsolutePath());
                  ChatAttachAlert.this.baseFragment.sendMedia((MediaController.PhotoEntry)ChatAttachAlert.this.cameraPhoto.get(0), paramVideoEditedInfo);
                  ChatAttachAlert.this.closeCamera(false);
                  ChatAttachAlert.this.dismiss();
                  ChatAttachAlert.access$5102(ChatAttachAlert.this, null);
                }

                public void willHidePhotoViewer()
                {
                  ChatAttachAlert.access$4702(ChatAttachAlert.this, false);
                }
              }
              , ChatAttachAlert.this.baseFragment);
            }
          }
          , new Runnable()
          {
            public void run()
            {
              AndroidUtilities.runOnUIThread(ChatAttachAlert.this.videoRecordRunnable, 1000L);
            }
          }
          , false);
          ChatAttachAlert.this.shutterButton.setState(ShutterButton.State.RECORDING, true);
          return true;
        }

        public void shutterReleased()
        {
          if ((ChatAttachAlert.this.takingPhoto) || (ChatAttachAlert.this.cameraView == null) || (ChatAttachAlert.this.mediaCaptured))
            return;
          ChatAttachAlert.access$4702(ChatAttachAlert.this, true);
          if (ChatAttachAlert.this.shutterButton.getState() == ShutterButton.State.RECORDING)
          {
            ChatAttachAlert.this.resetRecordState();
            CameraController.getInstance().stopVideoRecording(ChatAttachAlert.this.cameraView.getCameraSession(), false);
            ChatAttachAlert.this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
            return;
          }
          ChatAttachAlert.access$5102(ChatAttachAlert.this, AndroidUtilities.generatePicturePath());
          boolean bool = ChatAttachAlert.this.cameraView.getCameraSession().isSameTakePictureOrientation();
          ChatAttachAlert.access$4802(ChatAttachAlert.this, CameraController.getInstance().takePicture(ChatAttachAlert.this.cameraFile, ChatAttachAlert.this.cameraView.getCameraSession(), new Runnable(bool)
          {
            public void run()
            {
              ChatAttachAlert.access$4802(ChatAttachAlert.this, false);
              if ((ChatAttachAlert.this.cameraFile == null) || (ChatAttachAlert.this.baseFragment == null))
                return;
              PhotoViewer.getInstance().setParentActivity(ChatAttachAlert.this.baseFragment.getParentActivity());
              PhotoViewer.getInstance().setParentAlert(ChatAttachAlert.this);
              ChatAttachAlert.access$5502(ChatAttachAlert.this, new ArrayList());
              try
              {
                i = new ExifInterface(ChatAttachAlert.this.cameraFile.getAbsolutePath()).getAttributeInt("Orientation", 1);
                switch (i)
                {
                case 4:
                case 5:
                case 7:
                default:
                  i = 0;
                case 6:
                case 3:
                case 8:
                }
                while (true)
                {
                  ChatAttachAlert.this.cameraPhoto.add(new MediaController.PhotoEntry(0, 0, 0L, ChatAttachAlert.this.cameraFile.getAbsolutePath(), i, false));
                  PhotoViewer.getInstance().openPhotoForSelect(ChatAttachAlert.this.cameraPhoto, 0, 2, new PhotoViewer.EmptyPhotoViewerProvider()
                  {
                    @TargetApi(16)
                    public boolean cancelButtonPressed()
                    {
                      if ((ChatAttachAlert.this.cameraOpened) && (ChatAttachAlert.this.cameraView != null) && (ChatAttachAlert.this.cameraFile != null))
                      {
                        ChatAttachAlert.this.cameraFile.delete();
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            if ((ChatAttachAlert.this.cameraView != null) && (!ChatAttachAlert.this.isDismissed()) && (Build.VERSION.SDK_INT >= 21))
                              ChatAttachAlert.this.cameraView.setSystemUiVisibility(1028);
                          }
                        }
                        , 1000L);
                        CameraController.getInstance().startPreview(ChatAttachAlert.this.cameraView.getCameraSession());
                        ChatAttachAlert.access$5102(ChatAttachAlert.this, null);
                      }
                      return true;
                    }

                    public boolean scaleToFill()
                    {
                      int j = 0;
                      int i = Settings.System.getInt(ChatAttachAlert.this.baseFragment.getParentActivity().getContentResolver(), "accelerometer_rotation", 0);
                      if ((ChatAttachAlert.11.4.this.val$sameTakePictureOrientation) || (i == 1))
                        j = 1;
                      return j;
                    }

                    public void sendButtonPressed(int paramInt, VideoEditedInfo paramVideoEditedInfo)
                    {
                      if (ChatAttachAlert.this.cameraFile == null)
                        return;
                      AndroidUtilities.addMediaToGallery(ChatAttachAlert.this.cameraFile.getAbsolutePath());
                      ChatAttachAlert.this.baseFragment.sendMedia((MediaController.PhotoEntry)ChatAttachAlert.this.cameraPhoto.get(0), null);
                      ChatAttachAlert.this.closeCamera(false);
                      ChatAttachAlert.this.dismiss();
                      ChatAttachAlert.access$5102(ChatAttachAlert.this, null);
                    }

                    public void willHidePhotoViewer()
                    {
                      ChatAttachAlert.access$4702(ChatAttachAlert.this, false);
                    }
                  }
                  , ChatAttachAlert.this.baseFragment);
                  return;
                  i = 90;
                  continue;
                  i = 180;
                  continue;
                  i = 270;
                }
              }
              catch (Exception localException)
              {
                while (true)
                {
                  FileLog.e(localException);
                  int i = 0;
                }
              }
            }
          }));
        }
      });
      this.switchCameraButton = new ImageView(paramContext);
      this.switchCameraButton.setScaleType(ImageView.ScaleType.CENTER);
      this.cameraPanel.addView(this.switchCameraButton, LayoutHelper.createFrame(48, 48, 21));
      this.switchCameraButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if ((ChatAttachAlert.this.takingPhoto) || (ChatAttachAlert.this.cameraView == null) || (!ChatAttachAlert.this.cameraView.isInitied()))
            return;
          ChatAttachAlert.access$5702(ChatAttachAlert.this, false);
          ChatAttachAlert.this.cameraView.switchCamera();
          paramView = ObjectAnimator.ofFloat(ChatAttachAlert.this.switchCameraButton, "scaleX", new float[] { 0.0F }).setDuration(100L);
          paramView.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              paramAnimator = ChatAttachAlert.this.switchCameraButton;
              if (ChatAttachAlert.this.cameraView.isFrontface());
              for (int i = 2130837659; ; i = 2130837660)
              {
                paramAnimator.setImageResource(i);
                ObjectAnimator.ofFloat(ChatAttachAlert.this.switchCameraButton, "scaleX", new float[] { 1.0F }).setDuration(100L).start();
                return;
              }
            }
          });
          paramView.start();
        }
      });
      i = 0;
      while (i < 2)
      {
        this.flashModeButton[i] = new ImageView(paramContext);
        this.flashModeButton[i].setScaleType(ImageView.ScaleType.CENTER);
        this.flashModeButton[i].setVisibility(4);
        this.cameraPanel.addView(this.flashModeButton[i], LayoutHelper.createFrame(48, 48, 51));
        this.flashModeButton[i].setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if ((ChatAttachAlert.this.flashAnimationInProgress) || (ChatAttachAlert.this.cameraView == null) || (!ChatAttachAlert.this.cameraView.isInitied()) || (!ChatAttachAlert.this.cameraOpened));
            Object localObject2;
            do
            {
              return;
              localObject1 = ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode();
              localObject2 = ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode();
            }
            while (((String)localObject1).equals(localObject2));
            ChatAttachAlert.this.cameraView.getCameraSession().setCurrentFlashMode((String)localObject2);
            ChatAttachAlert.access$5802(ChatAttachAlert.this, true);
            if (ChatAttachAlert.this.flashModeButton[0] == paramView);
            for (Object localObject1 = ChatAttachAlert.this.flashModeButton[1]; ; localObject1 = ChatAttachAlert.this.flashModeButton[0])
            {
              ((ImageView)localObject1).setVisibility(0);
              ChatAttachAlert.this.setCameraFlashModeIcon((ImageView)localObject1, (String)localObject2);
              localObject2 = new AnimatorSet();
              ((AnimatorSet)localObject2).playTogether(new Animator[] { ObjectAnimator.ofFloat(paramView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(48.0F) }), ObjectAnimator.ofFloat(localObject1, "translationY", new float[] { -AndroidUtilities.dp(48.0F), 0.0F }), ObjectAnimator.ofFloat(paramView, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(localObject1, "alpha", new float[] { 0.0F, 1.0F }) });
              ((AnimatorSet)localObject2).setDuration(200L);
              ((AnimatorSet)localObject2).addListener(new AnimatorListenerAdapter(paramView)
              {
                public void onAnimationEnd(Animator paramAnimator)
                {
                  ChatAttachAlert.access$5802(ChatAttachAlert.this, false);
                  this.val$currentImage.setVisibility(4);
                }
              });
              ((AnimatorSet)localObject2).start();
              return;
            }
          }
        });
        i += 1;
      }
      this.progressView.showTextView();
    }
  }

  private void applyCameraViewPosition()
  {
    if (this.cameraView != null)
    {
      if (!this.cameraOpened)
      {
        this.cameraView.setTranslationX(this.cameraViewLocation[0]);
        this.cameraView.setTranslationY(this.cameraViewLocation[1]);
      }
      this.cameraIcon.setTranslationX(this.cameraViewLocation[0]);
      this.cameraIcon.setTranslationY(this.cameraViewLocation[1]);
      int i = AndroidUtilities.dp(80.0F) - this.cameraViewOffsetX;
      int j = AndroidUtilities.dp(80.0F) - this.cameraViewOffsetY;
      if (!this.cameraOpened)
      {
        this.cameraView.setClipLeft(this.cameraViewOffsetX);
        this.cameraView.setClipTop(this.cameraViewOffsetY);
        localLayoutParams = (FrameLayout.LayoutParams)this.cameraView.getLayoutParams();
        if ((localLayoutParams.height != j) || (localLayoutParams.width != i))
        {
          localLayoutParams.width = i;
          localLayoutParams.height = j;
          this.cameraView.setLayoutParams(localLayoutParams);
          AndroidUtilities.runOnUIThread(new Runnable(localLayoutParams)
          {
            public void run()
            {
              if (ChatAttachAlert.this.cameraView != null)
                ChatAttachAlert.this.cameraView.setLayoutParams(this.val$layoutParamsFinal);
            }
          });
        }
      }
      FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.cameraIcon.getLayoutParams();
      if ((localLayoutParams.height != j) || (localLayoutParams.width != i))
      {
        localLayoutParams.width = i;
        localLayoutParams.height = j;
        this.cameraIcon.setLayoutParams(localLayoutParams);
        AndroidUtilities.runOnUIThread(new Runnable(localLayoutParams)
        {
          public void run()
          {
            if (ChatAttachAlert.this.cameraIcon != null)
              ChatAttachAlert.this.cameraIcon.setLayoutParams(this.val$layoutParamsFinal);
          }
        });
      }
    }
  }

  private void checkCameraViewPosition()
  {
    if (!this.deviceHasGoodCamera)
      return;
    int j = this.attachPhotoRecyclerView.getChildCount();
    int i = 0;
    while (true)
    {
      if (i < j)
      {
        localObject = this.attachPhotoRecyclerView.getChildAt(i);
        if (!(localObject instanceof PhotoAttachCameraCell))
          break label337;
        if ((Build.VERSION.SDK_INT < 19) || (((View)localObject).isAttachedToWindow()));
      }
      else
      {
        this.cameraViewOffsetX = 0;
        this.cameraViewOffsetY = 0;
        this.cameraViewLocation[0] = AndroidUtilities.dp(-150.0F);
        this.cameraViewLocation[1] = 0;
        applyCameraViewPosition();
        return;
      }
      ((View)localObject).getLocationInWindow(this.cameraViewLocation);
      Object localObject = this.cameraViewLocation;
      localObject[0] -= getLeftInset();
      float f = this.listView.getX() + backgroundPaddingLeft - getLeftInset();
      if (this.cameraViewLocation[0] < f)
      {
        this.cameraViewOffsetX = (int)(f - this.cameraViewLocation[0]);
        if (this.cameraViewOffsetX >= AndroidUtilities.dp(80.0F))
        {
          this.cameraViewOffsetX = 0;
          this.cameraViewLocation[0] = AndroidUtilities.dp(-150.0F);
          this.cameraViewLocation[1] = 0;
          if ((Build.VERSION.SDK_INT < 21) || (this.cameraViewLocation[1] >= AndroidUtilities.statusBarHeight))
            break label329;
          this.cameraViewOffsetY = (AndroidUtilities.statusBarHeight - this.cameraViewLocation[1]);
          if (this.cameraViewOffsetY < AndroidUtilities.dp(80.0F))
            break label307;
          this.cameraViewOffsetY = 0;
          this.cameraViewLocation[0] = AndroidUtilities.dp(-150.0F);
          this.cameraViewLocation[1] = 0;
        }
      }
      while (true)
      {
        applyCameraViewPosition();
        return;
        localObject = this.cameraViewLocation;
        localObject[0] += this.cameraViewOffsetX;
        break;
        this.cameraViewOffsetX = 0;
        break;
        label307: localObject = this.cameraViewLocation;
        localObject[1] += this.cameraViewOffsetY;
        continue;
        label329: this.cameraViewOffsetY = 0;
      }
      label337: i += 1;
    }
  }

  private PhotoAttachPhotoCell getCellForIndex(int paramInt)
  {
    if (MediaController.allPhotosAlbumEntry == null)
      return null;
    int j = this.attachPhotoRecyclerView.getChildCount();
    int i = 0;
    if (i < j)
    {
      Object localObject = this.attachPhotoRecyclerView.getChildAt(i);
      int k;
      if ((localObject instanceof PhotoAttachPhotoCell))
      {
        localObject = (PhotoAttachPhotoCell)localObject;
        k = ((Integer)((PhotoAttachPhotoCell)localObject).getImageView().getTag()).intValue();
        if ((k >= 0) && (k < MediaController.allPhotosAlbumEntry.photos.size()))
          break label90;
      }
      label90: 
      do
      {
        i += 1;
        break;
      }
      while (k != paramInt);
      return localObject;
    }
    return (PhotoAttachPhotoCell)null;
  }

  private void hideHint()
  {
    if (this.hideHintRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.hideHintRunnable);
      this.hideHintRunnable = null;
    }
    if (this.hintTextView == null)
      return;
    this.currentHintAnimation = new AnimatorSet();
    this.currentHintAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[] { 0.0F }) });
    this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
    this.currentHintAnimation.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationCancel(Animator paramAnimator)
      {
        if ((ChatAttachAlert.this.currentHintAnimation != null) && (ChatAttachAlert.this.currentHintAnimation.equals(paramAnimator)))
          ChatAttachAlert.access$6002(ChatAttachAlert.this, null);
      }

      public void onAnimationEnd(Animator paramAnimator)
      {
        if ((ChatAttachAlert.this.currentHintAnimation == null) || (!ChatAttachAlert.this.currentHintAnimation.equals(paramAnimator)));
        do
        {
          return;
          ChatAttachAlert.access$6002(ChatAttachAlert.this, null);
        }
        while (ChatAttachAlert.this.hintTextView == null);
        ChatAttachAlert.this.hintTextView.setVisibility(4);
      }
    });
    this.currentHintAnimation.setDuration(300L);
    this.currentHintAnimation.start();
  }

  private void onRevealAnimationEnd(boolean paramBoolean)
  {
    NotificationCenter.getInstance().setAnimationInProgress(false);
    this.revealAnimationInProgress = false;
    if ((paramBoolean) && (Build.VERSION.SDK_INT <= 19) && (MediaController.allPhotosAlbumEntry == null))
      MediaController.loadGalleryPhotosAlbums(0);
    if (paramBoolean)
    {
      checkCamera(true);
      showHint();
    }
  }

  @TargetApi(16)
  private void openCamera()
  {
    if (this.cameraView == null)
      return;
    this.animateCameraValues[0] = 0;
    this.animateCameraValues[1] = (AndroidUtilities.dp(80.0F) - this.cameraViewOffsetX);
    this.animateCameraValues[2] = (AndroidUtilities.dp(80.0F) - this.cameraViewOffsetY);
    this.cameraAnimationInProgress = true;
    this.cameraPanel.setVisibility(0);
    this.cameraPanel.setTag(null);
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[] { 0.0F, 1.0F }));
    localArrayList.add(ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[] { 1.0F }));
    int i = 0;
    while (true)
    {
      if (i < 2)
      {
        if (this.flashModeButton[i].getVisibility() == 0)
          localArrayList.add(ObjectAnimator.ofFloat(this.flashModeButton[i], "alpha", new float[] { 1.0F }));
      }
      else
      {
        AnimatorSet localAnimatorSet = new AnimatorSet();
        localAnimatorSet.playTogether(localArrayList);
        localAnimatorSet.setDuration(200L);
        localAnimatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            ChatAttachAlert.access$1002(ChatAttachAlert.this, false);
            if (ChatAttachAlert.this.cameraOpened)
              ChatAttachAlert.this.delegate.onCameraOpened();
          }
        });
        localAnimatorSet.start();
        if (Build.VERSION.SDK_INT >= 21)
          this.cameraView.setSystemUiVisibility(1028);
        this.cameraOpened = true;
        return;
      }
      i += 1;
    }
  }

  private boolean processTouchEvent(MotionEvent paramMotionEvent)
  {
    if (((!this.pressed) && (paramMotionEvent.getActionMasked() == 0)) || (paramMotionEvent.getActionMasked() == 5))
      if (!this.takingPhoto)
      {
        this.pressed = true;
        this.maybeStartDraging = true;
        this.lastY = paramMotionEvent.getY();
      }
    while (true)
    {
      return true;
      if (!this.pressed)
        continue;
      if (paramMotionEvent.getActionMasked() == 2)
      {
        f1 = paramMotionEvent.getY();
        f2 = f1 - this.lastY;
        if (this.maybeStartDraging)
        {
          if (Math.abs(f2) <= AndroidUtilities.getPixelsInCM(0.4F, false))
            continue;
          this.maybeStartDraging = false;
          this.dragging = true;
          return true;
        }
        if ((!this.dragging) || (this.cameraView == null))
          continue;
        this.cameraView.setTranslationY(f2 + this.cameraView.getTranslationY());
        this.lastY = f1;
        if (this.cameraPanel.getTag() != null)
          continue;
        this.cameraPanel.setTag(Integer.valueOf(1));
        paramMotionEvent = new AnimatorSet();
        paramMotionEvent.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.flashModeButton[0], "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.flashModeButton[1], "alpha", new float[] { 0.0F }) });
        paramMotionEvent.setDuration(200L);
        paramMotionEvent.start();
        return true;
      }
      if ((paramMotionEvent.getActionMasked() != 3) && (paramMotionEvent.getActionMasked() != 1) && (paramMotionEvent.getActionMasked() != 6))
        continue;
      this.pressed = false;
      if (!this.dragging)
        break;
      this.dragging = false;
      if (this.cameraView == null)
        continue;
      if (Math.abs(this.cameraView.getTranslationY()) > this.cameraView.getMeasuredHeight() / 6.0F)
      {
        closeCamera(true);
        return true;
      }
      paramMotionEvent = new AnimatorSet();
      paramMotionEvent.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.cameraView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.flashModeButton[0], "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.flashModeButton[1], "alpha", new float[] { 1.0F }) });
      paramMotionEvent.setDuration(250L);
      paramMotionEvent.setInterpolator(this.interpolator);
      paramMotionEvent.start();
      this.cameraPanel.setTag(null);
      return true;
    }
    this.cameraView.getLocationOnScreen(this.viewPosition);
    float f1 = paramMotionEvent.getRawX();
    float f2 = this.viewPosition[0];
    float f3 = paramMotionEvent.getRawY();
    float f4 = this.viewPosition[1];
    this.cameraView.focusToPoint((int)(f1 - f2), (int)(f3 - f4));
    return true;
  }

  private void resetRecordState()
  {
    if (this.baseFragment == null)
      return;
    int i = 0;
    while (i < 2)
    {
      this.flashModeButton[i].setAlpha(1.0F);
      i += 1;
    }
    this.switchCameraButton.setAlpha(1.0F);
    this.recordTime.setAlpha(0.0F);
    AndroidUtilities.cancelRunOnUIThread(this.videoRecordRunnable);
    this.videoRecordRunnable = null;
    AndroidUtilities.unlockOrientation(this.baseFragment.getParentActivity());
  }

  private void setCameraFlashModeIcon(ImageView paramImageView, String paramString)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    default:
    case 109935:
    case 3551:
    case 3005871:
    }
    while (true)
      switch (i)
      {
      default:
        return;
        if (!paramString.equals("off"))
          continue;
        i = 0;
        continue;
        if (!paramString.equals("on"))
          continue;
        i = 1;
        continue;
        if (!paramString.equals("auto"))
          continue;
        i = 2;
      case 0:
      case 1:
      case 2:
      }
    paramImageView.setImageResource(2130837712);
    return;
    paramImageView.setImageResource(2130837713);
    return;
    paramImageView.setImageResource(2130837711);
  }

  private void setUseRevealAnimation(boolean paramBoolean)
  {
    if ((!paramBoolean) || ((paramBoolean) && (Build.VERSION.SDK_INT >= 18) && (!AndroidUtilities.isTablet())))
      this.useRevealAnimation = paramBoolean;
  }

  private void showHint()
  {
    if (SearchQuery.inlineBots.isEmpty());
    do
      return;
    while (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("bothint", false));
    this.hintShowed = true;
    this.hintTextView.setVisibility(0);
    this.currentHintAnimation = new AnimatorSet();
    this.currentHintAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[] { 0.0F, 1.0F }) });
    this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
    this.currentHintAnimation.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationCancel(Animator paramAnimator)
      {
        if ((ChatAttachAlert.this.currentHintAnimation != null) && (ChatAttachAlert.this.currentHintAnimation.equals(paramAnimator)))
          ChatAttachAlert.access$6002(ChatAttachAlert.this, null);
      }

      public void onAnimationEnd(Animator paramAnimator)
      {
        if ((ChatAttachAlert.this.currentHintAnimation == null) || (!ChatAttachAlert.this.currentHintAnimation.equals(paramAnimator)))
          return;
        ChatAttachAlert.access$6002(ChatAttachAlert.this, null);
        AndroidUtilities.runOnUIThread(ChatAttachAlert.access$6302(ChatAttachAlert.this, new Runnable()
        {
          public void run()
          {
            if (ChatAttachAlert.this.hideHintRunnable != this)
              return;
            ChatAttachAlert.access$6302(ChatAttachAlert.this, null);
            ChatAttachAlert.this.hideHint();
          }
        }), 2000L);
      }
    });
    this.currentHintAnimation.setDuration(300L);
    this.currentHintAnimation.start();
  }

  @SuppressLint({"NewApi"})
  private void startRevealAnimation(boolean paramBoolean)
  {
    this.containerView.setTranslationY(0.0F);
    AnimatorSet localAnimatorSet = new AnimatorSet();
    Object localObject1 = this.delegate.getRevealView();
    Object localObject2;
    float f1;
    int i;
    if ((((View)localObject1).getVisibility() == 0) && (((ViewGroup)((View)localObject1).getParent()).getVisibility() == 0))
    {
      localObject2 = new int[2];
      ((View)localObject1).getLocationInWindow(localObject2);
      if (Build.VERSION.SDK_INT <= 19)
      {
        f1 = AndroidUtilities.displaySize.y - this.containerView.getMeasuredHeight() - AndroidUtilities.statusBarHeight;
        this.revealX = (localObject2[0] + ((View)localObject1).getMeasuredWidth() / 2);
        i = localObject2[1];
        this.revealY = (int)(((View)localObject1).getMeasuredHeight() / 2 + i - f1);
        if (Build.VERSION.SDK_INT <= 19)
          this.revealY -= AndroidUtilities.statusBarHeight;
      }
    }
    int k;
    int j;
    while (true)
    {
      localObject1 = new int[4][];
      localObject1[0] = { 0, 0 };
      localObject1[1] = { 0, AndroidUtilities.dp(304.0F) };
      localObject1[2] = { this.containerView.getMeasuredWidth(), 0 };
      localObject1[3] = { this.containerView.getMeasuredWidth(), AndroidUtilities.dp(304.0F) };
      k = this.revealY - this.scrollOffsetY + backgroundPaddingTop;
      j = 0;
      i = 0;
      while (j < 4)
      {
        i = Math.max(i, (int)Math.ceil(Math.sqrt((this.revealX - localObject1[j][0]) * (this.revealX - localObject1[j][0]) + (k - localObject1[j][1]) * (k - localObject1[j][1]))));
        j += 1;
      }
      f1 = this.containerView.getY();
      break;
      this.revealX = (AndroidUtilities.displaySize.x / 2 + backgroundPaddingLeft);
      this.revealY = (int)(AndroidUtilities.displaySize.y - this.containerView.getY());
    }
    label422: float f2;
    if (this.revealX <= this.containerView.getMeasuredWidth())
    {
      j = this.revealX;
      localObject1 = new ArrayList(3);
      if (!paramBoolean)
        break label1220;
      f1 = 0.0F;
      if (!paramBoolean)
        break label1227;
      f2 = i;
      label430: ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this, "revealRadius", new float[] { f1, f2 }));
      localObject2 = this.backDrawable;
      if (!paramBoolean)
        break label1232;
      k = 51;
      label468: ((ArrayList)localObject1).add(ObjectAnimator.ofInt(localObject2, "alpha", new int[] { k }));
      if (Build.VERSION.SDK_INT < 21)
        break label1260;
    }
    while (true)
    {
      try
      {
        localObject2 = this.containerView;
        k = this.revealY;
        if (!paramBoolean)
          continue;
        f1 = 0.0F;
        break label1507;
        ((ArrayList)localObject1).add(ViewAnimationUtils.createCircularReveal((View)localObject2, j, k, f1, f2));
        localAnimatorSet.setDuration(320L);
        localAnimatorSet.playTogether((Collection)localObject1);
        localAnimatorSet.addListener(new AnimatorListenerAdapter(paramBoolean, localAnimatorSet)
        {
          public void onAnimationCancel(Animator paramAnimator)
          {
            if ((ChatAttachAlert.this.currentSheetAnimation != null) && (this.val$animatorSet.equals(paramAnimator)))
              ChatAttachAlert.access$7702(ChatAttachAlert.this, null);
          }

          public void onAnimationEnd(Animator paramAnimator)
          {
            if ((ChatAttachAlert.this.currentSheetAnimation != null) && (ChatAttachAlert.this.currentSheetAnimation.equals(paramAnimator)))
            {
              ChatAttachAlert.access$7202(ChatAttachAlert.this, null);
              ChatAttachAlert.this.onRevealAnimationEnd(this.val$open);
              ChatAttachAlert.this.containerView.invalidate();
              ChatAttachAlert.this.containerView.setLayerType(0, null);
              if (this.val$open);
            }
            try
            {
              ChatAttachAlert.this.dismissInternal();
              return;
            }
            catch (Exception paramAnimator)
            {
              FileLog.e(paramAnimator);
            }
          }
        });
        if (!paramBoolean)
          break label1495;
        this.innerAnimators.clear();
        NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[] { NotificationCenter.dialogsNeedReload });
        NotificationCenter.getInstance().setAnimationInProgress(true);
        this.revealAnimationInProgress = true;
        if (Build.VERSION.SDK_INT > 19)
          break label1453;
        i = 11;
        j = 0;
        if (j >= i)
          break label1495;
        if (Build.VERSION.SDK_INT > 19)
          break label1460;
        if (j >= 8)
          continue;
        this.views[j].setScaleX(0.1F);
        this.views[j].setScaleY(0.1F);
        this.views[j].setAlpha(0.0F);
        localObject2 = new InnerAnimator(null);
        k = this.views[j].getLeft() + this.views[j].getMeasuredWidth() / 2;
        int m = this.views[j].getTop() + this.attachView.getTop() + this.views[j].getMeasuredHeight() / 2;
        f1 = (float)Math.sqrt((this.revealX - k) * (this.revealX - k) + (this.revealY - m) * (this.revealY - m));
        float f3 = (this.revealX - k) / f1;
        f2 = (this.revealY - m) / f1;
        localObject1 = this.views[j];
        float f4 = this.views[j].getMeasuredWidth() / 2;
        ((View)localObject1).setPivotX(f3 * AndroidUtilities.dp(20.0F) + f4);
        localObject1 = this.views[j];
        f3 = this.views[j].getMeasuredHeight() / 2;
        ((View)localObject1).setPivotY(f2 * AndroidUtilities.dp(20.0F) + f3);
        InnerAnimator.access$6802((InnerAnimator)localObject2, f1 - AndroidUtilities.dp(81.0F));
        this.views[j].setTag(2131165319, Integer.valueOf(1));
        ArrayList localArrayList = new ArrayList();
        if (j >= 8)
          break label1489;
        localArrayList.add(ObjectAnimator.ofFloat(this.views[j], "scaleX", new float[] { 0.7F, 1.05F }));
        localArrayList.add(ObjectAnimator.ofFloat(this.views[j], "scaleY", new float[] { 0.7F, 1.05F }));
        localObject1 = new AnimatorSet();
        ((AnimatorSet)localObject1).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.views[j], "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.views[j], "scaleY", new float[] { 1.0F }) });
        ((AnimatorSet)localObject1).setDuration(100L);
        ((AnimatorSet)localObject1).setInterpolator(this.decelerateInterpolator);
        if (Build.VERSION.SDK_INT > 19)
          continue;
        localArrayList.add(ObjectAnimator.ofFloat(this.views[j], "alpha", new float[] { 1.0F }));
        InnerAnimator.access$6902((InnerAnimator)localObject2, new AnimatorSet());
        ((InnerAnimator)localObject2).animatorSet.playTogether(localArrayList);
        ((InnerAnimator)localObject2).animatorSet.setDuration(150L);
        ((InnerAnimator)localObject2).animatorSet.setInterpolator(this.decelerateInterpolator);
        ((InnerAnimator)localObject2).animatorSet.addListener(new AnimatorListenerAdapter((AnimatorSet)localObject1)
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            if (this.val$animatorSetInner != null)
              this.val$animatorSetInner.start();
          }
        });
        this.innerAnimators.add(localObject2);
        j += 1;
        continue;
        j = this.containerView.getMeasuredWidth();
        break;
        label1220: f1 = i;
        break label422;
        label1227: f2 = 0.0F;
        break label430;
        label1232: k = 0;
        break label468;
        f1 = i;
        break label1507;
        f2 = 0.0F;
        continue;
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        continue;
      }
      label1260: if (!paramBoolean)
      {
        localAnimatorSet.setDuration(200L);
        ViewGroup localViewGroup = this.containerView;
        if (this.revealX <= this.containerView.getMeasuredWidth())
          f1 = this.revealX;
        while (true)
        {
          localViewGroup.setPivotX(f1);
          this.containerView.setPivotY(this.revealY);
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.containerView, "scaleX", new float[] { 0.0F }));
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.containerView, "scaleY", new float[] { 0.0F }));
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F }));
          break;
          f1 = this.containerView.getMeasuredWidth();
        }
      }
      localAnimatorSet.setDuration(250L);
      this.containerView.setScaleX(1.0F);
      this.containerView.setScaleY(1.0F);
      this.containerView.setAlpha(1.0F);
      if (Build.VERSION.SDK_INT > 19)
        continue;
      localAnimatorSet.setStartDelay(20L);
      continue;
      label1453: i = 8;
      continue;
      label1460: this.views[j].setScaleX(0.7F);
      this.views[j].setScaleY(0.7F);
      continue;
      label1489: localObject1 = null;
      continue;
      label1495: this.currentSheetAnimation = localAnimatorSet;
      localAnimatorSet.start();
      return;
      label1507: if (!paramBoolean)
        continue;
      f2 = i;
    }
  }

  @SuppressLint({"NewApi"})
  private void updateLayout()
  {
    Object localObject;
    int i;
    if (this.listView.getChildCount() <= 0)
    {
      localObject = this.listView;
      i = this.listView.getPaddingTop();
      this.scrollOffsetY = i;
      ((RecyclerListView)localObject).setTopGlowOffset(i);
      this.listView.invalidate();
    }
    while (true)
    {
      return;
      localObject = this.listView.getChildAt(0);
      RecyclerListView.Holder localHolder = (RecyclerListView.Holder)this.listView.findContainingViewHolder((View)localObject);
      i = ((View)localObject).getTop();
      if ((i >= 0) && (localHolder != null) && (localHolder.getAdapterPosition() == 0));
      while (this.scrollOffsetY != i)
      {
        localObject = this.listView;
        this.scrollOffsetY = i;
        ((RecyclerListView)localObject).setTopGlowOffset(i);
        this.listView.invalidate();
        return;
        i = 0;
      }
    }
  }

  public boolean allowCaption()
  {
    return true;
  }

  public boolean canDismiss()
  {
    return true;
  }

  protected boolean canDismissWithSwipe()
  {
    return false;
  }

  protected boolean canDismissWithTouchOutside()
  {
    return !this.cameraOpened;
  }

  public boolean cancelButtonPressed()
  {
    return false;
  }

  public void checkCamera(boolean paramBoolean)
  {
    if (this.baseFragment == null)
      return;
    boolean bool = this.deviceHasGoodCamera;
    if (Build.VERSION.SDK_INT >= 23)
      if (this.baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0)
      {
        if (paramBoolean)
          this.baseFragment.getParentActivity().requestPermissions(new String[] { "android.permission.CAMERA" }, 17);
        this.deviceHasGoodCamera = false;
      }
    while (true)
    {
      if ((bool != this.deviceHasGoodCamera) && (this.photoAttachAdapter != null))
        this.photoAttachAdapter.notifyDataSetChanged();
      if ((!isShowing()) || (!this.deviceHasGoodCamera) || (this.baseFragment == null) || (this.backDrawable.getAlpha() == 0) || (this.revealAnimationInProgress) || (this.cameraOpened))
        break;
      showCamera();
      return;
      CameraController.getInstance().initCamera();
      this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
      continue;
      if (Build.VERSION.SDK_INT < 16)
        continue;
      CameraController.getInstance().initCamera();
      this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
    }
  }

  @TargetApi(16)
  public void closeCamera(boolean paramBoolean)
  {
    if ((this.takingPhoto) || (this.cameraView == null))
      return;
    this.animateCameraValues[1] = (AndroidUtilities.dp(80.0F) - this.cameraViewOffsetX);
    this.animateCameraValues[2] = (AndroidUtilities.dp(80.0F) - this.cameraViewOffsetY);
    if (paramBoolean)
    {
      Object localObject1 = (FrameLayout.LayoutParams)this.cameraView.getLayoutParams();
      Object localObject2 = this.animateCameraValues;
      i = (int)this.cameraView.getTranslationY();
      ((FrameLayout.LayoutParams)localObject1).topMargin = i;
      localObject2[0] = i;
      this.cameraView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.cameraView.setTranslationY(0.0F);
      this.cameraAnimationInProgress = true;
      localObject1 = new ArrayList();
      ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[] { 0.0F }));
      ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[] { 0.0F }));
      i = 0;
      while (true)
      {
        if (i < 2)
        {
          if (this.flashModeButton[i].getVisibility() == 0)
            ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.flashModeButton[i], "alpha", new float[] { 0.0F }));
        }
        else
        {
          localObject2 = new AnimatorSet();
          ((AnimatorSet)localObject2).playTogether((Collection)localObject1);
          ((AnimatorSet)localObject2).setDuration(200L);
          ((AnimatorSet)localObject2).addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              ChatAttachAlert.access$1002(ChatAttachAlert.this, false);
              ChatAttachAlert.access$1102(ChatAttachAlert.this, false);
              if (ChatAttachAlert.this.cameraPanel != null)
                ChatAttachAlert.this.cameraPanel.setVisibility(8);
              if ((Build.VERSION.SDK_INT >= 21) && (ChatAttachAlert.this.cameraView != null))
                ChatAttachAlert.this.cameraView.setSystemUiVisibility(1024);
            }
          });
          ((AnimatorSet)localObject2).start();
          return;
        }
        i += 1;
      }
    }
    this.animateCameraValues[0] = 0;
    setCameraOpenProgress(0.0F);
    this.cameraPanel.setAlpha(0.0F);
    this.cameraPanel.setVisibility(8);
    int i = 0;
    while (true)
    {
      if (i < 2)
      {
        if (this.flashModeButton[i].getVisibility() == 0)
          this.flashModeButton[i].setAlpha(0.0F);
      }
      else
      {
        this.cameraOpened = false;
        if (Build.VERSION.SDK_INT < 21)
          break;
        this.cameraView.setSystemUiVisibility(1024);
        return;
      }
      i += 1;
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.albumsDidLoaded)
      if (this.photoAttachAdapter != null)
      {
        this.loading = false;
        this.progressView.showTextView();
        this.photoAttachAdapter.notifyDataSetChanged();
      }
    do
      while (true)
      {
        return;
        if (paramInt != NotificationCenter.reloadInlineHints)
          break;
        if (this.adapter == null)
          continue;
        this.adapter.notifyDataSetChanged();
        return;
      }
    while (paramInt != NotificationCenter.cameraInitied);
    checkCamera(false);
  }

  public void dismiss()
  {
    if (this.cameraAnimationInProgress)
      return;
    if (this.cameraOpened)
    {
      closeCamera(true);
      return;
    }
    hideCamera(true);
    super.dismiss();
  }

  public void dismissInternal()
  {
    if (this.containerView != null)
      this.containerView.setVisibility(4);
    super.dismissInternal();
  }

  public void dismissWithButtonClick(int paramInt)
  {
    super.dismissWithButtonClick(paramInt);
    if ((paramInt != 0) && (paramInt != 2));
    for (boolean bool = true; ; bool = false)
    {
      hideCamera(bool);
      return;
    }
  }

  public float getCameraOpenProgress()
  {
    return this.cameraOpenProgress;
  }

  public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    paramMessageObject = getCellForIndex(paramInt);
    if (paramMessageObject != null)
    {
      paramFileLocation = new int[2];
      paramMessageObject.getImageView().getLocationInWindow(paramFileLocation);
      paramFileLocation[0] -= getLeftInset();
      PhotoViewer.PlaceProviderObject localPlaceProviderObject = new PhotoViewer.PlaceProviderObject();
      localPlaceProviderObject.viewX = paramFileLocation[0];
      localPlaceProviderObject.viewY = paramFileLocation[1];
      localPlaceProviderObject.parentView = this.attachPhotoRecyclerView;
      localPlaceProviderObject.imageReceiver = paramMessageObject.getImageView().getImageReceiver();
      localPlaceProviderObject.thumb = localPlaceProviderObject.imageReceiver.getBitmap();
      localPlaceProviderObject.scale = paramMessageObject.getImageView().getScaleX();
      paramMessageObject.getCheckBox().setVisibility(8);
      return localPlaceProviderObject;
    }
    return null;
  }

  protected float getRevealRadius()
  {
    return this.revealRadius;
  }

  public int getSelectedCount()
  {
    return this.photoAttachAdapter.getSelectedPhotos().size();
  }

  public HashMap<Integer, MediaController.PhotoEntry> getSelectedPhotos()
  {
    return this.photoAttachAdapter.getSelectedPhotos();
  }

  public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    paramMessageObject = getCellForIndex(paramInt);
    if (paramMessageObject != null)
      return paramMessageObject.getImageView().getImageReceiver().getBitmap();
    return null;
  }

  public void hideCamera(boolean paramBoolean)
  {
    if ((!this.deviceHasGoodCamera) || (this.cameraView == null));
    while (true)
    {
      return;
      this.cameraView.destroy(paramBoolean, null);
      this.container.removeView(this.cameraView);
      this.container.removeView(this.cameraIcon);
      this.cameraView = null;
      this.cameraIcon = null;
      int j = this.attachPhotoRecyclerView.getChildCount();
      int i = 0;
      while (i < j)
      {
        View localView = this.attachPhotoRecyclerView.getChildAt(i);
        if ((localView instanceof PhotoAttachCameraCell))
        {
          localView.setVisibility(0);
          return;
        }
        i += 1;
      }
    }
  }

  public void init()
  {
    if (MediaController.allPhotosAlbumEntry != null)
    {
      int i = 0;
      while (i < Math.min(100, MediaController.allPhotosAlbumEntry.photos.size()))
      {
        MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(i);
        localPhotoEntry.caption = null;
        localPhotoEntry.imagePath = null;
        localPhotoEntry.thumbPath = null;
        localPhotoEntry.stickers.clear();
        i += 1;
      }
    }
    if (this.currentHintAnimation != null)
    {
      this.currentHintAnimation.cancel();
      this.currentHintAnimation = null;
    }
    this.hintTextView.setAlpha(0.0F);
    this.hintTextView.setVisibility(4);
    this.attachPhotoLayoutManager.scrollToPositionWithOffset(0, 1000000);
    this.photoAttachAdapter.clearSelectedPhotos();
    this.layoutManager.scrollToPositionWithOffset(0, 1000000);
    updatePhotosButton();
  }

  public boolean isPhotoChecked(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < MediaController.allPhotosAlbumEntry.photos.size()) && (this.photoAttachAdapter.getSelectedPhotos().containsKey(Integer.valueOf(((MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(paramInt)).imageId)));
  }

  public void loadGalleryPhotos()
  {
    if ((MediaController.allPhotosAlbumEntry == null) && (Build.VERSION.SDK_INT >= 21))
      MediaController.loadGalleryPhotosAlbums(0);
  }

  protected boolean onContainerTouchEvent(MotionEvent paramMotionEvent)
  {
    return (this.cameraOpened) && (processTouchEvent(paramMotionEvent));
  }

  protected boolean onCustomCloseAnimation()
  {
    int i = 0;
    if (this.useRevealAnimation)
    {
      this.backDrawable.setAlpha(51);
      startRevealAnimation(false);
      i = 1;
    }
    return i;
  }

  protected boolean onCustomLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = 0;
    int j = paramInt3 - paramInt1;
    paramInt2 = paramInt4 - paramInt2;
    if (j < paramInt2)
      paramInt1 = 1;
    while (paramView == this.cameraPanel)
    {
      if (paramInt1 != 0)
      {
        this.cameraPanel.layout(0, paramInt4 - AndroidUtilities.dp(100.0F), j, paramInt4);
        return true;
        paramInt1 = 0;
        continue;
      }
      this.cameraPanel.layout(paramInt3 - AndroidUtilities.dp(100.0F), 0, paramInt3, paramInt2);
      return true;
    }
    if ((paramView == this.flashModeButton[0]) || (paramView == this.flashModeButton[1]))
    {
      if (Build.VERSION.SDK_INT >= 21);
      for (paramInt2 = AndroidUtilities.dp(10.0F); ; paramInt2 = 0)
      {
        paramInt4 = i;
        if (Build.VERSION.SDK_INT >= 21)
          paramInt4 = AndroidUtilities.dp(8.0F);
        if (paramInt1 == 0)
          break;
        paramView.layout(paramInt3 - paramView.getMeasuredWidth() - paramInt4, paramInt2, paramInt3 - paramInt4, paramView.getMeasuredHeight() + paramInt2);
        return true;
      }
      paramView.layout(paramInt4, paramInt2, paramView.getMeasuredWidth() + paramInt4, paramView.getMeasuredHeight() + paramInt2);
      return true;
    }
    return false;
  }

  protected boolean onCustomMeasure(View paramView, int paramInt1, int paramInt2)
  {
    int i;
    if (paramInt1 < paramInt2)
      i = 1;
    while (true)
      if (paramView == this.cameraView)
      {
        if ((!this.cameraOpened) || (this.cameraAnimationInProgress))
          break;
        this.cameraView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824));
        return true;
        i = 0;
        continue;
      }
      else
      {
        if (paramView != this.cameraPanel)
          break;
        if (i != 0)
        {
          this.cameraPanel.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0F), 1073741824));
          return true;
        }
        this.cameraPanel.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824));
        return true;
      }
    return false;
  }

  protected boolean onCustomOpenAnimation()
  {
    if (this.useRevealAnimation)
    {
      startRevealAnimation(true);
      return true;
    }
    return false;
  }

  public void onDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.albumsDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.reloadInlineHints);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.cameraInitied);
    this.baseFragment = null;
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((this.cameraOpened) && ((paramInt == 24) || (paramInt == 25)))
    {
      this.shutterButton.getDelegate().shutterReleased();
      return true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }

  public void onOpenAnimationEnd()
  {
    onRevealAnimationEnd(true);
  }

  public void onOpenAnimationStart()
  {
  }

  public void onPause()
  {
    if (this.shutterButton == null)
      return;
    if (!this.requestingPermissions)
    {
      if ((this.cameraView != null) && (this.shutterButton.getState() == ShutterButton.State.RECORDING))
      {
        resetRecordState();
        CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
        this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
      }
      if (this.cameraOpened)
        closeCamera(false);
      hideCamera(true);
    }
    while (true)
    {
      this.paused = true;
      return;
      if ((this.cameraView != null) && (this.shutterButton.getState() == ShutterButton.State.RECORDING))
        this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
      this.requestingPermissions = false;
    }
  }

  public void onResume()
  {
    this.paused = false;
    if ((isShowing()) && (!isDismissed()))
      checkCamera(false);
  }

  public boolean scaleToFill()
  {
    return false;
  }

  public void sendButtonPressed(int paramInt, VideoEditedInfo paramVideoEditedInfo)
  {
    if (this.photoAttachAdapter.getSelectedPhotos().isEmpty())
    {
      if ((paramInt < 0) || (paramInt >= MediaController.allPhotosAlbumEntry.photos.size()))
        return;
      paramVideoEditedInfo = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(paramInt);
      this.photoAttachAdapter.getSelectedPhotos().put(Integer.valueOf(paramVideoEditedInfo.imageId), paramVideoEditedInfo);
    }
    this.delegate.didPressedButton(7);
  }

  public void setAllowDrawContent(boolean paramBoolean)
  {
    super.setAllowDrawContent(paramBoolean);
    checkCameraViewPosition();
  }

  public void setCameraOpenProgress(float paramFloat)
  {
    if (this.cameraView == null)
      return;
    this.cameraOpenProgress = paramFloat;
    float f3 = this.animateCameraValues[1];
    float f4 = this.animateCameraValues[2];
    int i;
    float f1;
    float f2;
    label72: FrameLayout.LayoutParams localLayoutParams;
    if (AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y)
    {
      i = 1;
      if (i == 0)
        break label325;
      f1 = this.container.getWidth();
      f2 = this.container.getHeight();
      if (paramFloat != 0.0F)
        break label346;
      this.cameraView.setClipLeft(this.cameraViewOffsetX);
      this.cameraView.setClipTop(this.cameraViewOffsetY);
      this.cameraView.setTranslationX(this.cameraViewLocation[0]);
      this.cameraView.setTranslationY(this.cameraViewLocation[1]);
      this.cameraIcon.setTranslationX(this.cameraViewLocation[0]);
      this.cameraIcon.setTranslationY(this.cameraViewLocation[1]);
      label156: localLayoutParams = (FrameLayout.LayoutParams)this.cameraView.getLayoutParams();
      localLayoutParams.width = (int)((f1 - f3) * paramFloat + f3);
      localLayoutParams.height = (int)((f2 - f4) * paramFloat + f4);
      if (paramFloat == 0.0F)
        break label389;
      this.cameraView.setClipLeft((int)(this.cameraViewOffsetX * (1.0F - paramFloat)));
      this.cameraView.setClipTop((int)(this.cameraViewOffsetY * (1.0F - paramFloat)));
      localLayoutParams.leftMargin = (int)(this.cameraViewLocation[0] * (1.0F - paramFloat));
    }
    for (localLayoutParams.topMargin = (int)(this.animateCameraValues[0] + (this.cameraViewLocation[1] - this.animateCameraValues[0]) * (1.0F - paramFloat)); ; localLayoutParams.topMargin = 0)
    {
      this.cameraView.setLayoutParams(localLayoutParams);
      if (paramFloat > 0.5F)
        break label404;
      this.cameraIcon.setAlpha(1.0F - paramFloat / 0.5F);
      return;
      i = 0;
      break;
      label325: f1 = this.container.getWidth();
      f2 = this.container.getHeight();
      break label72;
      label346: if ((this.cameraView.getTranslationX() == 0.0F) && (this.cameraView.getTranslationY() == 0.0F))
        break label156;
      this.cameraView.setTranslationX(0.0F);
      this.cameraView.setTranslationY(0.0F);
      break label156;
      label389: localLayoutParams.leftMargin = 0;
    }
    label404: this.cameraIcon.setAlpha(0.0F);
  }

  public void setDelegate(ChatAttachViewDelegate paramChatAttachViewDelegate)
  {
    this.delegate = paramChatAttachViewDelegate;
  }

  public void setPhotoChecked(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= MediaController.allPhotosAlbumEntry.photos.size()))
      return;
    Object localObject = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(paramInt);
    boolean bool;
    int j;
    int i;
    if (this.photoAttachAdapter.getSelectedPhotos().containsKey(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId)))
    {
      this.photoAttachAdapter.getSelectedPhotos().remove(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId));
      bool = false;
      j = this.attachPhotoRecyclerView.getChildCount();
      i = 0;
    }
    while (true)
    {
      if (i < j)
      {
        localObject = this.attachPhotoRecyclerView.getChildAt(i);
        if (((localObject instanceof PhotoAttachPhotoCell)) && (((Integer)((View)localObject).getTag()).intValue() == paramInt))
          ((PhotoAttachPhotoCell)localObject).setChecked(bool, false);
      }
      else
      {
        updatePhotosButton();
        return;
        this.photoAttachAdapter.getSelectedPhotos().put(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId), localObject);
        bool = true;
        break;
      }
      i += 1;
    }
  }

  @SuppressLint({"NewApi"})
  protected void setRevealRadius(float paramFloat)
  {
    this.revealRadius = paramFloat;
    if (Build.VERSION.SDK_INT <= 19)
      this.listView.invalidate();
    if (!isDismissed())
    {
      int i = 0;
      if (i < this.innerAnimators.size())
      {
        InnerAnimator localInnerAnimator = (InnerAnimator)this.innerAnimators.get(i);
        if (localInnerAnimator.startRadius > paramFloat);
        while (true)
        {
          i += 1;
          break;
          localInnerAnimator.animatorSet.start();
          this.innerAnimators.remove(i);
          i -= 1;
        }
      }
    }
  }

  @TargetApi(16)
  public void showCamera()
  {
    if (this.paused)
      return;
    if (this.cameraView == null)
    {
      this.cameraView = new CameraView(this.baseFragment.getParentActivity(), false);
      this.container.addView(this.cameraView, 1, LayoutHelper.createFrame(80, 80.0F));
      this.cameraView.setDelegate(new CameraView.CameraViewDelegate()
      {
        public void onCameraCreated(Camera paramCamera)
        {
        }

        public void onCameraInit()
        {
          int k = 0;
          int j = ChatAttachAlert.this.attachPhotoRecyclerView.getChildCount();
          int i = 0;
          while (true)
          {
            if (i < j)
            {
              localObject = ChatAttachAlert.this.attachPhotoRecyclerView.getChildAt(i);
              if ((localObject instanceof PhotoAttachCameraCell))
                ((View)localObject).setVisibility(4);
            }
            else
            {
              if (!ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode().equals(ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode()))
                break label140;
              i = 0;
              while (i < 2)
              {
                ChatAttachAlert.this.flashModeButton[i].setVisibility(4);
                ChatAttachAlert.this.flashModeButton[i].setAlpha(0.0F);
                ChatAttachAlert.this.flashModeButton[i].setTranslationY(0.0F);
                i += 1;
              }
            }
            i += 1;
            continue;
            label140: ChatAttachAlert.this.setCameraFlashModeIcon(ChatAttachAlert.this.flashModeButton[0], ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode());
            i = 0;
            if (i >= 2)
              break;
            localObject = ChatAttachAlert.this.flashModeButton[i];
            label193: float f;
            if (i == 0)
            {
              j = 0;
              ((ImageView)localObject).setVisibility(j);
              localObject = ChatAttachAlert.this.flashModeButton[i];
              if ((i != 0) || (!ChatAttachAlert.this.cameraOpened))
                break label257;
              f = 1.0F;
            }
            while (true)
            {
              ((ImageView)localObject).setAlpha(f);
              ChatAttachAlert.this.flashModeButton[i].setTranslationY(0.0F);
              i += 1;
              break;
              j = 4;
              break label193;
              label257: f = 0.0F;
            }
          }
          Object localObject = ChatAttachAlert.this.switchCameraButton;
          if (ChatAttachAlert.this.cameraView.isFrontface())
          {
            i = 2130837659;
            ((ImageView)localObject).setImageResource(i);
            localObject = ChatAttachAlert.this.switchCameraButton;
            if (!ChatAttachAlert.this.cameraView.hasFrontFaceCamera())
              break label331;
          }
          label331: for (i = k; ; i = 4)
          {
            ((ImageView)localObject).setVisibility(i);
            return;
            i = 2130837660;
            break;
          }
        }
      });
      this.cameraIcon = new FrameLayout(this.baseFragment.getParentActivity());
      this.container.addView(this.cameraIcon, 2, LayoutHelper.createFrame(80, 80.0F));
      ImageView localImageView = new ImageView(this.baseFragment.getParentActivity());
      localImageView.setScaleType(ImageView.ScaleType.CENTER);
      localImageView.setImageResource(2130837873);
      this.cameraIcon.addView(localImageView, LayoutHelper.createFrame(80, 80, 85));
    }
    this.cameraView.setTranslationX(this.cameraViewLocation[0]);
    this.cameraView.setTranslationY(this.cameraViewLocation[1]);
    this.cameraIcon.setTranslationX(this.cameraViewLocation[0]);
    this.cameraIcon.setTranslationY(this.cameraViewLocation[1]);
  }

  public void updatePhotoAtIndex(int paramInt)
  {
    PhotoAttachPhotoCell localPhotoAttachPhotoCell = getCellForIndex(paramInt);
    MediaController.PhotoEntry localPhotoEntry;
    if (localPhotoAttachPhotoCell != null)
    {
      localPhotoAttachPhotoCell.getImageView().setOrientation(0, true);
      localPhotoEntry = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(paramInt);
      if (localPhotoEntry.thumbPath != null)
        localPhotoAttachPhotoCell.getImageView().setImage(localPhotoEntry.thumbPath, null, localPhotoAttachPhotoCell.getContext().getResources().getDrawable(2130837964));
    }
    else
    {
      return;
    }
    if (localPhotoEntry.path != null)
    {
      localPhotoAttachPhotoCell.getImageView().setOrientation(localPhotoEntry.orientation, true);
      localPhotoAttachPhotoCell.getImageView().setImage("thumb://" + localPhotoEntry.imageId + ":" + localPhotoEntry.path, null, localPhotoAttachPhotoCell.getContext().getResources().getDrawable(2130837964));
      return;
    }
    localPhotoAttachPhotoCell.getImageView().setImageResource(2130837964);
  }

  public void updatePhotosButton()
  {
    int i = this.photoAttachAdapter.getSelectedPhotos().size();
    if (i == 0)
    {
      this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0F), 0, 0);
      this.sendPhotosButton.imageView.setBackgroundResource(2130837610);
      this.sendPhotosButton.imageView.setImageResource(2130837609);
      this.sendPhotosButton.textView.setText("");
    }
    while ((Build.VERSION.SDK_INT >= 23) && (getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0))
    {
      this.progressView.setText(LocaleController.getString("PermissionStorage", 2131166261));
      this.progressView.setTextSize(16);
      return;
      this.sendPhotosButton.imageView.setPadding(AndroidUtilities.dp(2.0F), 0, 0, 0);
      this.sendPhotosButton.imageView.setBackgroundResource(2130837617);
      this.sendPhotosButton.imageView.setImageResource(2130837616);
      this.sendPhotosButton.textView.setText(LocaleController.formatString("SendItems", 2131166415, new Object[] { String.format("(%d)", new Object[] { Integer.valueOf(i) }) }));
    }
    this.progressView.setText(LocaleController.getString("NoPhotos", 2131166039));
    this.progressView.setTextSize(20);
  }

  public void willHidePhotoViewer()
  {
    int j = this.attachPhotoRecyclerView.getChildCount();
    int i = 0;
    while (i < j)
    {
      Object localObject = this.attachPhotoRecyclerView.getChildAt(i);
      if ((localObject instanceof PhotoAttachPhotoCell))
      {
        localObject = (PhotoAttachPhotoCell)localObject;
        if (((PhotoAttachPhotoCell)localObject).getCheckBox().getVisibility() != 0)
          ((PhotoAttachPhotoCell)localObject).getCheckBox().setVisibility(0);
      }
      i += 1;
    }
  }

  public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    paramMessageObject = getCellForIndex(paramInt);
    if (paramMessageObject != null)
      paramMessageObject.getCheckBox().setVisibility(0);
  }

  private class AttachBotButton extends FrameLayout
  {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private boolean checkingForLongPress = false;
    private TLRPC.User currentUser;
    private BackupImageView imageView;
    private TextView nameTextView;
    private CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    private int pressCount = 0;
    private boolean pressed;

    public AttachBotButton(Context arg2)
    {
      super();
      this.imageView = new BackupImageView(localContext);
      this.imageView.setRoundRadius(AndroidUtilities.dp(27.0F));
      addView(this.imageView, LayoutHelper.createFrame(54, 54.0F, 49, 0.0F, 7.0F, 0.0F, 0.0F));
      this.nameTextView = new TextView(localContext);
      this.nameTextView.setTextColor(Theme.getColor("dialogTextGray2"));
      this.nameTextView.setTextSize(1, 12.0F);
      this.nameTextView.setMaxLines(2);
      this.nameTextView.setGravity(49);
      this.nameTextView.setLines(2);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 6.0F, 65.0F, 6.0F, 0.0F));
    }

    private void onLongPress()
    {
      if ((ChatAttachAlert.this.baseFragment == null) || (this.currentUser == null))
        return;
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getContext());
      localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
      localBuilder.setMessage(LocaleController.formatString("ChatHintsDelete", 2131165535, new Object[] { ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name) }));
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          SearchQuery.removeInline(ChatAttachAlert.AttachBotButton.this.currentUser.id);
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
      localBuilder.show();
    }

    protected void cancelCheckLongPress()
    {
      this.checkingForLongPress = false;
      if (this.pendingCheckForLongPress != null)
        removeCallbacks(this.pendingCheckForLongPress);
      if (this.pendingCheckForTap != null)
        removeCallbacks(this.pendingCheckForTap);
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0F), 1073741824));
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool1;
      boolean bool2;
      if (paramMotionEvent.getAction() == 0)
      {
        this.pressed = true;
        invalidate();
        bool1 = true;
        if (bool1)
          break label184;
        bool2 = super.onTouchEvent(paramMotionEvent);
      }
      while (true)
      {
        if ((paramMotionEvent.getAction() != 0) && (paramMotionEvent.getAction() != 2))
          cancelCheckLongPress();
        return bool2;
        if (this.pressed)
        {
          if (paramMotionEvent.getAction() == 1)
          {
            getParent().requestDisallowInterceptTouchEvent(true);
            this.pressed = false;
            playSoundEffect(0);
            ChatAttachAlert.this.delegate.didSelectBot(MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_topPeer)SearchQuery.inlineBots.get(((Integer)getTag()).intValue())).peer.user_id)));
            ChatAttachAlert.this.setUseRevealAnimation(false);
            ChatAttachAlert.this.dismiss();
            ChatAttachAlert.this.setUseRevealAnimation(true);
            invalidate();
            bool1 = false;
            break;
          }
          if (paramMotionEvent.getAction() == 3)
          {
            this.pressed = false;
            invalidate();
          }
        }
        bool1 = false;
        break;
        label184: bool2 = bool1;
        if (paramMotionEvent.getAction() != 0)
          continue;
        startCheckLongPress();
        bool2 = bool1;
      }
    }

    public void setUser(TLRPC.User paramUser)
    {
      if (paramUser == null)
        return;
      this.currentUser = paramUser;
      Object localObject2 = null;
      this.nameTextView.setText(ContactsController.formatName(paramUser.first_name, paramUser.last_name));
      this.avatarDrawable.setInfo(paramUser);
      Object localObject1 = localObject2;
      if (paramUser != null)
      {
        localObject1 = localObject2;
        if (paramUser.photo != null)
          localObject1 = paramUser.photo.photo_small;
      }
      this.imageView.setImage((TLObject)localObject1, "50_50", this.avatarDrawable);
      requestLayout();
    }

    protected void startCheckLongPress()
    {
      if (this.checkingForLongPress)
        return;
      this.checkingForLongPress = true;
      if (this.pendingCheckForTap == null)
        this.pendingCheckForTap = new CheckForTap(null);
      postDelayed(this.pendingCheckForTap, ViewConfiguration.getTapTimeout());
    }

    class CheckForLongPress
      implements Runnable
    {
      public int currentPressCount;

      CheckForLongPress()
      {
      }

      public void run()
      {
        if ((ChatAttachAlert.AttachBotButton.this.checkingForLongPress) && (ChatAttachAlert.AttachBotButton.this.getParent() != null) && (this.currentPressCount == ChatAttachAlert.AttachBotButton.this.pressCount))
        {
          ChatAttachAlert.AttachBotButton.access$202(ChatAttachAlert.AttachBotButton.this, false);
          ChatAttachAlert.AttachBotButton.this.performHapticFeedback(0);
          ChatAttachAlert.AttachBotButton.this.onLongPress();
          MotionEvent localMotionEvent = MotionEvent.obtain(0L, 0L, 3, 0.0F, 0.0F, 0);
          ChatAttachAlert.AttachBotButton.this.onTouchEvent(localMotionEvent);
          localMotionEvent.recycle();
        }
      }
    }

    private final class CheckForTap
      implements Runnable
    {
      private CheckForTap()
      {
      }

      public void run()
      {
        if (ChatAttachAlert.AttachBotButton.this.pendingCheckForLongPress == null)
          ChatAttachAlert.AttachBotButton.access$002(ChatAttachAlert.AttachBotButton.this, new ChatAttachAlert.AttachBotButton.CheckForLongPress(ChatAttachAlert.AttachBotButton.this));
        ChatAttachAlert.AttachBotButton.this.pendingCheckForLongPress.currentPressCount = ChatAttachAlert.AttachBotButton.access$104(ChatAttachAlert.AttachBotButton.this);
        ChatAttachAlert.AttachBotButton.this.postDelayed(ChatAttachAlert.AttachBotButton.this.pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout());
      }
    }
  }

  private class AttachButton extends FrameLayout
  {
    private ImageView imageView;
    private TextView textView;

    public AttachButton(Context arg2)
    {
      super();
      this.imageView = new ImageView(localContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      addView(this.imageView, LayoutHelper.createFrame(64, 64, 49));
      this.textView = new TextView(localContext);
      this.textView.setLines(1);
      this.textView.setSingleLine(true);
      this.textView.setGravity(1);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      this.textView.setTextColor(Theme.getColor("dialogTextGray2"));
      this.textView.setTextSize(1, 12.0F);
      addView(this.textView, LayoutHelper.createFrame(-1, -2.0F, 51, 0.0F, 64.0F, 0.0F, 0.0F));
    }

    public boolean hasOverlappingRendering()
    {
      return false;
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0F), 1073741824));
    }

    public void setTextAndIcon(CharSequence paramCharSequence, Drawable paramDrawable)
    {
      this.textView.setText(paramCharSequence);
      this.imageView.setBackgroundDrawable(paramDrawable);
    }
  }

  public static abstract interface ChatAttachViewDelegate
  {
    public abstract void didPressedButton(int paramInt);

    public abstract void didSelectBot(TLRPC.User paramUser);

    public abstract View getRevealView();

    public abstract void onCameraOpened();
  }

  private class InnerAnimator
  {
    private AnimatorSet animatorSet;
    private float startRadius;

    private InnerAnimator()
    {
    }
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
      if (!SearchQuery.inlineBots.isEmpty());
      for (int i = (int)Math.ceil(SearchQuery.inlineBots.size() / 4.0F) + 1; ; i = 0)
        return i + 1;
    }

    public int getItemViewType(int paramInt)
    {
      switch (paramInt)
      {
      default:
        return 2;
      case 0:
        return 0;
      case 1:
      }
      return 1;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (paramInt > 1)
      {
        int i = (paramInt - 2) * 4;
        paramViewHolder = (FrameLayout)paramViewHolder.itemView;
        paramInt = 0;
        if (paramInt < 4)
        {
          ChatAttachAlert.AttachBotButton localAttachBotButton = (ChatAttachAlert.AttachBotButton)paramViewHolder.getChildAt(paramInt);
          if (i + paramInt >= SearchQuery.inlineBots.size())
            localAttachBotButton.setVisibility(4);
          while (true)
          {
            paramInt += 1;
            break;
            localAttachBotButton.setVisibility(0);
            localAttachBotButton.setTag(Integer.valueOf(i + paramInt));
            localAttachBotButton.setUser(MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_topPeer)SearchQuery.inlineBots.get(i + paramInt)).peer.user_id)));
          }
        }
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new FrameLayout(this.mContext)
        {
          protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
          {
            paramInt2 = (paramInt3 - paramInt1 - AndroidUtilities.dp(360.0F)) / 3;
            paramInt1 = 0;
            while (paramInt1 < 4)
            {
              paramInt3 = AndroidUtilities.dp(10.0F) + paramInt1 % 4 * (AndroidUtilities.dp(85.0F) + paramInt2);
              View localView = getChildAt(paramInt1);
              localView.layout(paramInt3, 0, localView.getMeasuredWidth() + paramInt3, localView.getMeasuredHeight());
              paramInt1 += 1;
            }
          }
        };
        paramInt = 0;
      case 0:
        while (paramInt < 4)
        {
          paramViewGroup.addView(new ChatAttachAlert.AttachBotButton(ChatAttachAlert.this, this.mContext));
          paramInt += 1;
          continue;
          paramViewGroup = ChatAttachAlert.this.attachView;
        }
      case 1:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new ShadowSectionCell(this.mContext);
        continue;
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0F)));
      }
    }
  }

  private class PhotoAttachAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    private HashMap<Integer, MediaController.PhotoEntry> selectedPhotos = new HashMap();

    public PhotoAttachAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
    }

    public void clearSelectedPhotos()
    {
      if (!this.selectedPhotos.isEmpty())
      {
        Iterator localIterator = this.selectedPhotos.entrySet().iterator();
        while (localIterator.hasNext())
        {
          MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)((Map.Entry)localIterator.next()).getValue();
          localPhotoEntry.imagePath = null;
          localPhotoEntry.thumbPath = null;
          localPhotoEntry.caption = null;
          localPhotoEntry.stickers.clear();
        }
        this.selectedPhotos.clear();
        ChatAttachAlert.this.updatePhotosButton();
        notifyDataSetChanged();
      }
    }

    public RecyclerListView.Holder createHolder()
    {
      PhotoAttachPhotoCell localPhotoAttachPhotoCell = new PhotoAttachPhotoCell(this.mContext);
      localPhotoAttachPhotoCell.setDelegate(new PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate()
      {
        public void onCheckClick(PhotoAttachPhotoCell paramPhotoAttachPhotoCell)
        {
          MediaController.PhotoEntry localPhotoEntry = paramPhotoAttachPhotoCell.getPhotoEntry();
          boolean bool;
          if (ChatAttachAlert.PhotoAttachAdapter.this.selectedPhotos.containsKey(Integer.valueOf(localPhotoEntry.imageId)))
          {
            ChatAttachAlert.PhotoAttachAdapter.this.selectedPhotos.remove(Integer.valueOf(localPhotoEntry.imageId));
            paramPhotoAttachPhotoCell.setChecked(false, true);
            localPhotoEntry.imagePath = null;
            localPhotoEntry.thumbPath = null;
            localPhotoEntry.stickers.clear();
            if (((Integer)paramPhotoAttachPhotoCell.getTag()).intValue() == MediaController.allPhotosAlbumEntry.photos.size() - 1)
            {
              bool = true;
              paramPhotoAttachPhotoCell.setPhotoEntry(localPhotoEntry, bool);
            }
          }
          while (true)
          {
            ChatAttachAlert.this.updatePhotosButton();
            return;
            bool = false;
            break;
            ChatAttachAlert.PhotoAttachAdapter.this.selectedPhotos.put(Integer.valueOf(localPhotoEntry.imageId), localPhotoEntry);
            paramPhotoAttachPhotoCell.setChecked(true, true);
          }
        }
      });
      return new RecyclerListView.Holder(localPhotoAttachPhotoCell);
    }

    public int getItemCount()
    {
      int i = 0;
      if (ChatAttachAlert.this.deviceHasGoodCamera)
        i = 1;
      int j = i;
      if (MediaController.allPhotosAlbumEntry != null)
        j = i + MediaController.allPhotosAlbumEntry.photos.size();
      return j;
    }

    public int getItemViewType(int paramInt)
    {
      if ((ChatAttachAlert.this.deviceHasGoodCamera) && (paramInt == 0))
        return 1;
      return 0;
    }

    public HashMap<Integer, MediaController.PhotoEntry> getSelectedPhotos()
    {
      return this.selectedPhotos;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool;
      if ((!ChatAttachAlert.this.deviceHasGoodCamera) || (paramInt != 0))
      {
        int i = paramInt;
        if (ChatAttachAlert.this.deviceHasGoodCamera)
          i = paramInt - 1;
        paramViewHolder = (PhotoAttachPhotoCell)paramViewHolder.itemView;
        MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(i);
        if (i == MediaController.allPhotosAlbumEntry.photos.size() - 1)
        {
          bool = true;
          paramViewHolder.setPhotoEntry(localPhotoEntry, bool);
          paramViewHolder.setChecked(this.selectedPhotos.containsKey(Integer.valueOf(localPhotoEntry.imageId)), false);
          paramViewHolder.getImageView().setTag(Integer.valueOf(i));
          paramViewHolder.setTag(Integer.valueOf(i));
        }
      }
      do
      {
        return;
        bool = false;
        break;
      }
      while ((!ChatAttachAlert.this.deviceHasGoodCamera) || (paramInt != 0));
      if ((ChatAttachAlert.this.cameraView != null) && (ChatAttachAlert.this.cameraView.isInitied()))
      {
        paramViewHolder.itemView.setVisibility(4);
        return;
      }
      paramViewHolder.itemView.setVisibility(0);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        if (ChatAttachAlert.this.viewsCache.isEmpty())
          break;
        paramViewGroup = (RecyclerListView.Holder)ChatAttachAlert.this.viewsCache.get(0);
        ChatAttachAlert.this.viewsCache.remove(0);
        return paramViewGroup;
      case 1:
        return new RecyclerListView.Holder(new PhotoAttachCameraCell(this.mContext));
      }
      return createHolder();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.ChatAttachAlert
 * JD-Core Version:    0.6.0
 */