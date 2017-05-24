package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaCodecInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import com.b.a.a.k;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.b;
import com.coremedia.iso.d;
import com.googlecode.mp4parser.c.g;
import com.googlecode.mp4parser.c.h;
import itman.Vidofilm.a.i;
import itman.Vidofilm.a.n;
import itman.Vidofilm.d.e;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MediaController.PhotoEntry;
import org.vidogram.messenger.MediaController.SearchImage;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.UserObject;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.VideoEditedInfo;
import org.vidogram.messenger.browser.Browser;
import org.vidogram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.vidogram.messenger.query.SharedMediaQuery;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.time.FastDateFormat;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.BotInlineResult;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.InputPhoto;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageAction;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_inputPhoto;
import org.vidogram.tgnet.TLRPC.TL_message;
import org.vidogram.tgnet.TLRPC.TL_messageActionEmpty;
import org.vidogram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.vidogram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.vidogram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.vidogram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.vidogram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.vidogram.tgnet.TLRPC.TL_messageService;
import org.vidogram.tgnet.TLRPC.TL_photoEmpty;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.DrawerLayoutContainer;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Adapters.MentionsAdapter;
import org.vidogram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import org.vidogram.ui.Cells.CheckBoxCell;
import org.vidogram.ui.Components.AnimatedFileDrawable;
import org.vidogram.ui.Components.ChatAttachAlert;
import org.vidogram.ui.Components.CheckBox;
import org.vidogram.ui.Components.ClippingImageView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.Paint.Views.ColorPicker;
import org.vidogram.ui.Components.PhotoCropView;
import org.vidogram.ui.Components.PhotoCropView.PhotoCropViewDelegate;
import org.vidogram.ui.Components.PhotoFilterView;
import org.vidogram.ui.Components.PhotoPaintView;
import org.vidogram.ui.Components.PhotoViewerCaptionEnterView;
import org.vidogram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate;
import org.vidogram.ui.Components.PickerBottomLayoutViewer;
import org.vidogram.ui.Components.RadialProgressView;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.vidogram.ui.Components.SeekBar;
import org.vidogram.ui.Components.SeekBar.SeekBarDelegate;
import org.vidogram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.vidogram.ui.Components.StickersAlert;
import org.vidogram.ui.Components.VideoPlayer;
import org.vidogram.ui.Components.VideoPlayer.VideoPlayerDelegate;
import org.vidogram.ui.Components.VideoTimelineView;
import org.vidogram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate;

public class PhotoViewer
  implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener, NotificationCenter.NotificationCenterDelegate
{

  @SuppressLint({"StaticFieldLeak"})
  private static volatile PhotoViewer Instance = null;
  private static DecelerateInterpolator decelerateInterpolator;
  private static final int gallery_menu_delete = 6;
  private static final int gallery_menu_masks = 13;
  private static final int gallery_menu_openin = 11;
  private static final int gallery_menu_save = 1;
  private static final int gallery_menu_send = 3;
  private static final int gallery_menu_share = 10;
  private static final int gallery_menu_showall = 2;
  private static Drawable[] progressDrawables;
  private static Paint progressPaint;
  private ActionBar actionBar;
  private Context actvityContext;
  private boolean allowMentions;
  private boolean allowShare;
  private float animateToScale;
  private float animateToX;
  private float animateToY;
  private ClippingImageView animatingImageView;
  private Runnable animationEndRunnable;
  private int animationInProgress;
  private long animationStartTime;
  private float animationValue;
  private float[][] animationValues = (float[][])Array.newInstance(Float.TYPE, new int[] { 2, 8 });
  private boolean applying;
  private AspectRatioFrameLayout aspectRatioFrameLayout;
  private boolean attachedToWindow;
  private long audioFramesSize;
  private ArrayList<TLRPC.Photo> avatarsArr = new ArrayList();
  private int avatarsDialogId;
  private BackgroundDrawable backgroundDrawable = new BackgroundDrawable(-16777216);
  private int bitrate;
  private Paint blackPaint = new Paint();
  private FrameLayout bottomLayout;
  private boolean bottomTouchEnabled = true;
  private boolean canDragDown = true;
  private boolean canShowBottom = true;
  private boolean canZoom = true;
  private PhotoViewerCaptionEnterView captionEditText;
  private ImageView captionItem;
  private TextView captionTextView;
  private TextView captionTextViewNew;
  private TextView captionTextViewOld;
  private ImageReceiver centerImage = new ImageReceiver();
  private AnimatorSet changeModeAnimation;
  private boolean changingPage;
  private CheckBox checkImageView;
  private int classGuid;
  private ImageView compressItem;
  private int compressionsCount = -1;
  private FrameLayoutDrawer containerView;
  private ImageView cropItem;
  private AnimatorSet currentActionBarAnimation;
  private AnimatedFileDrawable currentAnimation;
  private TLRPC.BotInlineResult currentBotInlineResult;
  private long currentDialogId;
  private int currentEditMode;
  private TLRPC.FileLocation currentFileLocation;
  private String[] currentFileNames = new String[3];
  private int currentIndex;
  private MessageObject currentMessageObject;
  private String currentPathObject;
  private PlaceProviderObject currentPlaceObject;
  private File currentPlayingVideoFile;
  private String currentSubtitle;
  private Bitmap currentThumb;
  private TLRPC.FileLocation currentUserAvatarLocation = null;
  private TextView dateTextView;
  private boolean disableShowCheck;
  private boolean discardTap;
  private boolean doneButtonPressed;
  private boolean dontResetZoomOnFirstLayout;
  private boolean doubleTap;
  private float dragY;
  private boolean draggingDown;
  private PickerBottomLayoutViewer editorDoneLayout;
  private boolean[] endReached = { 0, 1 };
  private long endTime;
  private long estimatedDuration;
  private int estimatedSize;
  private GestureDetector gestureDetector;
  private PlaceProviderObject hideAfterAnimation;
  private boolean ignoreDidSetImage;
  private AnimatorSet imageMoveAnimation;
  private ArrayList<MessageObject> imagesArr = new ArrayList();
  private ArrayList<Object> imagesArrLocals = new ArrayList();
  private ArrayList<TLRPC.FileLocation> imagesArrLocations = new ArrayList();
  private ArrayList<Integer> imagesArrLocationsSizes = new ArrayList();
  private ArrayList<MessageObject> imagesArrTemp = new ArrayList();
  private HashMap<Integer, MessageObject>[] imagesByIds = { new HashMap(), new HashMap() };
  private HashMap<Integer, MessageObject>[] imagesByIdsTemp = { new HashMap(), new HashMap() };
  private boolean inPreview;
  private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5F);
  private boolean invalidCoords;
  private boolean isActionBarVisible = true;
  private boolean isFirstLoading;
  private boolean isPlaying;
  private boolean isVisible;
  private Object lastInsets;
  private String lastTitle;
  private ImageReceiver leftImage = new ImageReceiver();
  private boolean loadInitialVideo;
  private boolean loadingMoreImages;
  private ActionBarMenuItem masksItem;
  private float maxX;
  private float maxY;
  private LinearLayoutManager mentionLayoutManager;
  private AnimatorSet mentionListAnimation;
  private RecyclerListView mentionListView;
  private MentionsAdapter mentionsAdapter;
  private ActionBarMenuItem menuItem;
  private long mergeDialogId;
  private float minX;
  private float minY;
  private float moveStartX;
  private float moveStartY;
  private boolean moving;
  private ImageView muteItem;
  private boolean muteVideo;
  private TextView nameTextView;
  private boolean needCaptionLayout;
  private boolean needSearchImageInArr;
  private boolean opennedFromMedia;
  private int originalBitrate;
  private int originalHeight;
  private long originalSize;
  private int originalWidth;
  private ImageView paintItem;
  private Activity parentActivity;
  private ChatAttachAlert parentAlert;
  private ChatActivity parentChatActivity;
  private PhotoCropView photoCropView;
  private PhotoFilterView photoFilterView;
  private PhotoPaintView photoPaintView;
  private PhotoProgressView[] photoProgressViews = new PhotoProgressView[3];
  private PickerBottomLayoutViewer pickerView;
  private float pinchCenterX;
  private float pinchCenterY;
  private float pinchStartDistance;
  private float pinchStartScale = 1.0F;
  private float pinchStartX;
  private float pinchStartY;
  private PhotoViewerProvider placeProvider;
  private boolean playAdvertising;
  private int previewViewEnd;
  private int previousCompression;
  private RadialProgressView progressView;
  private QualityChooseView qualityChooseView;
  private AnimatorSet qualityChooseViewAnimation;
  private PickerBottomLayoutViewer qualityPicker;
  private boolean requestingPreview;
  private TextView resetButton;
  private int resultHeight;
  private int resultWidth;
  private ImageReceiver rightImage = new ImageReceiver();
  private int rotationValue;
  private float scale = 1.0F;
  private Scroller scroller;
  private int selectedCompression;
  private int sendPhotoType;
  private ImageView shareButton;
  private PlaceProviderObject showAfterAnimation;
  private long startTime;
  private int switchImageAfterAnimation;
  private boolean textureUploaded;
  private int totalImagesCount;
  private int totalImagesCountMerge;
  private long transitionAnimationStartTime;
  private float translationX;
  private float translationY;
  private boolean tryStartRequestPreviewOnFinish;
  private ImageView tuneItem;
  private Runnable updateProgressRunnable = new Runnable()
  {
    public void run()
    {
      float f1 = 0.0F;
      float f2;
      if ((PhotoViewer.this.videoPlayer != null) && (PhotoViewer.this.videoPlayerSeekbar != null) && (!PhotoViewer.this.videoPlayerSeekbar.isDragging()))
      {
        f2 = (float)PhotoViewer.this.videoPlayer.getCurrentPosition() / (float)PhotoViewer.this.videoPlayer.getDuration();
        if ((PhotoViewer.this.inPreview) || (PhotoViewer.this.videoTimelineViewContainer == null) || (PhotoViewer.this.videoTimelineViewContainer.getVisibility() != 0))
          break label267;
        if (f2 >= PhotoViewer.this.videoTimelineView.getRightProgress())
        {
          PhotoViewer.this.videoPlayer.pause();
          PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0F);
          PhotoViewer.this.videoPlayer.seekTo((int)(PhotoViewer.this.videoTimelineView.getLeftProgress() * (float)PhotoViewer.this.videoPlayer.getDuration()));
          PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
          PhotoViewer.this.updateVideoPlayerTime();
        }
      }
      else
      {
        if (PhotoViewer.this.isPlaying)
          AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable);
        return;
      }
      f2 -= PhotoViewer.this.videoTimelineView.getLeftProgress();
      if (f2 < 0.0F);
      while (true)
      {
        f2 = f1 / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
        f1 = f2;
        if (f2 > 1.0F)
          f1 = 1.0F;
        PhotoViewer.this.videoPlayerSeekbar.setProgress(f1);
        break;
        label267: PhotoViewer.this.videoPlayerSeekbar.setProgress(f2);
        break;
        f1 = f2;
      }
    }
  };
  private VelocityTracker velocityTracker;
  private float videoCrossfadeAlpha;
  private long videoCrossfadeAlphaLastTime;
  private boolean videoCrossfadeStarted;
  private float videoDuration;
  private long videoFramesSize;
  private ImageView videoPlayButton;
  private VideoPlayer videoPlayer;
  private FrameLayout videoPlayerControlFrameLayout;
  private SeekBar videoPlayerSeekbar;
  private TextView videoPlayerTime;
  private MessageObject videoPreviewMessageObject;
  private TextureView videoTextureView;
  private VideoTimelineView videoTimelineView;
  private FrameLayout videoTimelineViewContainer;
  private TextView videoUrl;
  private AlertDialog visibleDialog;
  private boolean wasLayout;
  private WindowManager.LayoutParams windowLayoutParams;
  private FrameLayout windowView;
  private boolean zoomAnimation;
  private boolean zooming;

  public PhotoViewer()
  {
    this.blackPaint.setColor(-16777216);
  }

  private void animateTo(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean)
  {
    animateTo(paramFloat1, paramFloat2, paramFloat3, paramBoolean, 250);
  }

  private void animateTo(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean, int paramInt)
  {
    if ((this.scale == paramFloat1) && (this.translationX == paramFloat2) && (this.translationY == paramFloat3))
      return;
    this.zoomAnimation = paramBoolean;
    this.animateToScale = paramFloat1;
    this.animateToX = paramFloat2;
    this.animateToY = paramFloat3;
    this.animationStartTime = System.currentTimeMillis();
    this.imageMoveAnimation = new AnimatorSet();
    this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "animationValue", new float[] { 0.0F, 1.0F }) });
    this.imageMoveAnimation.setInterpolator(this.interpolator);
    this.imageMoveAnimation.setDuration(paramInt);
    this.imageMoveAnimation.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        PhotoViewer.access$11302(PhotoViewer.this, null);
        PhotoViewer.this.containerView.invalidate();
      }
    });
    this.imageMoveAnimation.start();
  }

  private void applyCurrentEditMode()
  {
    Bitmap localBitmap;
    Object localObject1;
    if (this.currentEditMode == 1)
    {
      localBitmap = this.photoCropView.getBitmap();
      localObject1 = null;
    }
    while (true)
    {
      TLRPC.PhotoSize localPhotoSize;
      Object localObject2;
      label153: float f1;
      float f2;
      if (localBitmap != null)
      {
        localPhotoSize = ImageLoader.scaleAndSaveImage(localBitmap, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
        if (localPhotoSize != null)
        {
          localObject2 = this.imagesArrLocals.get(this.currentIndex);
          if (!(localObject2 instanceof MediaController.PhotoEntry))
            break label453;
          localObject2 = (MediaController.PhotoEntry)localObject2;
          ((MediaController.PhotoEntry)localObject2).imagePath = FileLoader.getPathToAttach(localPhotoSize, true).toString();
          localPhotoSize = ImageLoader.scaleAndSaveImage(localBitmap, AndroidUtilities.dp(120.0F), AndroidUtilities.dp(120.0F), 70, false, 101, 101);
          if (localPhotoSize != null)
            ((MediaController.PhotoEntry)localObject2).thumbPath = FileLoader.getPathToAttach(localPhotoSize, true).toString();
          if (localObject1 != null)
            ((MediaController.PhotoEntry)localObject2).stickers.addAll((Collection)localObject1);
          if ((this.sendPhotoType == 0) && (this.placeProvider != null))
          {
            this.placeProvider.updatePhotoAtIndex(this.currentIndex);
            if (!this.placeProvider.isPhotoChecked(this.currentIndex))
            {
              this.placeProvider.setPhotoChecked(this.currentIndex);
              this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), true);
              updateSelectedCount();
            }
          }
          if (this.currentEditMode == 1)
          {
            f1 = this.photoCropView.getRectSizeX() / getContainerViewWidth();
            f2 = this.photoCropView.getRectSizeY() / getContainerViewHeight();
            if (f1 <= f2)
              break label547;
          }
        }
      }
      while (true)
      {
        this.scale = f1;
        this.translationX = (this.photoCropView.getRectX() + this.photoCropView.getRectSizeX() / 2.0F - getContainerViewWidth() / 2);
        this.translationY = (this.photoCropView.getRectY() + this.photoCropView.getRectSizeY() / 2.0F - getContainerViewHeight() / 2);
        this.zoomAnimation = true;
        this.applying = true;
        this.photoCropView.onDisappear();
        this.centerImage.setParentView(null);
        this.centerImage.setOrientation(0, true);
        this.ignoreDidSetImage = true;
        this.centerImage.setImageBitmap(localBitmap);
        this.ignoreDidSetImage = false;
        this.centerImage.setParentView(this.containerView);
        return;
        if (this.currentEditMode == 2)
        {
          localBitmap = this.photoFilterView.getBitmap();
          localObject1 = null;
          break;
        }
        if (this.currentEditMode != 3)
          break label552;
        localBitmap = this.photoPaintView.getBitmap();
        localObject1 = this.photoPaintView.getMasks();
        break;
        label453: if (!(localObject2 instanceof MediaController.SearchImage))
          break label153;
        localObject2 = (MediaController.SearchImage)localObject2;
        ((MediaController.SearchImage)localObject2).imagePath = FileLoader.getPathToAttach(localPhotoSize, true).toString();
        localPhotoSize = ImageLoader.scaleAndSaveImage(localBitmap, AndroidUtilities.dp(120.0F), AndroidUtilities.dp(120.0F), 70, false, 101, 101);
        if (localPhotoSize != null)
          ((MediaController.SearchImage)localObject2).thumbPath = FileLoader.getPathToAttach(localPhotoSize, true).toString();
        if (localObject1 == null)
          break label153;
        ((MediaController.SearchImage)localObject2).stickers.addAll((Collection)localObject1);
        break label153;
        label547: f1 = f2;
      }
      label552: localObject1 = null;
      localBitmap = null;
    }
  }

  private boolean checkAnimation()
  {
    int i = 0;
    if ((this.animationInProgress != 0) && (Math.abs(this.transitionAnimationStartTime - System.currentTimeMillis()) >= 500L))
    {
      if (this.animationEndRunnable != null)
      {
        this.animationEndRunnable.run();
        this.animationEndRunnable = null;
      }
      this.animationInProgress = 0;
    }
    if (this.animationInProgress != 0)
      i = 1;
    return i;
  }

  private void checkMinMax(boolean paramBoolean)
  {
    float f1 = this.translationX;
    float f2 = this.translationY;
    updateMinMax(this.scale);
    if (this.translationX < this.minX)
    {
      f1 = this.minX;
      if (this.translationY >= this.minY)
        break label84;
      f2 = this.minY;
    }
    while (true)
    {
      animateTo(this.scale, f1, f2, paramBoolean);
      return;
      if (this.translationX <= this.maxX)
        break;
      f1 = this.maxX;
      break;
      label84: if (this.translationY <= this.maxY)
        continue;
      f2 = this.maxY;
    }
  }

  private void checkProgress(int paramInt, boolean paramBoolean)
  {
    boolean bool3 = false;
    int i;
    Object localObject1;
    Object localObject2;
    boolean bool1;
    if (this.currentFileNames[paramInt] != null)
    {
      int j = this.currentIndex;
      if (paramInt == 1)
        i = j + 1;
      while (true)
      {
        localObject1 = null;
        if (this.currentMessageObject != null)
        {
          MessageObject localMessageObject = (MessageObject)this.imagesArr.get(i);
          if (!TextUtils.isEmpty(localMessageObject.messageOwner.attachPath))
          {
            localObject2 = new File(localMessageObject.messageOwner.attachPath);
            localObject1 = localObject2;
            if (!((File)localObject2).exists())
              localObject1 = null;
          }
          localObject2 = localObject1;
          if (localObject1 == null)
            localObject2 = FileLoader.getPathToMessage(localMessageObject.messageOwner);
          bool1 = localMessageObject.isVideo();
          localObject1 = localObject2;
          if ((localObject1 == null) || (!((File)localObject1).exists()))
            break label615;
          if (bool1)
          {
            this.photoProgressViews[paramInt].setBackgroundState(3, paramBoolean);
            label156: if (paramInt == 0)
            {
              if (this.imagesArrLocals.isEmpty())
              {
                paramBoolean = bool3;
                if (this.currentFileNames[0] != null)
                {
                  paramBoolean = bool3;
                  if (!bool1)
                  {
                    paramBoolean = bool3;
                    if (this.photoProgressViews[0].backgroundState == 0);
                  }
                }
              }
              else
              {
                paramBoolean = true;
              }
              this.canZoom = paramBoolean;
            }
            return;
            i = j;
            if (paramInt != 2)
              continue;
            i = j - 1;
            continue;
          }
        }
        else if (this.currentBotInlineResult != null)
        {
          localObject1 = (TLRPC.BotInlineResult)this.imagesArrLocals.get(i);
          if ((((TLRPC.BotInlineResult)localObject1).type.equals("video")) || (MessageObject.isVideoDocument(((TLRPC.BotInlineResult)localObject1).document)))
          {
            if (((TLRPC.BotInlineResult)localObject1).document == null)
              break;
            localObject2 = FileLoader.getPathToAttach(((TLRPC.BotInlineResult)localObject1).document);
          }
        }
      }
    }
    while (true)
    {
      label292: boolean bool2 = true;
      while (true)
      {
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          bool1 = bool2;
          if (((File)localObject2).exists())
            break;
        }
        localObject1 = new File(FileLoader.getInstance().getDirectory(4), this.currentFileNames[paramInt]);
        bool1 = bool2;
        break;
        if (((TLRPC.BotInlineResult)localObject1).content_url == null)
          break label757;
        localObject2 = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(((TLRPC.BotInlineResult)localObject1).content_url) + "." + ImageLoader.getHttpUrlExtension(((TLRPC.BotInlineResult)localObject1).content_url, "mp4"));
        break label292;
        if (((TLRPC.BotInlineResult)localObject1).document != null)
        {
          localObject2 = new File(FileLoader.getInstance().getDirectory(3), this.currentFileNames[paramInt]);
          bool2 = false;
          continue;
        }
        if (((TLRPC.BotInlineResult)localObject1).photo != null)
        {
          localObject2 = new File(FileLoader.getInstance().getDirectory(0), this.currentFileNames[paramInt]);
          bool2 = false;
          continue;
          if (this.currentFileLocation != null)
          {
            localObject1 = (TLRPC.FileLocation)this.imagesArrLocations.get(i);
            if (this.avatarsDialogId != 0);
            for (bool1 = true; ; bool1 = false)
            {
              localObject1 = FileLoader.getPathToAttach((TLObject)localObject1, bool1);
              bool1 = false;
              break;
            }
          }
          if (this.currentPathObject != null)
          {
            localObject1 = new File(FileLoader.getInstance().getDirectory(3), this.currentFileNames[paramInt]);
            if (!((File)localObject1).exists())
            {
              localObject1 = new File(FileLoader.getInstance().getDirectory(4), this.currentFileNames[paramInt]);
              bool1 = false;
              break;
              this.photoProgressViews[paramInt].setBackgroundState(-1, paramBoolean);
              break label156;
              label615: if (bool1)
                if (!FileLoader.getInstance().isLoadingFile(this.currentFileNames[paramInt]))
                  this.photoProgressViews[paramInt].setBackgroundState(2, false);
              while (true)
              {
                localObject2 = ImageLoader.getInstance().getFileProgress(this.currentFileNames[paramInt]);
                localObject1 = localObject2;
                if (localObject2 == null)
                  localObject1 = Float.valueOf(0.0F);
                this.photoProgressViews[paramInt].setProgress(((Float)localObject1).floatValue(), false);
                break;
                this.photoProgressViews[paramInt].setBackgroundState(1, false);
                continue;
                this.photoProgressViews[paramInt].setBackgroundState(0, paramBoolean);
              }
              this.photoProgressViews[paramInt].setBackgroundState(-1, paramBoolean);
              return;
            }
            bool1 = false;
            break;
          }
          localObject1 = null;
          bool1 = false;
          break;
        }
        localObject2 = null;
        bool2 = false;
      }
      label757: localObject2 = null;
    }
  }

  private void closeCaptionEnter(boolean paramBoolean)
  {
    if ((this.currentIndex < 0) || (this.currentIndex >= this.imagesArrLocals.size()))
      return;
    Object localObject2 = this.imagesArrLocals.get(this.currentIndex);
    if (paramBoolean)
    {
      if (!(localObject2 instanceof MediaController.PhotoEntry))
        break label293;
      ((MediaController.PhotoEntry)localObject2).caption = this.captionEditText.getFieldCharSequence();
      if ((this.captionEditText.getFieldCharSequence().length() != 0) && (!this.placeProvider.isPhotoChecked(this.currentIndex)))
      {
        this.placeProvider.setPhotoChecked(this.currentIndex);
        this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), true);
        updateSelectedCount();
      }
    }
    this.captionEditText.setTag(null);
    if (this.lastTitle != null)
    {
      this.actionBar.setTitle(this.lastTitle);
      this.lastTitle = null;
    }
    Object localObject1;
    if (this.captionItem.getVisibility() == 0)
    {
      ActionBar localActionBar = this.actionBar;
      if (!this.muteVideo)
        break label319;
      localObject1 = LocaleController.getString("AttachGif", 2131165364);
      label195: localActionBar.setTitle((CharSequence)localObject1);
      localActionBar = this.actionBar;
      if (!this.muteVideo)
        break label332;
      localObject1 = null;
      label216: localActionBar.setSubtitle((CharSequence)localObject1);
      localObject1 = this.captionItem;
      if (this.captionEditText.getFieldCharSequence().length() != 0)
        break label340;
    }
    label293: label319: label332: label340: for (int i = 2130838007; ; i = 2130838008)
    {
      ((ImageView)localObject1).setImageResource(i);
      updateCaptionTextForCurrentPhoto(localObject2);
      setCurrentCaption(this.captionEditText.getFieldCharSequence());
      if (this.captionEditText.isPopupShowing())
        this.captionEditText.hidePopup();
      this.captionEditText.closeKeyboard();
      return;
      if (!(localObject2 instanceof MediaController.SearchImage))
        break;
      ((MediaController.SearchImage)localObject2).caption = this.captionEditText.getFieldCharSequence();
      break;
      localObject1 = LocaleController.getString("AttachVideo", 2131165369);
      break label195;
      localObject1 = this.currentSubtitle;
      break label216;
    }
  }

  private void didChangedCompressionLevel(boolean paramBoolean)
  {
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
    localEditor.putInt("compress_video2", this.selectedCompression);
    localEditor.commit();
    updateWidthHeightBitrateForCompression();
    updateVideoInfo();
    if (paramBoolean)
      requestVideoPreview(1);
  }

  private int getAdditionX()
  {
    if ((this.currentEditMode != 0) && (this.currentEditMode != 3))
      return AndroidUtilities.dp(14.0F);
    return 0;
  }

  private int getAdditionY()
  {
    int k = 0;
    int j = 0;
    int i = 0;
    if (this.currentEditMode == 3)
    {
      j = ActionBar.getCurrentActionBarHeight();
      if (Build.VERSION.SDK_INT >= 21)
        i = AndroidUtilities.statusBarHeight;
      i += j;
    }
    do
    {
      return i;
      i = k;
    }
    while (this.currentEditMode == 0);
    k = AndroidUtilities.dp(14.0F);
    i = j;
    if (Build.VERSION.SDK_INT >= 21)
      i = AndroidUtilities.statusBarHeight;
    return i + k;
  }

  private int getContainerViewHeight()
  {
    return getContainerViewHeight(this.currentEditMode);
  }

  private int getContainerViewHeight(int paramInt)
  {
    int j = AndroidUtilities.displaySize.y;
    int i = j;
    if (paramInt == 0)
    {
      i = j;
      if (Build.VERSION.SDK_INT >= 21)
        i = j + AndroidUtilities.statusBarHeight;
    }
    if (paramInt == 1)
      j = i - AndroidUtilities.dp(144.0F);
    do
    {
      return j;
      if (paramInt == 2)
        return i - AndroidUtilities.dp(154.0F);
      j = i;
    }
    while (paramInt != 3);
    return i - (AndroidUtilities.dp(48.0F) + ActionBar.getCurrentActionBarHeight());
  }

  private int getContainerViewWidth()
  {
    return getContainerViewWidth(this.currentEditMode);
  }

  private int getContainerViewWidth(int paramInt)
  {
    int j = this.containerView.getWidth();
    int i = j;
    if (paramInt != 0)
    {
      i = j;
      if (paramInt != 3)
        i = j - AndroidUtilities.dp(28.0F);
    }
    return i;
  }

  private TLObject getFileLocation(int paramInt, int[] paramArrayOfInt)
  {
    if (paramInt < 0)
      return null;
    if (!this.imagesArrLocations.isEmpty())
    {
      if (paramInt >= this.imagesArrLocations.size())
        return null;
      paramArrayOfInt[0] = ((Integer)this.imagesArrLocationsSizes.get(paramInt)).intValue();
      return (TLObject)this.imagesArrLocations.get(paramInt);
    }
    Object localObject;
    if (!this.imagesArr.isEmpty())
    {
      if (paramInt >= this.imagesArr.size())
        return null;
      localObject = (MessageObject)this.imagesArr.get(paramInt);
      if (!(((MessageObject)localObject).messageOwner instanceof TLRPC.TL_messageService))
        break label173;
      if ((((MessageObject)localObject).messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto))
        return ((MessageObject)localObject).messageOwner.action.newUserPhoto.photo_big;
      localObject = FileLoader.getClosestPhotoSizeWithSize(((MessageObject)localObject).photoThumbs, AndroidUtilities.getPhotoSize());
      if (localObject != null)
      {
        paramArrayOfInt[0] = ((TLRPC.PhotoSize)localObject).size;
        if (paramArrayOfInt[0] == 0)
          paramArrayOfInt[0] = -1;
        return ((TLRPC.PhotoSize)localObject).location;
      }
      paramArrayOfInt[0] = -1;
    }
    label173: 
    do
    {
      while (true)
      {
        return null;
        if (((!(((MessageObject)localObject).messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) || (((MessageObject)localObject).messageOwner.media.photo == null)) && ((!(((MessageObject)localObject).messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) || (((MessageObject)localObject).messageOwner.media.webpage == null)))
          break;
        localObject = FileLoader.getClosestPhotoSizeWithSize(((MessageObject)localObject).photoThumbs, AndroidUtilities.getPhotoSize());
        if (localObject != null)
        {
          paramArrayOfInt[0] = ((TLRPC.PhotoSize)localObject).size;
          if (paramArrayOfInt[0] == 0)
            paramArrayOfInt[0] = -1;
          return ((TLRPC.PhotoSize)localObject).location;
        }
        paramArrayOfInt[0] = -1;
      }
      if ((((MessageObject)localObject).messageOwner.media instanceof TLRPC.TL_messageMediaInvoice))
        return ((TLRPC.TL_messageMediaInvoice)((MessageObject)localObject).messageOwner.media).photo;
    }
    while ((((MessageObject)localObject).getDocument() == null) || (((MessageObject)localObject).getDocument().thumb == null));
    paramArrayOfInt[0] = ((MessageObject)localObject).getDocument().thumb.size;
    if (paramArrayOfInt[0] == 0)
      paramArrayOfInt[0] = -1;
    return (TLObject)((MessageObject)localObject).getDocument().thumb.location;
  }

  private String getFileName(int paramInt)
  {
    if (paramInt < 0)
      return null;
    Object localObject;
    if ((!this.imagesArrLocations.isEmpty()) || (!this.imagesArr.isEmpty()))
    {
      if (!this.imagesArrLocations.isEmpty())
      {
        if (paramInt >= this.imagesArrLocations.size())
          return null;
        localObject = (TLRPC.FileLocation)this.imagesArrLocations.get(paramInt);
        return ((TLRPC.FileLocation)localObject).volume_id + "_" + ((TLRPC.FileLocation)localObject).local_id + ".jpg";
      }
      if (!this.imagesArr.isEmpty())
      {
        if (paramInt >= this.imagesArr.size())
          return null;
        return FileLoader.getMessageFileName(((MessageObject)this.imagesArr.get(paramInt)).messageOwner);
      }
    }
    else if (!this.imagesArrLocals.isEmpty())
    {
      if (paramInt >= this.imagesArrLocals.size())
        return null;
      localObject = this.imagesArrLocals.get(paramInt);
      if ((localObject instanceof MediaController.SearchImage))
      {
        localObject = (MediaController.SearchImage)localObject;
        if (((MediaController.SearchImage)localObject).document != null)
          return FileLoader.getAttachFileName(((MediaController.SearchImage)localObject).document);
        if ((((MediaController.SearchImage)localObject).type != 1) && (((MediaController.SearchImage)localObject).localUrl != null) && (((MediaController.SearchImage)localObject).localUrl.length() > 0))
        {
          File localFile = new File(((MediaController.SearchImage)localObject).localUrl);
          if (localFile.exists())
            return localFile.getName();
          ((MediaController.SearchImage)localObject).localUrl = "";
        }
        return Utilities.MD5(((MediaController.SearchImage)localObject).imageUrl) + "." + ImageLoader.getHttpUrlExtension(((MediaController.SearchImage)localObject).imageUrl, "jpg");
      }
      if ((localObject instanceof TLRPC.BotInlineResult))
      {
        localObject = (TLRPC.BotInlineResult)localObject;
        if (((TLRPC.BotInlineResult)localObject).document != null)
          return FileLoader.getAttachFileName(((TLRPC.BotInlineResult)localObject).document);
        if (((TLRPC.BotInlineResult)localObject).photo != null)
          return FileLoader.getAttachFileName(FileLoader.getClosestPhotoSizeWithSize(((TLRPC.BotInlineResult)localObject).photo.sizes, AndroidUtilities.getPhotoSize()));
        if (((TLRPC.BotInlineResult)localObject).content_url != null)
          return Utilities.MD5(((TLRPC.BotInlineResult)localObject).content_url) + "." + ImageLoader.getHttpUrlExtension(((TLRPC.BotInlineResult)localObject).content_url, "jpg");
      }
    }
    return (String)null;
  }

  public static PhotoViewer getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        PhotoViewer localPhotoViewer = Instance;
        localObject1 = localPhotoViewer;
        if (localPhotoViewer == null)
        {
          localObject1 = new PhotoViewer();
          Instance = (PhotoViewer)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (PhotoViewer)localObject2;
  }

  private void goToNext()
  {
    float f = 0.0F;
    if (this.scale != 1.0F)
      f = (getContainerViewWidth() - this.centerImage.getImageWidth()) / 2 * this.scale;
    this.switchImageAfterAnimation = 1;
    animateTo(this.scale, this.minX - getContainerViewWidth() - f - AndroidUtilities.dp(30.0F) / 2, this.translationY, false);
  }

  private void goToPrev()
  {
    float f = 0.0F;
    if (this.scale != 1.0F)
      f = (getContainerViewWidth() - this.centerImage.getImageWidth()) / 2 * this.scale;
    this.switchImageAfterAnimation = 2;
    animateTo(this.scale, f + (this.maxX + getContainerViewWidth()) + AndroidUtilities.dp(30.0F) / 2, this.translationY, false);
  }

  private void onActionClick(boolean paramBoolean)
  {
    Intent localIntent = null;
    if (((this.currentMessageObject == null) && (this.currentBotInlineResult == null)) || (this.currentFileNames[0] == null))
      return;
    File localFile;
    if (this.currentMessageObject != null)
    {
      if ((this.currentMessageObject.messageOwner.attachPath == null) || (this.currentMessageObject.messageOwner.attachPath.length() == 0))
        break label515;
      localFile = new File(this.currentMessageObject.messageOwner.attachPath);
      localObject = localFile;
      if (localFile.exists());
    }
    label515: for (Object localObject = null; ; localObject = null)
    {
      if (localObject == null)
      {
        localFile = FileLoader.getPathToMessage(this.currentMessageObject.messageOwner);
        localObject = localFile;
        if (!localFile.exists())
          localObject = localIntent;
      }
      while (true)
      {
        if (localObject == null)
        {
          if (!paramBoolean)
            break;
          if (this.currentMessageObject != null)
          {
            if (!FileLoader.getInstance().isLoadingFile(this.currentFileNames[0]))
            {
              FileLoader.getInstance().loadFile(this.currentMessageObject.getDocument(), true, false);
              return;
              localObject = localIntent;
              if (this.currentBotInlineResult == null)
                continue;
              if (this.currentBotInlineResult.document != null)
              {
                localFile = FileLoader.getPathToAttach(this.currentBotInlineResult.document);
                localObject = localIntent;
                if (!localFile.exists())
                  continue;
                localObject = localFile;
                continue;
              }
              localFile = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.currentBotInlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(this.currentBotInlineResult.content_url, "mp4"));
              localObject = localIntent;
              if (!localFile.exists())
                continue;
              localObject = localFile;
              continue;
            }
            FileLoader.getInstance().cancelLoadFile(this.currentMessageObject.getDocument());
            return;
          }
          if (this.currentBotInlineResult == null)
            break;
          if (this.currentBotInlineResult.document != null)
          {
            if (!FileLoader.getInstance().isLoadingFile(this.currentFileNames[0]))
            {
              FileLoader.getInstance().loadFile(this.currentBotInlineResult.document, true, false);
              return;
            }
            FileLoader.getInstance().cancelLoadFile(this.currentBotInlineResult.document);
            return;
          }
          if (!ImageLoader.getInstance().isLoadingHttpFile(this.currentBotInlineResult.content_url))
          {
            ImageLoader.getInstance().loadHttpFile(this.currentBotInlineResult.content_url, "mp4");
            return;
          }
          ImageLoader.getInstance().cancelLoadHttpFile(this.currentBotInlineResult.content_url);
          return;
        }
        if (Build.VERSION.SDK_INT >= 16)
        {
          preparePlayer((File)localObject, true, false, e.a(ApplicationLoader.applicationContext).c());
          return;
        }
        localIntent = new Intent("android.intent.action.VIEW");
        if (Build.VERSION.SDK_INT >= 24)
        {
          localIntent.setFlags(1);
          localIntent.setDataAndType(FileProvider.a(this.parentActivity, "org.vidogram.messenger.provider", (File)localObject), "video/mp4");
        }
        while (true)
        {
          this.parentActivity.startActivityForResult(localIntent, 500);
          return;
          localIntent.setDataAndType(Uri.fromFile((File)localObject), "video/mp4");
        }
      }
    }
  }

  @SuppressLint({"NewApi"})
  private void onDraw(Canvas paramCanvas)
  {
    if ((this.animationInProgress == 1) || ((!this.isVisible) && (this.animationInProgress != 2)))
      return;
    float f1 = -1.0F;
    float f4;
    float f5;
    float f6;
    float f7;
    float f8;
    float f3;
    float f2;
    label282: ImageReceiver localImageReceiver;
    if (this.imageMoveAnimation != null)
    {
      if (!this.scroller.isFinished())
        this.scroller.abortAnimation();
      f4 = this.scale;
      f5 = this.animateToScale;
      f6 = this.scale;
      f7 = this.animationValue;
      f8 = this.translationX;
      float f9 = this.animateToX;
      float f10 = this.translationX;
      float f11 = this.animationValue;
      f3 = this.translationY + (this.animateToY - this.translationY) * this.animationValue;
      if (this.currentEditMode == 1)
        this.photoCropView.setAnimationProgress(this.animationValue);
      f2 = f1;
      if (this.animateToScale == 1.0F)
      {
        f2 = f1;
        if (this.scale == 1.0F)
        {
          f2 = f1;
          if (this.translationX == 0.0F)
            f2 = f3;
        }
      }
      this.containerView.invalidate();
      f1 = (f9 - f10) * f11 + f8;
      f4 = (f5 - f6) * f7 + f4;
      if ((this.currentEditMode != 0) || (this.scale != 1.0F) || (f2 == -1.0F) || (this.zoomAnimation))
        break label1845;
      f5 = getContainerViewHeight() / 4.0F;
      this.backgroundDrawable.setAlpha((int)Math.max(127.0F, (1.0F - Math.min(Math.abs(f2), f5) / f5) * 255.0F));
      localImageReceiver = null;
      if (this.currentEditMode == 0)
      {
        if ((this.scale < 1.0F) || (this.zoomAnimation) || (this.zooming))
          break label1917;
        if (f1 <= this.maxX + AndroidUtilities.dp(5.0F))
          break label1858;
        localImageReceiver = this.leftImage;
      }
    }
    while (true)
    {
      label338: boolean bool;
      if (localImageReceiver != null)
      {
        bool = true;
        label346: this.changingPage = bool;
        if (localImageReceiver == this.rightImage)
        {
          f5 = 0.0F;
          f2 = 1.0F;
          if ((this.zoomAnimation) || (f1 >= this.minX))
            break label1911;
          f2 = Math.min(1.0F, (this.minX - f1) / paramCanvas.getWidth());
          f5 = (1.0F - f2) * 0.3F;
          f6 = -paramCanvas.getWidth() - AndroidUtilities.dp(30.0F) / 2;
        }
      }
      while (true)
      {
        int j;
        int i;
        if (localImageReceiver.hasBitmapImage())
        {
          paramCanvas.save();
          paramCanvas.translate(getContainerViewWidth() / 2, getContainerViewHeight() / 2);
          paramCanvas.translate(paramCanvas.getWidth() + AndroidUtilities.dp(30.0F) / 2 + f6, 0.0F);
          paramCanvas.scale(1.0F - f5, 1.0F - f5);
          j = localImageReceiver.getBitmapWidth();
          i = localImageReceiver.getBitmapHeight();
          f7 = getContainerViewWidth() / j;
          f8 = getContainerViewHeight() / i;
          if (f7 > f8)
          {
            f7 = f8;
            label538: j = (int)(j * f7);
            i = (int)(f7 * i);
            localImageReceiver.setAlpha(f2);
            localImageReceiver.setImageCoords(-j / 2, -i / 2, j, i);
            localImageReceiver.draw(paramCanvas);
            paramCanvas.restore();
          }
        }
        else
        {
          paramCanvas.save();
          paramCanvas.translate(f6, f3 / f4);
          paramCanvas.translate((paramCanvas.getWidth() * (this.scale + 1.0F) + AndroidUtilities.dp(30.0F)) / 2.0F, -f3 / f4);
          this.photoProgressViews[1].setScale(1.0F - f5);
          this.photoProgressViews[1].setAlpha(f2);
          this.photoProgressViews[1].onDraw(paramCanvas);
          paramCanvas.restore();
          f5 = 0.0F;
          f2 = 1.0F;
          if ((this.zoomAnimation) || (f1 <= this.maxX) || (this.currentEditMode != 0))
            break label1905;
          f2 = Math.min(1.0F, (f1 - this.maxX) / paramCanvas.getWidth());
          f5 = 0.3F * f2;
          f2 = 1.0F - f2;
          f6 = this.maxX;
        }
        while (true)
        {
          if ((Build.VERSION.SDK_INT >= 16) && (this.aspectRatioFrameLayout != null) && (this.aspectRatioFrameLayout.getVisibility() == 0))
          {
            i = 1;
            label767: if (this.centerImage.hasBitmapImage())
            {
              paramCanvas.save();
              paramCanvas.translate(getContainerViewWidth() / 2 + getAdditionX(), getContainerViewHeight() / 2 + getAdditionY());
              paramCanvas.translate(f6, f3);
              paramCanvas.scale(f4 - f5, f4 - f5);
              if (this.currentEditMode == 1)
                this.photoCropView.setBitmapParams(f4, f6, f3);
              int m = this.centerImage.getBitmapWidth();
              int n = this.centerImage.getBitmapHeight();
              j = n;
              int k = m;
              if (i != 0)
              {
                j = n;
                k = m;
                if (this.textureUploaded)
                {
                  j = n;
                  k = m;
                  if (Math.abs(m / n - this.videoTextureView.getMeasuredWidth() / this.videoTextureView.getMeasuredHeight()) > 0.01F)
                  {
                    k = this.videoTextureView.getMeasuredWidth();
                    j = this.videoTextureView.getMeasuredHeight();
                  }
                }
              }
              f7 = getContainerViewWidth() / k;
              f8 = getContainerViewHeight() / j;
              if (f7 <= f8)
                break label1899;
              f7 = f8;
              k = (int)(k * f7);
              j = (int)(j * f7);
              if ((i == 0) || (!this.textureUploaded) || (!this.videoCrossfadeStarted) || (this.videoCrossfadeAlpha != 1.0F))
              {
                this.centerImage.setAlpha(f2);
                this.centerImage.setImageCoords(-k / 2, -j / 2, k, j);
                this.centerImage.draw(paramCanvas);
              }
              if (i != 0)
              {
                if ((!this.videoCrossfadeStarted) && (this.textureUploaded))
                {
                  this.videoCrossfadeStarted = true;
                  this.videoCrossfadeAlpha = 0.0F;
                  this.videoCrossfadeAlphaLastTime = System.currentTimeMillis();
                }
                paramCanvas.translate(-k / 2, -j / 2);
                this.videoTextureView.setAlpha(this.videoCrossfadeAlpha * f2);
                this.aspectRatioFrameLayout.draw(paramCanvas);
                if ((this.videoCrossfadeStarted) && (this.videoCrossfadeAlpha < 1.0F))
                {
                  long l1 = System.currentTimeMillis();
                  long l2 = this.videoCrossfadeAlphaLastTime;
                  this.videoCrossfadeAlphaLastTime = l1;
                  this.videoCrossfadeAlpha += (float)(l1 - l2) / 300.0F;
                  this.containerView.invalidate();
                  if (this.videoCrossfadeAlpha > 1.0F)
                    this.videoCrossfadeAlpha = 1.0F;
                }
              }
              paramCanvas.restore();
            }
            label994: if ((i == 0) && ((this.videoPlayerControlFrameLayout == null) || (this.videoPlayerControlFrameLayout.getVisibility() != 0)))
            {
              paramCanvas.save();
              paramCanvas.translate(f6, f3 / f4);
              this.photoProgressViews[0].setScale(1.0F - f5);
              this.photoProgressViews[0].setAlpha(f2);
              this.photoProgressViews[0].onDraw(paramCanvas);
              paramCanvas.restore();
            }
            if (localImageReceiver != this.leftImage)
              break;
            if (localImageReceiver.hasBitmapImage())
            {
              paramCanvas.save();
              paramCanvas.translate(getContainerViewWidth() / 2, getContainerViewHeight() / 2);
              paramCanvas.translate(-(paramCanvas.getWidth() * (this.scale + 1.0F) + AndroidUtilities.dp(30.0F)) / 2.0F + f1, 0.0F);
              j = localImageReceiver.getBitmapWidth();
              i = localImageReceiver.getBitmapHeight();
              f2 = getContainerViewWidth() / j;
              f5 = getContainerViewHeight() / i;
              if (f2 <= f5)
                break label1902;
              f2 = f5;
            }
          }
          label1823: label1845: label1858: label1899: label1902: 
          while (true)
          {
            j = (int)(j * f2);
            i = (int)(f2 * i);
            localImageReceiver.setAlpha(1.0F);
            localImageReceiver.setImageCoords(-j / 2, -i / 2, j, i);
            localImageReceiver.draw(paramCanvas);
            paramCanvas.restore();
            paramCanvas.save();
            paramCanvas.translate(f1, f3 / f4);
            paramCanvas.translate(-(paramCanvas.getWidth() * (this.scale + 1.0F) + AndroidUtilities.dp(30.0F)) / 2.0F, -f3 / f4);
            this.photoProgressViews[2].setScale(1.0F);
            this.photoProgressViews[2].setAlpha(1.0F);
            this.photoProgressViews[2].onDraw(paramCanvas);
            paramCanvas.restore();
            return;
            if (this.animationStartTime != 0L)
            {
              this.translationX = this.animateToX;
              this.translationY = this.animateToY;
              this.scale = this.animateToScale;
              this.animationStartTime = 0L;
              if (this.currentEditMode == 1)
                this.photoCropView.setAnimationProgress(1.0F);
              updateMinMax(this.scale);
              this.zoomAnimation = false;
            }
            if ((!this.scroller.isFinished()) && (this.scroller.computeScrollOffset()))
            {
              if ((this.scroller.getStartX() < this.maxX) && (this.scroller.getStartX() > this.minX))
                this.translationX = this.scroller.getCurrX();
              if ((this.scroller.getStartY() < this.maxY) && (this.scroller.getStartY() > this.minY))
                this.translationY = this.scroller.getCurrY();
              this.containerView.invalidate();
            }
            if (this.switchImageAfterAnimation != 0)
            {
              if (this.switchImageAfterAnimation != 1)
                break label1823;
              setImageIndex(this.currentIndex + 1, false);
            }
            while (true)
            {
              this.switchImageAfterAnimation = 0;
              f5 = this.scale;
              f6 = this.translationY;
              f7 = this.translationX;
              f2 = f1;
              f4 = f5;
              f1 = f7;
              f3 = f6;
              if (this.moving)
                break;
              f2 = this.translationY;
              f4 = f5;
              f1 = f7;
              f3 = f6;
              break;
              if (this.switchImageAfterAnimation != 2)
                continue;
              setImageIndex(this.currentIndex - 1, false);
            }
            this.backgroundDrawable.setAlpha(255);
            break label282;
            if (f1 >= this.minX - AndroidUtilities.dp(5.0F))
              break label1917;
            localImageReceiver = this.rightImage;
            break label338;
            bool = false;
            break label346;
            break label538;
            i = 0;
            break label767;
            break label994;
          }
          label1905: f6 = f1;
        }
        label1911: f6 = f1;
      }
      label1917: localImageReceiver = null;
    }
  }

  private void onPhotoClosed(PlaceProviderObject paramPlaceProviderObject)
  {
    this.isVisible = false;
    this.disableShowCheck = true;
    this.currentMessageObject = null;
    this.currentBotInlineResult = null;
    this.currentFileLocation = null;
    this.currentPathObject = null;
    this.currentThumb = null;
    this.parentAlert = null;
    if (this.currentAnimation != null)
    {
      this.currentAnimation.setSecondParentView(null);
      this.currentAnimation = null;
    }
    int i = 0;
    while (i < 3)
    {
      if (this.photoProgressViews[i] != null)
        this.photoProgressViews[i].setBackgroundState(-1, false);
      i += 1;
    }
    requestVideoPreview(0);
    if (this.videoTimelineView != null)
      this.videoTimelineView.destroy();
    this.centerImage.setImageBitmap((Bitmap)null);
    this.leftImage.setImageBitmap((Bitmap)null);
    this.rightImage.setImageBitmap((Bitmap)null);
    this.containerView.post(new Runnable()
    {
      public void run()
      {
        PhotoViewer.access$10402(PhotoViewer.this, false);
        PhotoViewer.this.parentActivity.setRequestedOrientation(2);
        if ((PhotoViewer.this.containerView != null) && (PhotoViewer.this.videoUrl != null))
          PhotoViewer.this.containerView.removeView(PhotoViewer.this.videoUrl);
        PhotoViewer.this.animatingImageView.setImageBitmap(null);
        try
        {
          if (PhotoViewer.this.windowView.getParent() != null)
            ((WindowManager)PhotoViewer.this.parentActivity.getSystemService("window")).removeView(PhotoViewer.this.windowView);
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
    if (this.placeProvider != null)
      this.placeProvider.willHidePhotoViewer();
    this.placeProvider = null;
    this.disableShowCheck = false;
    if (paramPlaceProviderObject != null)
      paramPlaceProviderObject.imageReceiver.setVisible(true, true);
  }

  private void onPhotoShow(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, ArrayList<MessageObject> paramArrayList, ArrayList<Object> paramArrayList1, int paramInt, PlaceProviderObject paramPlaceProviderObject)
  {
    this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
    this.currentMessageObject = null;
    this.currentFileLocation = null;
    this.currentPathObject = null;
    this.currentBotInlineResult = null;
    this.currentIndex = -1;
    this.currentFileNames[0] = null;
    this.currentFileNames[1] = null;
    this.currentFileNames[2] = null;
    this.avatarsDialogId = 0;
    this.totalImagesCount = 0;
    this.totalImagesCountMerge = 0;
    this.currentEditMode = 0;
    this.isFirstLoading = true;
    this.needSearchImageInArr = false;
    this.loadingMoreImages = false;
    this.endReached[0] = false;
    Object localObject = this.endReached;
    if (this.mergeDialogId == 0L);
    int i;
    for (boolean bool = true; ; bool = false)
    {
      localObject[1] = bool;
      this.opennedFromMedia = false;
      this.needCaptionLayout = false;
      this.canShowBottom = true;
      this.imagesArr.clear();
      this.imagesArrLocations.clear();
      this.imagesArrLocationsSizes.clear();
      this.avatarsArr.clear();
      this.imagesArrLocals.clear();
      i = 0;
      while (i < 2)
      {
        this.imagesByIds[i].clear();
        this.imagesByIdsTemp[i].clear();
        i += 1;
      }
    }
    this.imagesArrTemp.clear();
    this.currentUserAvatarLocation = null;
    this.containerView.setPadding(0, 0, 0, 0);
    if (paramPlaceProviderObject != null);
    for (localObject = paramPlaceProviderObject.thumb; ; localObject = null)
    {
      this.currentThumb = ((Bitmap)localObject);
      this.menuItem.setVisibility(0);
      this.bottomLayout.setVisibility(0);
      this.bottomLayout.setTranslationY(0.0F);
      this.captionTextViewOld.setTranslationY(0.0F);
      this.captionTextViewNew.setTranslationY(0.0F);
      this.bottomLayout.setTranslationY(0.0F);
      this.shareButton.setVisibility(8);
      if (this.qualityChooseView != null)
      {
        this.qualityChooseView.setVisibility(4);
        this.qualityPicker.setVisibility(4);
        this.qualityChooseView.setTag(null);
      }
      if (this.qualityChooseViewAnimation != null)
      {
        this.qualityChooseViewAnimation.cancel();
        this.qualityChooseViewAnimation = null;
      }
      this.allowShare = false;
      this.menuItem.hideSubItem(2);
      this.menuItem.hideSubItem(10);
      this.menuItem.hideSubItem(11);
      this.actionBar.setTranslationY(0.0F);
      this.pickerView.setTranslationY(0.0F);
      this.checkImageView.setAlpha(1.0F);
      this.pickerView.setAlpha(1.0F);
      this.checkImageView.setVisibility(8);
      this.pickerView.setVisibility(8);
      this.paintItem.setVisibility(8);
      this.cropItem.setVisibility(8);
      this.tuneItem.setVisibility(8);
      if (this.videoTimelineViewContainer != null)
        this.videoTimelineViewContainer.setVisibility(8);
      this.captionItem.setVisibility(8);
      this.captionItem.setImageResource(2130838007);
      this.compressItem.setVisibility(8);
      this.captionEditText.setVisibility(8);
      this.mentionListView.setVisibility(8);
      this.muteItem.setVisibility(8);
      this.actionBar.setSubtitle(null);
      this.masksItem.setVisibility(8);
      this.muteVideo = false;
      this.muteItem.setImageResource(2130838123);
      this.editorDoneLayout.setVisibility(8);
      this.captionTextView.setTag(null);
      this.captionTextView.setVisibility(4);
      if (this.photoCropView != null)
        this.photoCropView.setVisibility(8);
      if (this.photoFilterView != null)
        this.photoFilterView.setVisibility(8);
      i = 0;
      while (i < 3)
      {
        if (this.photoProgressViews[i] != null)
          this.photoProgressViews[i].setBackgroundState(-1, false);
        i += 1;
      }
    }
    if ((paramMessageObject != null) && (paramArrayList == null))
    {
      this.imagesArr.add(paramMessageObject);
      if (this.currentAnimation != null)
        this.needSearchImageInArr = false;
    }
    label717: label995: label1767: 
    do
    {
      do
      {
        do
        {
          break label833;
          break label833;
          break label833;
          setImageIndex(0, true);
          i = paramInt;
          if (this.currentAnimation == null)
          {
            if ((this.currentDialogId == 0L) || (this.totalImagesCount != 0))
              break label1738;
            SharedMediaQuery.getMediaCount(this.currentDialogId, 0, this.classGuid, true);
            if (this.mergeDialogId != 0L)
              SharedMediaQuery.getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
          }
          while (true)
          {
            if (((this.currentMessageObject == null) || (!this.currentMessageObject.isVideo())) && ((this.currentBotInlineResult == null) || ((!this.currentBotInlineResult.type.equals("video")) && (!MessageObject.isVideoDocument(this.currentBotInlineResult.document)))))
              break label1767;
            onActionClick(false);
            return;
            if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) || ((paramMessageObject.messageOwner.action != null) && (!(paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty))))
              break;
            this.needSearchImageInArr = true;
            this.imagesByIds[0].put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
            this.menuItem.showSubItem(2);
            break;
            if (paramFileLocation != null)
            {
              this.avatarsDialogId = paramPlaceProviderObject.dialogId;
              this.imagesArrLocations.add(paramFileLocation);
              this.imagesArrLocationsSizes.add(Integer.valueOf(paramPlaceProviderObject.size));
              this.avatarsArr.add(new TLRPC.TL_photoEmpty());
              paramMessageObject = this.shareButton;
              if ((this.videoPlayerControlFrameLayout == null) || (this.videoPlayerControlFrameLayout.getVisibility() != 0))
              {
                i = 0;
                paramMessageObject.setVisibility(i);
                this.allowShare = true;
                this.menuItem.hideSubItem(2);
                if (this.shareButton.getVisibility() != 0)
                  break label1058;
                this.menuItem.hideSubItem(10);
              }
              while (true)
              {
                setImageIndex(0, true);
                this.currentUserAvatarLocation = paramFileLocation;
                i = paramInt;
                break;
                i = 8;
                break label995;
                this.menuItem.showSubItem(10);
              }
            }
            if (paramArrayList != null)
            {
              this.menuItem.showSubItem(2);
              this.opennedFromMedia = true;
              this.imagesArr.addAll(paramArrayList);
              i = paramInt;
              if (!this.opennedFromMedia)
              {
                Collections.reverse(this.imagesArr);
                i = this.imagesArr.size() - paramInt - 1;
              }
              paramInt = 0;
              if (paramInt < this.imagesArr.size())
              {
                paramMessageObject = (MessageObject)this.imagesArr.get(paramInt);
                paramFileLocation = this.imagesByIds;
                if (paramMessageObject.getDialogId() == this.currentDialogId);
                for (int j = 0; ; j = 1)
                {
                  paramFileLocation[j].put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
                  paramInt += 1;
                  break;
                }
              }
              setImageIndex(i, true);
              break label717;
            }
            i = paramInt;
            if (paramArrayList1 == null)
              break label717;
            if (this.sendPhotoType == 0)
              this.checkImageView.setVisibility(0);
            this.menuItem.setVisibility(8);
            this.imagesArrLocals.addAll(paramArrayList1);
            setImageIndex(paramInt, true);
            this.pickerView.setVisibility(0);
            this.bottomLayout.setVisibility(8);
            this.canShowBottom = false;
            paramMessageObject = this.imagesArrLocals.get(paramInt);
            if ((paramMessageObject instanceof MediaController.PhotoEntry))
              if (((MediaController.PhotoEntry)paramMessageObject).isVideo)
              {
                this.cropItem.setVisibility(8);
                this.bottomLayout.setVisibility(0);
                this.bottomLayout.setTranslationY(-AndroidUtilities.dp(48.0F));
                i = 1;
                if ((this.parentChatActivity != null) && ((this.parentChatActivity.currentEncryptedChat == null) || (AndroidUtilities.getPeerLayerVersion(this.parentChatActivity.currentEncryptedChat.layer) >= 46)))
                {
                  this.mentionsAdapter.setChatInfo(this.parentChatActivity.info);
                  paramMessageObject = this.mentionsAdapter;
                  if (this.parentChatActivity.currentChat == null)
                    break label1708;
                  bool = true;
                  paramMessageObject.setNeedUsernames(bool);
                  this.mentionsAdapter.setNeedBotContext(false);
                  if ((i == 0) || ((this.placeProvider != null) && ((this.placeProvider == null) || (!this.placeProvider.allowCaption()))))
                    break label1714;
                  bool = true;
                  this.needCaptionLayout = bool;
                  paramMessageObject = this.captionEditText;
                  if (!this.needCaptionLayout)
                    break label1720;
                  i = 0;
                  paramMessageObject.setVisibility(i);
                  if ((this.captionTextView.getTag() != null) || (!this.needCaptionLayout))
                    break label1727;
                  this.captionTextView.setText(LocaleController.getString("AddCaption", 2131165274));
                  this.captionTextView.setTag("empty");
                  this.captionTextView.setTextColor(-1291845633);
                  this.captionTextView.setVisibility(0);
                }
              }
            while (true)
            {
              if (this.needCaptionLayout)
                this.captionEditText.onCreate();
              if (Build.VERSION.SDK_INT >= 16)
              {
                this.paintItem.setVisibility(this.cropItem.getVisibility());
                this.tuneItem.setVisibility(this.cropItem.getVisibility());
              }
              updateSelectedCount();
              i = paramInt;
              break;
              this.cropItem.setVisibility(0);
              break label1348;
              if ((paramMessageObject instanceof TLRPC.BotInlineResult))
              {
                this.cropItem.setVisibility(8);
                i = 0;
                break label1351;
              }
              paramFileLocation = this.cropItem;
              if (((paramMessageObject instanceof MediaController.SearchImage)) && (((MediaController.SearchImage)paramMessageObject).type == 0));
              for (i = 0; ; i = 8)
              {
                paramFileLocation.setVisibility(i);
                if (this.cropItem.getVisibility() != 0)
                  break label1702;
                i = 1;
                break;
              }
              i = 0;
              break label1351;
              bool = false;
              break label1418;
              bool = false;
              break label1466;
              i = 8;
              break label1487;
              this.captionTextView.setTextColor(-1);
            }
            if (this.avatarsDialogId == 0)
              continue;
            MessagesController.getInstance().loadDialogPhotos(this.avatarsDialogId, 0, 80, 0L, true, this.classGuid);
          }
        }
        while (this.imagesArrLocals.isEmpty());
        paramMessageObject = this.imagesArrLocals.get(i);
      }
      while (!(paramMessageObject instanceof MediaController.PhotoEntry));
      paramMessageObject = (MediaController.PhotoEntry)paramMessageObject;
    }
    while (!paramMessageObject.isVideo);
    label833: label1348: label1351: label1487: paramFileLocation = e.a(ApplicationLoader.applicationContext).c();
    label1058: label1466: label1727: label1738: preparePlayer(new File(paramMessageObject.path), false, false, paramFileLocation);
    label1418: label1702: label1708: label1714: label1720: return;
  }

  private void onSharePressed()
  {
    boolean bool2 = false;
    Object localObject3 = null;
    Object localObject1 = null;
    if ((this.parentActivity == null) || (!this.allowShare))
      return;
    label109: 
    do
    {
      try
      {
        if (this.currentMessageObject != null)
        {
          bool2 = this.currentMessageObject.isVideo();
          localObject3 = localObject1;
          if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath))
          {
            localObject3 = new File(this.currentMessageObject.messageOwner.attachPath);
            if (((File)localObject3).exists())
              break label318;
            localObject3 = localObject1;
          }
          bool1 = bool2;
          localObject1 = localObject3;
          if (localObject3 == null)
          {
            localObject1 = FileLoader.getPathToMessage(this.currentMessageObject.messageOwner);
            bool1 = bool2;
          }
          if (!((File)localObject1).exists())
            break label257;
          localObject3 = new Intent("android.intent.action.SEND");
          if (!bool1)
            break;
          ((Intent)localObject3).setType("video/mp4");
          ((Intent)localObject3).putExtra("android.intent.extra.STREAM", Uri.fromFile((File)localObject1));
          this.parentActivity.startActivityForResult(Intent.createChooser((Intent)localObject3, LocaleController.getString("ShareFile", 2131166451)), 500);
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        return;
      }
      bool1 = bool2;
      localObject2 = localObject3;
    }
    while (this.currentFileLocation == null);
    label141: Object localObject2 = this.currentFileLocation;
    if (this.avatarsDialogId != 0);
    for (boolean bool1 = true; ; bool1 = false)
    {
      localObject2 = FileLoader.getPathToAttach((TLObject)localObject2, bool1);
      bool1 = bool2;
      break label109;
      if (this.currentMessageObject != null)
      {
        ((Intent)localObject3).setType(this.currentMessageObject.getMimeType());
        break label141;
      }
      ((Intent)localObject3).setType("image/jpeg");
      break label141;
      label257: localObject2 = new AlertDialog.Builder(this.parentActivity);
      ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("AppName", 2131165319));
      ((AlertDialog.Builder)localObject2).setPositiveButton(LocaleController.getString("OK", 2131166153), null);
      ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("PleaseDownload", 2131166288));
      showAlertDialog((AlertDialog.Builder)localObject2);
      return;
      label318: break;
    }
  }

  private boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f5 = 0.0F;
    float f4 = 0.0F;
    if ((this.animationInProgress != 0) || (this.animationStartTime != 0L))
      return false;
    if (this.currentEditMode == 2)
    {
      this.photoFilterView.onTouch(paramMotionEvent);
      return true;
    }
    if (this.currentEditMode == 1)
      return true;
    if ((this.captionEditText.isPopupShowing()) || (this.captionEditText.isKeyboardVisible()))
    {
      if (paramMotionEvent.getAction() == 1)
        closeCaptionEnter(true);
      return true;
    }
    if ((this.currentEditMode == 0) && (paramMotionEvent.getPointerCount() == 1) && (this.gestureDetector.onTouchEvent(paramMotionEvent)) && (this.doubleTap))
    {
      this.doubleTap = false;
      this.moving = false;
      this.zooming = false;
      checkMinMax(false);
      return true;
    }
    if ((paramMotionEvent.getActionMasked() == 0) || (paramMotionEvent.getActionMasked() == 5))
    {
      if (this.currentEditMode == 1)
        this.photoCropView.cancelAnimationRunnable();
      this.discardTap = false;
      if (!this.scroller.isFinished())
        this.scroller.abortAnimation();
      if ((!this.draggingDown) && (!this.changingPage))
      {
        if ((!this.canZoom) || (paramMotionEvent.getPointerCount() != 2))
          break label339;
        this.pinchStartDistance = (float)Math.hypot(paramMotionEvent.getX(1) - paramMotionEvent.getX(0), paramMotionEvent.getY(1) - paramMotionEvent.getY(0));
        this.pinchStartScale = this.scale;
        this.pinchCenterX = ((paramMotionEvent.getX(0) + paramMotionEvent.getX(1)) / 2.0F);
        this.pinchCenterY = ((paramMotionEvent.getY(0) + paramMotionEvent.getY(1)) / 2.0F);
        this.pinchStartX = this.translationX;
        this.pinchStartY = this.translationY;
        this.zooming = true;
        this.moving = false;
        if (this.velocityTracker != null)
          this.velocityTracker.clear();
      }
    }
    label339: float f1;
    float f2;
    float f3;
    while (true)
    {
      return false;
      if (paramMotionEvent.getPointerCount() != 1)
        continue;
      this.moveStartX = paramMotionEvent.getX();
      f1 = paramMotionEvent.getY();
      this.moveStartY = f1;
      this.dragY = f1;
      this.draggingDown = false;
      this.canDragDown = true;
      if (this.velocityTracker == null)
        continue;
      this.velocityTracker.clear();
      continue;
      if (paramMotionEvent.getActionMasked() != 2)
        break label1227;
      if (this.currentEditMode == 1)
        this.photoCropView.cancelAnimationRunnable();
      if ((this.canZoom) && (paramMotionEvent.getPointerCount() == 2) && (!this.draggingDown) && (this.zooming) && (!this.changingPage))
      {
        this.discardTap = true;
        this.scale = ((float)Math.hypot(paramMotionEvent.getX(1) - paramMotionEvent.getX(0), paramMotionEvent.getY(1) - paramMotionEvent.getY(0)) / this.pinchStartDistance * this.pinchStartScale);
        this.translationX = (this.pinchCenterX - getContainerViewWidth() / 2 - (this.pinchCenterX - getContainerViewWidth() / 2 - this.pinchStartX) * (this.scale / this.pinchStartScale));
        this.translationY = (this.pinchCenterY - getContainerViewHeight() / 2 - (this.pinchCenterY - getContainerViewHeight() / 2 - this.pinchStartY) * (this.scale / this.pinchStartScale));
        updateMinMax(this.scale);
        this.containerView.invalidate();
        continue;
      }
      if (paramMotionEvent.getPointerCount() != 1)
        continue;
      if (this.velocityTracker != null)
        this.velocityTracker.addMovement(paramMotionEvent);
      f1 = Math.abs(paramMotionEvent.getX() - this.moveStartX);
      f2 = Math.abs(paramMotionEvent.getY() - this.dragY);
      if ((f1 > AndroidUtilities.dp(3.0F)) || (f2 > AndroidUtilities.dp(3.0F)))
        this.discardTap = true;
      if ((!(this.placeProvider instanceof EmptyPhotoViewerProvider)) && (this.currentEditMode == 0) && (this.canDragDown) && (!this.draggingDown) && (this.scale == 1.0F) && (f2 >= AndroidUtilities.dp(30.0F)) && (f2 / 2.0F > f1))
      {
        this.draggingDown = true;
        this.moving = false;
        this.dragY = paramMotionEvent.getY();
        if ((this.isActionBarVisible) && (this.canShowBottom))
          toggleActionBar(false, true);
        while (true)
        {
          return true;
          if (this.pickerView.getVisibility() != 0)
            continue;
          toggleActionBar(false, true);
          toggleCheckImageView(false);
        }
      }
      if (this.draggingDown)
      {
        this.translationY = (paramMotionEvent.getY() - this.dragY);
        this.containerView.invalidate();
        continue;
      }
      if ((!this.invalidCoords) && (this.animationStartTime == 0L))
      {
        f2 = this.moveStartX - paramMotionEvent.getX();
        f1 = this.moveStartY - paramMotionEvent.getY();
        if ((!this.moving) && (this.currentEditMode == 0) && ((this.scale != 1.0F) || (Math.abs(f1) + AndroidUtilities.dp(12.0F) >= Math.abs(f2))) && (this.scale == 1.0F))
          continue;
        if (!this.moving)
        {
          this.moving = true;
          this.canDragDown = false;
          f1 = 0.0F;
          f2 = 0.0F;
        }
        this.moveStartX = paramMotionEvent.getX();
        this.moveStartY = paramMotionEvent.getY();
        updateMinMax(this.scale);
        if ((this.translationX >= this.minX) || ((this.currentEditMode == 0) && (this.rightImage.hasImage())))
        {
          f3 = f2;
          if (this.translationX <= this.maxX)
            break label1045;
          if (this.currentEditMode == 0)
          {
            f3 = f2;
            if (this.leftImage.hasImage())
              break label1045;
          }
        }
        f3 = f2 / 3.0F;
        label1045: if ((this.maxY == 0.0F) && (this.minY == 0.0F) && (this.currentEditMode == 0))
        {
          if (this.translationY - f1 >= this.minY)
            break;
          this.translationY = this.minY;
          f1 = f4;
        }
      }
    }
    while (true)
    {
      this.translationX -= f3;
      if ((this.scale != 1.0F) || (this.currentEditMode != 0))
        this.translationY -= f1;
      this.containerView.invalidate();
      break;
      if (this.translationY - f1 > this.maxY)
      {
        this.translationY = this.maxY;
        f1 = f4;
        continue;
        if ((this.translationY < this.minY) || (this.translationY > this.maxY))
        {
          f1 /= 3.0F;
          continue;
          this.invalidCoords = false;
          this.moveStartX = paramMotionEvent.getX();
          this.moveStartY = paramMotionEvent.getY();
          break;
          label1227: if ((paramMotionEvent.getActionMasked() != 3) && (paramMotionEvent.getActionMasked() != 1) && (paramMotionEvent.getActionMasked() != 6))
            break;
          if (this.currentEditMode == 1)
            this.photoCropView.startAnimationRunnable();
          if (this.zooming)
          {
            this.invalidCoords = true;
            if (this.scale < 1.0F)
            {
              updateMinMax(1.0F);
              animateTo(1.0F, 0.0F, 0.0F, true);
            }
            while (true)
            {
              this.zooming = false;
              break;
              if (this.scale > 3.0F)
              {
                f2 = this.pinchCenterX - getContainerViewWidth() / 2 - (this.pinchCenterX - getContainerViewWidth() / 2 - this.pinchStartX) * (3.0F / this.pinchStartScale);
                f3 = this.pinchCenterY - getContainerViewHeight() / 2 - (this.pinchCenterY - getContainerViewHeight() / 2 - this.pinchStartY) * (3.0F / this.pinchStartScale);
                updateMinMax(3.0F);
                if (f2 < this.minX)
                {
                  f1 = this.minX;
                  label1422: if (f3 >= this.minY)
                    break label1469;
                  f2 = this.minY;
                }
                while (true)
                {
                  animateTo(3.0F, f1, f2, true);
                  break;
                  f1 = f2;
                  if (f2 <= this.maxX)
                    break label1422;
                  f1 = this.maxX;
                  break label1422;
                  label1469: f2 = f3;
                  if (f3 <= this.maxY)
                    continue;
                  f2 = this.maxY;
                }
              }
              checkMinMax(true);
            }
          }
          if (this.draggingDown)
          {
            if (Math.abs(this.dragY - paramMotionEvent.getY()) > getContainerViewHeight() / 6.0F)
              closePhoto(true, false);
            while (true)
            {
              this.draggingDown = false;
              break;
              if (this.pickerView.getVisibility() == 0)
              {
                toggleActionBar(true, true);
                toggleCheckImageView(true);
              }
              animateTo(1.0F, 0.0F, 0.0F, false);
            }
          }
          if (!this.moving)
            break;
          f3 = this.translationX;
          f2 = this.translationY;
          updateMinMax(this.scale);
          this.moving = false;
          this.canDragDown = true;
          f1 = f5;
          if (this.velocityTracker != null)
          {
            f1 = f5;
            if (this.scale == 1.0F)
            {
              this.velocityTracker.computeCurrentVelocity(1000);
              f1 = this.velocityTracker.getXVelocity();
            }
          }
          if (this.currentEditMode == 0)
          {
            if (((this.translationX < this.minX - getContainerViewWidth() / 3) || (f1 < -AndroidUtilities.dp(650.0F))) && (this.rightImage.hasImage()))
            {
              goToNext();
              return true;
            }
            if (((this.translationX > this.maxX + getContainerViewWidth() / 3) || (f1 > AndroidUtilities.dp(650.0F))) && (this.leftImage.hasImage()))
            {
              goToPrev();
              return true;
            }
          }
          if (this.translationX < this.minX)
          {
            f1 = this.minX;
            label1773: if (this.translationY >= this.minY)
              break label1827;
            f2 = this.minY;
          }
          while (true)
          {
            animateTo(this.scale, f1, f2, false);
            break;
            f1 = f3;
            if (this.translationX <= this.maxX)
              break label1773;
            f1 = this.maxX;
            break label1773;
            label1827: if (this.translationY > this.maxY)
            {
              f2 = this.maxY;
              continue;
            }
          }
        }
      }
    }
  }

  private void openCaptionEnter()
  {
    if ((this.imageMoveAnimation != null) || (this.changeModeAnimation != null) || (this.currentEditMode != 0))
      return;
    this.captionEditText.setTag(Integer.valueOf(1));
    this.captionEditText.openKeyboard();
    this.lastTitle = this.actionBar.getTitle();
    if (this.captionItem.getVisibility() == 0)
    {
      ActionBar localActionBar = this.actionBar;
      if (this.muteVideo);
      for (String str = LocaleController.getString("GifCaption", 2131165792); ; str = LocaleController.getString("VideoCaption", 2131166571))
      {
        localActionBar.setTitle(str);
        this.actionBar.setSubtitle(null);
        return;
      }
    }
    this.actionBar.setTitle(LocaleController.getString("PhotoCaption", 2131166272));
  }

  @SuppressLint({"NewApi"})
  private void preparePlayer(File paramFile, boolean paramBoolean1, boolean paramBoolean2, n paramn)
  {
    if (this.parentActivity == null)
      return;
    if (!paramBoolean2)
      this.currentPlayingVideoFile = paramFile;
    this.inPreview = paramBoolean2;
    releasePlayer();
    if (this.videoTextureView == null)
    {
      this.aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity);
      this.aspectRatioFrameLayout.setVisibility(4);
      this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
      this.videoTextureView = new TextureView(this.parentActivity);
      this.videoTextureView.setOpaque(false);
      this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
    }
    this.textureUploaded = false;
    this.videoCrossfadeStarted = false;
    Object localObject = this.videoTextureView;
    this.videoCrossfadeAlpha = 0.0F;
    ((TextureView)localObject).setAlpha(0.0F);
    this.videoPlayButton.setImageResource(2130837872);
    long l1;
    if (this.videoPlayer == null)
    {
      this.videoPlayer = new VideoPlayer();
      this.videoPlayer.setTextureView(this.videoTextureView);
      this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate(paramn, paramFile, paramBoolean2)
      {
        public void onError(Exception paramException)
        {
          FileLog.e(paramException);
        }

        public void onRenderedFirstFrame()
        {
          if (!PhotoViewer.this.textureUploaded)
          {
            PhotoViewer.access$10702(PhotoViewer.this, true);
            PhotoViewer.this.containerView.invalidate();
          }
        }

        public void onStateChanged(boolean paramBoolean, int paramInt)
        {
          if (PhotoViewer.this.videoPlayer == null)
            return;
          if ((paramInt != 4) && (paramInt != 1));
          label296: 
          do
          {
            do
              while (true)
              {
                try
                {
                  PhotoViewer.this.parentActivity.getWindow().addFlags(128);
                  if ((paramInt != 3) || (PhotoViewer.this.aspectRatioFrameLayout.getVisibility() == 0))
                    continue;
                  PhotoViewer.this.aspectRatioFrameLayout.setVisibility(0);
                  if ((!PhotoViewer.this.videoPlayer.isPlaying()) || (paramInt == 4))
                    break label296;
                  if (PhotoViewer.this.isPlaying)
                    continue;
                  PhotoViewer.access$702(PhotoViewer.this, true);
                  PhotoViewer.this.videoPlayButton.setImageResource(2130837871);
                  AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable);
                  PhotoViewer.this.updateVideoPlayerTime();
                  if (this.val$pVideo.a() == null)
                    break;
                  PhotoViewer.this.captionTextView.setVisibility(8);
                  if ((paramBoolean) || (paramInt != 4))
                    break;
                  this.val$pVideo.a(null);
                  PhotoViewer.this.preparePlayer(this.val$file, true, this.val$preview, this.val$pVideo);
                  PhotoViewer.access$10402(PhotoViewer.this, false);
                  PhotoViewer.this.actionBar.setVisibility(0);
                  PhotoViewer.this.bottomLayout.setVisibility(0);
                  PhotoViewer.this.parentActivity.setRequestedOrientation(2);
                  if (PhotoViewer.this.videoUrl == null)
                    break;
                  PhotoViewer.this.containerView.removeView(PhotoViewer.this.videoUrl);
                  return;
                }
                catch (Exception localException1)
                {
                  FileLog.e(localException1);
                  continue;
                }
                try
                {
                  PhotoViewer.this.parentActivity.getWindow().clearFlags(128);
                }
                catch (Exception localException2)
                {
                  FileLog.e(localException2);
                }
              }
            while (!PhotoViewer.this.isPlaying);
            PhotoViewer.access$702(PhotoViewer.this, false);
            PhotoViewer.this.videoPlayButton.setImageResource(2130837872);
            AndroidUtilities.cancelRunOnUIThread(PhotoViewer.this.updateProgressRunnable);
          }
          while ((paramInt != 4) || (PhotoViewer.this.videoPlayerSeekbar.isDragging()));
          PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0F);
          PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
          if ((!PhotoViewer.this.inPreview) && (PhotoViewer.this.videoTimelineViewContainer != null) && (PhotoViewer.this.videoTimelineViewContainer.getVisibility() == 0))
            PhotoViewer.this.videoPlayer.seekTo((int)(PhotoViewer.this.videoTimelineView.getLeftProgress() * (float)PhotoViewer.this.videoPlayer.getDuration()));
          while (true)
          {
            PhotoViewer.this.videoPlayer.pause();
            break;
            PhotoViewer.this.videoPlayer.seekTo(0L);
          }
        }

        public boolean onSurfaceDestroyed(SurfaceTexture paramSurfaceTexture)
        {
          return false;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture)
        {
        }

        public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
        {
          int j;
          int i;
          AspectRatioFrameLayout localAspectRatioFrameLayout;
          if (PhotoViewer.this.aspectRatioFrameLayout != null)
          {
            if (paramInt3 != 90)
            {
              j = paramInt1;
              i = paramInt2;
              if (paramInt3 != 270);
            }
            else
            {
              i = paramInt1;
              j = paramInt2;
            }
            localAspectRatioFrameLayout = PhotoViewer.this.aspectRatioFrameLayout;
            if (i != 0)
              break label61;
            paramFloat = 1.0F;
          }
          while (true)
          {
            localAspectRatioFrameLayout.setAspectRatio(paramFloat, paramInt3);
            return;
            label61: paramFloat = j * paramFloat / i;
          }
        }
      });
      if (this.videoPlayer == null)
        break label701;
      long l2 = this.videoPlayer.getDuration();
      l1 = l2;
      if (l2 == -9223372036854775807L)
        l1 = 0L;
    }
    while (true)
    {
      l1 /= 1000L;
      int i = (int)Math.ceil(this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[] { Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L), Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L) })));
      if (paramn.a() != null)
      {
        this.videoUrl = new TextView(this.containerView.getContext());
        this.videoUrl.setBackgroundColor(0);
        this.videoUrl.setOnClickListener(new View.OnClickListener(paramn)
        {
          public void onClick(View paramView)
          {
            PhotoViewer.this.closePhoto(true, false);
            e.a(ApplicationLoader.applicationContext).a(this.val$pVideo.b());
            Browser.openUrl(PhotoViewer.this.parentActivity, this.val$pVideo.b().a());
          }
        });
      }
      try
      {
        localObject = (WindowManager)this.containerView.getContext().getSystemService("window");
        paramFile = new DisplayMetrics();
        ((WindowManager)localObject).getDefaultDisplay().getMetrics(paramFile);
        localObject = this.containerView.getContext().getResources();
        float f1 = TypedValue.applyDimension(1, 2.0F, ((Resources)localObject).getDisplayMetrics());
        f1 = paramFile.widthPixels / f1;
        float f2 = TypedValue.applyDimension(1, 8.0F, ((Resources)localObject).getDisplayMetrics());
        f2 = paramFile.heightPixels / f2;
        this.containerView.addView(this.videoUrl, LayoutHelper.createFrame((int)f1, f2, 83, 0.0F, 0.0F, 0.0F, f2));
        label488: com.b.a.a.a.a().a(new k("AdvertisingVideo Viewed"));
        this.playAdvertising = true;
        this.videoPlayer.preparePlayer(Uri.fromFile(paramn.a()), "other");
        this.actionBar.setVisibility(8);
        this.bottomLayout.setVisibility(8);
        this.parentActivity.setRequestedOrientation(1);
        label553: if (this.videoPlayerControlFrameLayout != null)
        {
          if ((this.currentBotInlineResult != null) && ((this.currentBotInlineResult.type.equals("video")) || (MessageObject.isVideoDocument(this.currentBotInlineResult.document))))
          {
            this.bottomLayout.setVisibility(0);
            this.bottomLayout.setTranslationY(-AndroidUtilities.dp(48.0F));
          }
          if (paramn.a() == null)
            break label769;
          this.videoPlayerControlFrameLayout.setVisibility(8);
        }
        while (true)
        {
          this.videoPlayerControlFrameLayout.setVisibility(0);
          this.dateTextView.setVisibility(8);
          this.nameTextView.setVisibility(8);
          if (this.allowShare)
          {
            this.shareButton.setVisibility(8);
            this.menuItem.showSubItem(10);
          }
          this.videoPlayer.setPlayWhenReady(paramBoolean1);
          this.inPreview = paramBoolean2;
          return;
          label701: l1 = 0L;
          break;
          com.b.a.a.a.a().a(new k("Video Viewed"));
          this.playAdvertising = false;
          this.videoPlayer.preparePlayer(Uri.fromFile(paramFile), "other");
          this.actionBar.setVisibility(0);
          this.bottomLayout.setVisibility(0);
          this.parentActivity.setRequestedOrientation(2);
          break label553;
          label769: this.videoPlayerControlFrameLayout.setVisibility(0);
        }
      }
      catch (Exception paramFile)
      {
        break label488;
      }
    }
  }

  private boolean processOpenVideo(String paramString)
  {
    label580: label869: label891: 
    do
      while (true)
      {
        Object localObject1;
        int j;
        try
        {
          this.videoPreviewMessageObject = null;
          this.compressItem.setVisibility(8);
          this.muteVideo = false;
          this.videoTimelineView.setVideoPath(paramString);
          this.compressionsCount = -1;
          this.originalSize = new File(paramString).length();
          localObject1 = new d(paramString);
          List localList = h.b((b)localObject1, "/moov/trak/");
          paramString = null;
          i = 1;
          if (h.a((b)localObject1, "/moov/trak/mdia/minf/stbl/stsd/mp4a/") != null)
            break label897;
          i = 0;
          break label897;
          if (h.a((b)localObject1, "/moov/trak/mdia/minf/stbl/stsd/avc1/") != null)
            break label891;
          i = 0;
          break label904;
          if (j >= localList.size())
            break;
          localObject1 = (TrackBox)(com.coremedia.iso.boxes.a)localList.get(j);
          long l1 = 0L;
          long l2 = l1;
          try
          {
            Object localObject2 = ((TrackBox)localObject1).getMediaBox();
            l2 = l1;
            MediaHeaderBox localMediaHeaderBox = ((MediaBox)localObject2).getMediaHeaderBox();
            l2 = l1;
            localObject2 = ((MediaBox)localObject2).getMediaInformationBox().getSampleTableBox().getSampleSizeBox().getSampleSizes();
            int k = 0;
            l2 = l1;
            if (k >= localObject2.length)
              continue;
            l1 += localObject2[k];
            k += 1;
            continue;
            l2 = l1;
            this.videoDuration = ((float)localMediaHeaderBox.getDuration() / (float)localMediaHeaderBox.getTimescale());
            float f1 = (float)(8L * l1);
            l2 = l1;
            float f2 = this.videoDuration;
            l2 = (int)(f1 / f2);
            localObject1 = ((TrackBox)localObject1).getTrackHeaderBox();
            if ((((TrackHeaderBox)localObject1).getWidth() == 0.0D) || (((TrackHeaderBox)localObject1).getHeight() == 0.0D))
              continue;
            k = (int)(l2 / 100000L * 100000L);
            this.bitrate = k;
            this.originalBitrate = k;
            if (this.bitrate <= 900000)
              continue;
            this.bitrate = 900000;
            this.videoFramesSize += l1;
            paramString = (String)localObject1;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            l1 = l2;
            l2 = 0L;
            continue;
            this.audioFramesSize += l1;
          }
          localObject1 = paramString.getMatrix();
          if (!((g)localObject1).equals(g.k))
            continue;
          this.rotationValue = 90;
          j = (int)paramString.getWidth();
          this.originalWidth = j;
          this.resultWidth = j;
          j = (int)paramString.getHeight();
          this.originalHeight = j;
          this.resultHeight = j;
          this.videoDuration *= 1000.0F;
          this.selectedCompression = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("compress_video2", 1);
          if ((this.originalWidth <= 1280) && (this.originalHeight <= 1280))
            break label580;
          this.compressionsCount = 5;
          updateWidthHeightBitrateForCompression();
          if (i != 0)
            break label672;
          if (this.resultWidth == this.originalWidth)
            break label925;
          if (this.resultHeight != this.originalHeight)
            break label672;
          break label925;
          if (((g)localObject1).equals(g.l))
          {
            this.rotationValue = 180;
            continue;
          }
        }
        catch (Exception paramString)
        {
          FileLog.e(paramString);
          return false;
        }
        if (!((g)localObject1).equals(g.m))
          continue;
        this.rotationValue = 270;
        continue;
        if ((this.originalWidth > 848) || (this.originalHeight > 848))
        {
          this.compressionsCount = 4;
          continue;
        }
        if ((this.originalWidth > 640) || (this.originalHeight > 640))
        {
          this.compressionsCount = 3;
          continue;
        }
        if ((this.originalWidth > 480) || (this.originalHeight > 480))
        {
          this.compressionsCount = 2;
          continue;
        }
        this.compressionsCount = 1;
        continue;
        paramString = this.compressItem;
        if (this.compressionsCount > 1)
          i = 0;
        while (true)
        {
          paramString.setVisibility(i);
          if ((Build.VERSION.SDK_INT >= 16) && (Build.VERSION.SDK_INT < 18) && (this.compressItem.getVisibility() == 0));
          try
          {
            paramString = MediaController.selectCodec("video/avc");
            if (paramString == null)
              this.compressItem.setVisibility(8);
            while (true)
            {
              updateVideoInfo();
              updateMuteButton();
              return true;
              i = 8;
              break;
              localObject1 = paramString.getName();
              if ((!((String)localObject1).equals("OMX.google.h264.encoder")) && (!((String)localObject1).equals("OMX.ST.VFM.H264Enc")) && (!((String)localObject1).equals("OMX.Exynos.avc.enc")) && (!((String)localObject1).equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER")) && (!((String)localObject1).equals("OMX.MARVELL.VIDEO.H264ENCODER")) && (!((String)localObject1).equals("OMX.k3.video.encoder.avc")) && (!((String)localObject1).equals("OMX.TI.DUCATI1.VIDEO.H264E")))
                break label869;
              this.compressItem.setVisibility(8);
            }
          }
          catch (Exception paramString)
          {
            while (true)
            {
              this.compressItem.setVisibility(8);
              FileLog.e(paramString);
              continue;
              if (MediaController.selectColorFormat(paramString, "video/avc") != 0)
                continue;
              this.compressItem.setVisibility(8);
            }
          }
        }
        int i = 1;
        if (i == 0)
        {
          return false;
          j = 0;
          continue;
          j += 1;
        }
      }
    while (paramString != null);
    label672: return false;
    label897: label904: label925: return false;
  }

  private void redraw(int paramInt)
  {
    if ((paramInt < 6) && (this.containerView != null))
    {
      this.containerView.invalidate();
      AndroidUtilities.runOnUIThread(new Runnable(paramInt)
      {
        public void run()
        {
          PhotoViewer.this.redraw(this.val$count + 1);
        }
      }
      , 100L);
    }
  }

  private void releasePlayer()
  {
    if (this.videoPlayer != null)
    {
      this.videoPlayer.releasePlayer();
      this.videoPlayer = null;
    }
    try
    {
      this.parentActivity.getWindow().clearFlags(128);
      if (this.aspectRatioFrameLayout != null)
      {
        this.containerView.removeView(this.aspectRatioFrameLayout);
        this.aspectRatioFrameLayout = null;
      }
      this.playAdvertising = false;
      this.actionBar.setVisibility(0);
      this.bottomLayout.setVisibility(0);
      this.parentActivity.setRequestedOrientation(2);
      if (this.videoUrl != null)
        this.containerView.removeView(this.videoUrl);
      if (this.videoTextureView != null)
        this.videoTextureView = null;
      if (this.isPlaying)
      {
        this.isPlaying = false;
        this.videoPlayButton.setImageResource(2130837872);
        AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
      }
      if ((!this.inPreview) && (!this.requestingPreview) && (this.videoPlayerControlFrameLayout != null))
      {
        this.videoPlayerControlFrameLayout.setVisibility(8);
        this.dateTextView.setVisibility(0);
        this.nameTextView.setVisibility(0);
        if (this.allowShare)
        {
          this.shareButton.setVisibility(0);
          this.menuItem.hideSubItem(10);
        }
      }
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  private void requestVideoPreview(int paramInt)
  {
    if (Build.VERSION.SDK_INT < 16);
    label494: 
    do
    {
      return;
      if (this.videoPreviewMessageObject != null)
        MediaController.getInstance().cancelVideoConvert(this.videoPreviewMessageObject);
      int i;
      if ((this.requestingPreview) && (!this.tryStartRequestPreviewOnFinish))
        i = 1;
      while (true)
      {
        this.requestingPreview = false;
        this.loadInitialVideo = false;
        this.progressView.setVisibility(4);
        if (paramInt != 1)
          break label494;
        if (this.selectedCompression != this.compressionsCount - 1)
          break;
        this.tryStartRequestPreviewOnFinish = false;
        if (i == 0)
        {
          localObject = e.a(ApplicationLoader.applicationContext).c();
          preparePlayer(this.currentPlayingVideoFile, false, false, (n)localObject);
          return;
          i = 0;
          continue;
        }
        this.progressView.setVisibility(0);
        this.loadInitialVideo = true;
        return;
      }
      this.requestingPreview = true;
      releasePlayer();
      if (this.videoPreviewMessageObject == null)
      {
        localObject = new TLRPC.TL_message();
        ((TLRPC.TL_message)localObject).id = 0;
        ((TLRPC.TL_message)localObject).message = "";
        ((TLRPC.TL_message)localObject).media = new TLRPC.TL_messageMediaEmpty();
        ((TLRPC.TL_message)localObject).action = new TLRPC.TL_messageActionEmpty();
        this.videoPreviewMessageObject = new MessageObject((TLRPC.Message)localObject, null, false);
        this.videoPreviewMessageObject.messageOwner.attachPath = new File(FileLoader.getInstance().getDirectory(4), "video_preview.mp4").getAbsolutePath();
        this.videoPreviewMessageObject.videoEditedInfo = new VideoEditedInfo();
        this.videoPreviewMessageObject.videoEditedInfo.rotationValue = this.rotationValue;
        this.videoPreviewMessageObject.videoEditedInfo.originalWidth = this.originalWidth;
        this.videoPreviewMessageObject.videoEditedInfo.originalHeight = this.originalHeight;
        this.videoPreviewMessageObject.videoEditedInfo.originalPath = this.currentPlayingVideoFile.getAbsolutePath();
      }
      localObject = this.videoPreviewMessageObject.videoEditedInfo;
      long l2 = this.startTime;
      ((VideoEditedInfo)localObject).startTime = l2;
      localObject = this.videoPreviewMessageObject.videoEditedInfo;
      long l3 = this.endTime;
      ((VideoEditedInfo)localObject).endTime = l3;
      long l1 = l2;
      if (l2 == -1L)
        l1 = 0L;
      l2 = l3;
      if (l3 == -1L)
        l2 = ()(this.videoDuration * 1000.0F);
      if (l2 - l1 > 5000000L)
        this.videoPreviewMessageObject.videoEditedInfo.endTime = (l1 + 5000000L);
      this.videoPreviewMessageObject.videoEditedInfo.bitrate = this.bitrate;
      this.videoPreviewMessageObject.videoEditedInfo.resultWidth = this.resultWidth;
      this.videoPreviewMessageObject.videoEditedInfo.resultHeight = this.resultHeight;
      if (!MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true))
        this.tryStartRequestPreviewOnFinish = true;
      this.requestingPreview = true;
      this.progressView.setVisibility(0);
      return;
      this.tryStartRequestPreviewOnFinish = false;
    }
    while (paramInt != 2);
    Object localObject = e.a(ApplicationLoader.applicationContext).c();
    preparePlayer(this.currentPlayingVideoFile, false, true, (n)localObject);
  }

  private void setCurrentCaption(CharSequence paramCharSequence)
  {
    if ((paramCharSequence != null) && (paramCharSequence.length() > 0))
    {
      this.captionTextView = this.captionTextViewOld;
      this.captionTextViewOld = this.captionTextViewNew;
      this.captionTextViewNew = this.captionTextView;
      Theme.createChatResources(null, true);
      paramCharSequence = Emoji.replaceEmoji(new SpannableStringBuilder(paramCharSequence.toString()), this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
      this.captionTextView.setTag(paramCharSequence);
      try
      {
        this.captionTextView.setText(paramCharSequence);
        this.captionTextView.setTextColor(-1);
        paramCharSequence = this.captionTextView;
        if ((this.bottomLayout.getVisibility() == 0) || (this.pickerView.getVisibility() == 0))
        {
          f = 1.0F;
          paramCharSequence.setAlpha(f);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              int i = 4;
              PhotoViewer.this.captionTextViewOld.setTag(null);
              PhotoViewer.this.captionTextViewOld.setVisibility(4);
              TextView localTextView = PhotoViewer.this.captionTextViewNew;
              if ((PhotoViewer.this.bottomLayout.getVisibility() == 0) || (PhotoViewer.this.pickerView.getVisibility() == 0))
                i = 0;
              localTextView.setVisibility(i);
            }
          });
          return;
        }
      }
      catch (Exception paramCharSequence)
      {
        while (true)
        {
          FileLog.e(paramCharSequence);
          continue;
          float f = 0.0F;
        }
      }
    }
    if (this.needCaptionLayout)
      try
      {
        this.captionTextView.setText(LocaleController.getString("AddCaption", 2131165274));
        this.captionTextView.setTag("empty");
        this.captionTextView.setVisibility(0);
        this.captionTextView.setTextColor(-1291845633);
        return;
      }
      catch (Exception paramCharSequence)
      {
        while (true)
          FileLog.e(paramCharSequence);
      }
    this.captionTextView.setTextColor(-1);
    this.captionTextView.setTag(null);
    this.captionTextView.setVisibility(4);
  }

  private void setImageIndex(int paramInt, boolean paramBoolean)
  {
    if ((this.currentIndex == paramInt) || (this.placeProvider == null))
      return;
    if (!paramBoolean)
      this.currentThumb = null;
    this.currentFileNames[0] = getFileName(paramInt);
    this.currentFileNames[1] = getFileName(paramInt + 1);
    this.currentFileNames[2] = getFileName(paramInt - 1);
    this.placeProvider.willSwitchFromPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
    int m = this.currentIndex;
    this.currentIndex = paramInt;
    boolean bool = false;
    paramBoolean = false;
    int j = 0;
    Object localObject1;
    int i;
    label372: label634: long l;
    label398: label449: label711: label737: label758: int k;
    if (!this.imagesArr.isEmpty())
    {
      if ((this.currentIndex < 0) || (this.currentIndex >= this.imagesArr.size()))
      {
        closePhoto(false, false);
        return;
      }
      localObject1 = (MessageObject)this.imagesArr.get(this.currentIndex);
      if ((this.currentMessageObject != null) && (this.currentMessageObject.getId() == ((MessageObject)localObject1).getId()));
      for (i = 1; ; i = 0)
      {
        this.currentMessageObject = ((MessageObject)localObject1);
        paramBoolean = this.currentMessageObject.isVideo();
        bool = this.currentMessageObject.isInvoice();
        if (!bool)
          break label682;
        this.masksItem.setVisibility(8);
        this.menuItem.hideSubItem(6);
        this.menuItem.hideSubItem(11);
        setCurrentCaption(this.currentMessageObject.messageOwner.media.description);
        this.allowShare = false;
        this.bottomLayout.setTranslationY(AndroidUtilities.dp(48.0F));
        this.captionTextViewOld.setTranslationY(AndroidUtilities.dp(48.0F));
        this.captionTextViewNew.setTranslationY(AndroidUtilities.dp(48.0F));
        if (this.currentAnimation == null)
          break label1057;
        this.menuItem.hideSubItem(1);
        this.menuItem.hideSubItem(10);
        if (!this.currentMessageObject.canDeleteMessage(null))
          this.menuItem.setVisibility(8);
        this.allowShare = true;
        this.shareButton.setVisibility(0);
        this.actionBar.setTitle(LocaleController.getString("AttachGif", 2131165364));
        if (this.currentPlaceObject != null)
        {
          if (this.animationInProgress != 0)
            break label2587;
          this.currentPlaceObject.imageReceiver.setVisible(true, true);
        }
        this.currentPlaceObject = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
        if (this.currentPlaceObject != null)
        {
          if (this.animationInProgress != 0)
            break label2598;
          this.currentPlaceObject.imageReceiver.setVisible(false, true);
        }
        if (i == 0)
        {
          this.draggingDown = false;
          this.translationX = 0.0F;
          this.translationY = 0.0F;
          this.scale = 1.0F;
          this.animateToX = 0.0F;
          this.animateToY = 0.0F;
          this.animateToScale = 1.0F;
          this.animationStartTime = 0L;
          this.imageMoveAnimation = null;
          this.changeModeAnimation = null;
          if (this.aspectRatioFrameLayout != null)
            this.aspectRatioFrameLayout.setVisibility(4);
          releasePlayer();
          this.pinchStartDistance = 0.0F;
          this.pinchStartScale = 1.0F;
          this.pinchCenterX = 0.0F;
          this.pinchCenterY = 0.0F;
          this.pinchStartX = 0.0F;
          this.pinchStartY = 0.0F;
          this.moveStartX = 0.0F;
          this.moveStartY = 0.0F;
          this.zooming = false;
          this.moving = false;
          this.doubleTap = false;
          this.invalidCoords = false;
          this.canDragDown = true;
          this.changingPage = false;
          this.switchImageAfterAnimation = 0;
          if ((this.imagesArrLocals.isEmpty()) && ((this.currentFileNames[0] == null) || (paramBoolean) || (this.photoProgressViews[0].backgroundState == 0)))
            break label2609;
          paramBoolean = true;
          this.canZoom = paramBoolean;
          updateMinMax(this.scale);
        }
        if (m != -1)
          break label2614;
        setImages();
        paramInt = 0;
        while (paramInt < 3)
        {
          checkProgress(paramInt, false);
          paramInt += 1;
        }
        break;
      }
      label682: localObject1 = this.masksItem;
      if ((this.currentMessageObject.hasPhotoStickers()) && ((int)this.currentMessageObject.getDialogId() != 0))
      {
        paramInt = 0;
        ((ActionBarMenuItem)localObject1).setVisibility(paramInt);
        if (!this.currentMessageObject.canDeleteMessage(null))
          break label951;
        this.menuItem.showSubItem(6);
        if ((!paramBoolean) || (Build.VERSION.SDK_INT < 16))
          break label963;
        this.menuItem.showSubItem(11);
        if (!this.currentMessageObject.isFromUser())
          break label988;
        localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
        if (localObject1 == null)
          break label975;
        this.nameTextView.setText(UserObject.getUserName((TLRPC.User)localObject1));
        label806: l = this.currentMessageObject.messageOwner.date * 1000L;
        localObject1 = LocaleController.formatString("formatDateAtTime", 2131166662, new Object[] { LocaleController.getInstance().formatterYear.format(new Date(l)), LocaleController.getInstance().formatterDay.format(new Date(l)) });
        if ((this.currentFileNames[0] == null) || (!paramBoolean))
          break label1045;
        this.dateTextView.setText(String.format("%s (%s)", new Object[] { localObject1, AndroidUtilities.formatFileSize(this.currentMessageObject.getDocument().size) }));
      }
      while (true)
      {
        setCurrentCaption(this.currentMessageObject.caption);
        break;
        paramInt = 4;
        break label711;
        label951: this.menuItem.hideSubItem(6);
        break label737;
        label963: this.menuItem.hideSubItem(11);
        break label758;
        label975: this.nameTextView.setText("");
        break label806;
        label988: localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
        if (localObject1 != null)
        {
          this.nameTextView.setText(((TLRPC.Chat)localObject1).title);
          break label806;
        }
        this.nameTextView.setText("");
        break label806;
        label1045: this.dateTextView.setText((CharSequence)localObject1);
      }
      label1057: if ((this.totalImagesCount + this.totalImagesCountMerge != 0) && (!this.needSearchImageInArr))
        if (this.opennedFromMedia)
          if ((this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge) && (!this.loadingMoreImages) && (this.currentIndex > this.imagesArr.size() - 5))
          {
            if (!this.imagesArr.isEmpty())
              break label1344;
            paramInt = 0;
            label1137: k = 0;
            if ((this.endReached[0] == 0) || (this.mergeDialogId == 0L))
              break label2858;
            if ((this.imagesArr.isEmpty()) || (((MessageObject)this.imagesArr.get(this.imagesArr.size() - 1)).getDialogId() == this.mergeDialogId))
              break label2846;
            paramInt = 1;
            j = 0;
          }
    }
    while (true)
    {
      if (paramInt == 0)
        l = this.currentDialogId;
      while (true)
      {
        SharedMediaQuery.loadMedia(l, 0, 80, j, 0, true, this.classGuid);
        this.loadingMoreImages = true;
        this.actionBar.setTitle(LocaleController.formatString("Of", 2131166154, new Object[] { Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge) }));
        label1281: if ((this.currentMessageObject.messageOwner.ttl == 0) || (this.currentMessageObject.messageOwner.ttl >= 3600))
          break label1701;
        this.allowShare = false;
        this.menuItem.hideSubItem(1);
        this.shareButton.setVisibility(8);
        this.menuItem.hideSubItem(10);
        break;
        label1344: paramInt = ((MessageObject)this.imagesArr.get(this.imagesArr.size() - 1)).getId();
        break label1137;
        l = this.mergeDialogId;
      }
      if ((this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge) && (!this.loadingMoreImages) && (this.currentIndex < 5))
      {
        if (!this.imagesArr.isEmpty())
          break label1582;
        paramInt = 0;
        label1425: k = 0;
        if ((this.endReached[0] == 0) || (this.mergeDialogId == 0L))
          break label2837;
        if ((this.imagesArr.isEmpty()) || (((MessageObject)this.imagesArr.get(0)).getDialogId() == this.mergeDialogId))
          break label2825;
        paramInt = 1;
        j = 0;
      }
      while (true)
      {
        if (paramInt == 0)
          l = this.currentDialogId;
        while (true)
        {
          SharedMediaQuery.loadMedia(l, 0, 80, j, 0, true, this.classGuid);
          this.loadingMoreImages = true;
          this.actionBar.setTitle(LocaleController.formatString("Of", 2131166154, new Object[] { Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge - this.imagesArr.size() + this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge) }));
          break;
          label1582: paramInt = ((MessageObject)this.imagesArr.get(0)).getId();
          break label1425;
          l = this.mergeDialogId;
        }
        if ((this.currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
        {
          if (this.currentMessageObject.isVideo())
          {
            this.actionBar.setTitle(LocaleController.getString("AttachVideo", 2131165369));
            break label1281;
          }
          this.actionBar.setTitle(LocaleController.getString("AttachPhoto", 2131165367));
          break label1281;
        }
        if (!bool)
          break label1281;
        this.actionBar.setTitle(this.currentMessageObject.messageOwner.media.title);
        break label1281;
        label1701: this.allowShare = true;
        this.menuItem.showSubItem(1);
        localObject1 = this.shareButton;
        if ((this.videoPlayerControlFrameLayout == null) || (this.videoPlayerControlFrameLayout.getVisibility() != 0));
        for (paramInt = 0; ; paramInt = 8)
        {
          ((ImageView)localObject1).setVisibility(paramInt);
          if (this.shareButton.getVisibility() != 0)
            break label1773;
          this.menuItem.hideSubItem(10);
          break;
        }
        label1773: this.menuItem.showSubItem(10);
        break label372;
        if (!this.imagesArrLocations.isEmpty())
        {
          this.nameTextView.setText("");
          this.dateTextView.setText("");
          if ((this.avatarsDialogId == UserConfig.getClientUserId()) && (!this.avatarsArr.isEmpty()))
            this.menuItem.showSubItem(6);
          while (true)
          {
            localObject1 = this.currentFileLocation;
            if ((paramInt >= 0) && (paramInt < this.imagesArrLocations.size()))
              break;
            closePhoto(false, false);
            return;
            this.menuItem.hideSubItem(6);
          }
          this.currentFileLocation = ((TLRPC.FileLocation)this.imagesArrLocations.get(paramInt));
          if ((localObject1 == null) || (this.currentFileLocation == null) || (((TLRPC.FileLocation)localObject1).local_id != this.currentFileLocation.local_id) || (((TLRPC.FileLocation)localObject1).volume_id != this.currentFileLocation.volume_id))
            break label2820;
        }
        label2820: for (paramInt = 1; ; paramInt = 0)
        {
          this.actionBar.setTitle(LocaleController.formatString("Of", 2131166154, new Object[] { Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.imagesArrLocations.size()) }));
          this.menuItem.showSubItem(1);
          this.allowShare = true;
          localObject1 = this.shareButton;
          if ((this.videoPlayerControlFrameLayout == null) || (this.videoPlayerControlFrameLayout.getVisibility() != 0))
          {
            i = 0;
            label2027: ((ImageView)localObject1).setVisibility(i);
            if (this.shareButton.getVisibility() != 0)
              break label2063;
            this.menuItem.hideSubItem(10);
          }
          while (true)
          {
            i = paramInt;
            break;
            i = 8;
            break label2027;
            label2063: this.menuItem.showSubItem(10);
          }
          i = j;
          if (this.imagesArrLocals.isEmpty())
            break label372;
          if ((paramInt < 0) || (paramInt >= this.imagesArrLocals.size()))
          {
            closePhoto(false, false);
            return;
          }
          Object localObject2 = this.imagesArrLocals.get(paramInt);
          if ((localObject2 instanceof MediaController.PhotoEntry))
          {
            MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)localObject2;
            this.currentPathObject = localPhotoEntry.path;
            if ((localPhotoEntry.bucketId == 0) && (localPhotoEntry.dateTaken == 0L) && (this.imagesArrLocals.size() == 1));
            for (paramInt = 1; ; paramInt = 0)
            {
              localObject1 = localPhotoEntry.caption;
              paramBoolean = localPhotoEntry.isVideo;
              label2188: if (paramInt == 0)
                break label2539;
              if (!paramBoolean)
                break label2520;
              this.muteItem.setVisibility(0);
              this.captionItem.setVisibility(0);
              this.captionTextViewNew.setTranslationY(AndroidUtilities.dp(96.0F));
              this.captionTextViewOld.setTranslationY(AndroidUtilities.dp(96.0F));
              this.videoTimelineViewContainer.setVisibility(0);
              processOpenVideo(this.currentPathObject);
              this.actionBar.setTitle(LocaleController.getString("AttachVideo", 2131165369));
              label2273: if (this.sendPhotoType == 0)
                this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), false);
              setCurrentCaption((CharSequence)localObject1);
              updateCaptionTextForCurrentPhoto(localObject2);
              i = j;
              break;
            }
          }
          if ((localObject2 instanceof TLRPC.BotInlineResult))
          {
            localObject1 = (TLRPC.BotInlineResult)localObject2;
            this.currentBotInlineResult = ((TLRPC.BotInlineResult)localObject1);
            if (((TLRPC.BotInlineResult)localObject1).document != null)
            {
              paramBoolean = MessageObject.isVideoDocument(((TLRPC.BotInlineResult)localObject1).document);
              this.currentPathObject = FileLoader.getPathToAttach(((TLRPC.BotInlineResult)localObject1).document).getAbsolutePath();
            }
          }
          while (true)
          {
            localObject1 = null;
            paramInt = 0;
            break label2188;
            if (((TLRPC.BotInlineResult)localObject1).photo != null)
            {
              this.currentPathObject = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(((TLRPC.BotInlineResult)localObject1).photo.sizes, AndroidUtilities.getPhotoSize())).getAbsolutePath();
              paramBoolean = false;
              continue;
            }
            if (((TLRPC.BotInlineResult)localObject1).content_url != null)
            {
              this.currentPathObject = ((TLRPC.BotInlineResult)localObject1).content_url;
              paramBoolean = ((TLRPC.BotInlineResult)localObject1).type.equals("video");
              continue;
              if ((localObject2 instanceof MediaController.SearchImage))
              {
                localObject1 = (MediaController.SearchImage)localObject2;
                if (((MediaController.SearchImage)localObject1).document != null);
                for (this.currentPathObject = FileLoader.getPathToAttach(((MediaController.SearchImage)localObject1).document, true).getAbsolutePath(); ; this.currentPathObject = ((MediaController.SearchImage)localObject1).imageUrl)
                {
                  localObject1 = ((MediaController.SearchImage)localObject1).caption;
                  paramInt = 0;
                  paramBoolean = bool;
                  break;
                }
                label2520: this.actionBar.setTitle(LocaleController.getString("AttachPhoto", 2131165367));
                break label2273;
                label2539: this.actionBar.setTitle(LocaleController.formatString("Of", 2131166154, new Object[] { Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.imagesArrLocals.size()) }));
                break label2273;
                label2587: this.showAfterAnimation = this.currentPlaceObject;
                break label398;
                label2598: this.hideAfterAnimation = this.currentPlaceObject;
                break label449;
                label2609: paramBoolean = false;
                break label634;
                label2614: checkProgress(0, false);
                if (m > this.currentIndex)
                {
                  localObject1 = this.rightImage;
                  this.rightImage = this.centerImage;
                  this.centerImage = this.leftImage;
                  this.leftImage = ((ImageReceiver)localObject1);
                  localObject1 = this.photoProgressViews[0];
                  this.photoProgressViews[0] = this.photoProgressViews[2];
                  this.photoProgressViews[2] = localObject1;
                  setIndexToImage(this.leftImage, this.currentIndex - 1);
                  checkProgress(1, false);
                  checkProgress(2, false);
                  return;
                }
                if (m >= this.currentIndex)
                  break;
                localObject1 = this.leftImage;
                this.leftImage = this.centerImage;
                this.centerImage = this.rightImage;
                this.rightImage = ((ImageReceiver)localObject1);
                localObject1 = this.photoProgressViews[0];
                this.photoProgressViews[0] = this.photoProgressViews[1];
                this.photoProgressViews[1] = localObject1;
                setIndexToImage(this.rightImage, this.currentIndex + 1);
                checkProgress(1, false);
                checkProgress(2, false);
                return;
              }
              localObject1 = null;
              paramInt = 0;
              paramBoolean = bool;
              break label2188;
            }
            paramBoolean = false;
          }
        }
        label2825: k = 1;
        j = paramInt;
        paramInt = k;
        continue;
        label2837: j = paramInt;
        paramInt = k;
      }
      label2846: k = 1;
      j = paramInt;
      paramInt = k;
      continue;
      label2858: j = paramInt;
      paramInt = k;
    }
  }

  private void setImages()
  {
    if (this.animationInProgress == 0)
    {
      setIndexToImage(this.centerImage, this.currentIndex);
      setIndexToImage(this.rightImage, this.currentIndex + 1);
      setIndexToImage(this.leftImage, this.currentIndex - 1);
    }
  }

  private void setIndexToImage(ImageReceiver paramImageReceiver, int paramInt)
  {
    paramImageReceiver.setOrientation(0, false);
    Object localObject6;
    int i;
    Object localObject2;
    Object localObject1;
    if (!this.imagesArrLocals.isEmpty())
    {
      paramImageReceiver.setParentMessageObject(null);
      if ((paramInt >= 0) && (paramInt < this.imagesArrLocals.size()))
      {
        localObject6 = this.imagesArrLocals.get(paramInt);
        i = (int)(AndroidUtilities.getPhotoSize() / AndroidUtilities.density);
        localObject2 = null;
        localObject1 = localObject2;
        if (this.currentThumb != null)
        {
          localObject1 = localObject2;
          if (paramImageReceiver == this.centerImage)
            localObject1 = this.currentThumb;
        }
        if (localObject1 != null)
          break label1305;
      }
    }
    label1153: label1296: label1305: for (Object localObject5 = this.placeProvider.getThumbForPhoto(null, null, paramInt); ; localObject5 = localObject1)
    {
      localObject2 = null;
      Object localObject3 = null;
      localObject1 = null;
      paramInt = 0;
      Object localObject4 = null;
      if ((localObject6 instanceof MediaController.PhotoEntry))
      {
        localObject1 = (MediaController.PhotoEntry)localObject6;
        if (((MediaController.PhotoEntry)localObject1).isVideo)
          break label1296;
        if (((MediaController.PhotoEntry)localObject1).imagePath != null)
        {
          localObject1 = ((MediaController.PhotoEntry)localObject1).imagePath;
          localObject3 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(i), Integer.valueOf(i) });
          localObject2 = localObject1;
          localObject1 = localObject3;
        }
      }
      while (true)
      {
        paramInt = 0;
        localObject4 = null;
        localObject3 = localObject2;
        localObject6 = null;
        localObject2 = localObject1;
        localObject1 = localObject6;
        if (localObject1 != null)
          if (localObject5 != null)
          {
            localObject2 = new BitmapDrawable(null, (Bitmap)localObject5);
            label238: if (localObject5 != null)
              break label722;
            localObject3 = ((TLRPC.Document)localObject1).thumb.location;
            label253: paramImageReceiver.setImage((TLObject)localObject1, null, "d", (Drawable)localObject2, (TLRPC.FileLocation)localObject3, String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(i), Integer.valueOf(i) }), paramInt, null, false);
            return;
            paramImageReceiver.setOrientation(((MediaController.PhotoEntry)localObject1).orientation, false);
            localObject1 = ((MediaController.PhotoEntry)localObject1).path;
            break;
            if ((localObject6 instanceof TLRPC.BotInlineResult))
            {
              localObject1 = (TLRPC.BotInlineResult)localObject6;
              if ((((TLRPC.BotInlineResult)localObject1).type.equals("video")) || (MessageObject.isVideoDocument(((TLRPC.BotInlineResult)localObject1).document)))
                if (((TLRPC.BotInlineResult)localObject1).document != null)
                {
                  localObject1 = ((TLRPC.BotInlineResult)localObject1).document.thumb.location;
                  localObject3 = null;
                  paramInt = 0;
                  localObject2 = null;
                }
            }
          }
        while (true)
        {
          localObject6 = localObject1;
          localObject1 = localObject2;
          localObject2 = localObject4;
          localObject4 = localObject6;
          break;
          localObject3 = ((TLRPC.BotInlineResult)localObject1).thumb_url;
          paramInt = 0;
          localObject2 = null;
          localObject1 = null;
          continue;
          if ((((TLRPC.BotInlineResult)localObject1).type.equals("gif")) && (((TLRPC.BotInlineResult)localObject1).document != null))
          {
            localObject2 = ((TLRPC.BotInlineResult)localObject1).document;
            paramInt = ((TLRPC.BotInlineResult)localObject1).document.size;
            localObject4 = "d";
            localObject3 = null;
            localObject1 = null;
            continue;
          }
          if (((TLRPC.BotInlineResult)localObject1).photo != null)
          {
            localObject2 = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.BotInlineResult)localObject1).photo.sizes, AndroidUtilities.getPhotoSize());
            localObject1 = ((TLRPC.PhotoSize)localObject2).location;
            paramInt = ((TLRPC.PhotoSize)localObject2).size;
            localObject4 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(i), Integer.valueOf(i) });
            localObject3 = null;
            localObject2 = null;
            continue;
          }
          if (((TLRPC.BotInlineResult)localObject1).content_url != null)
          {
            if (((TLRPC.BotInlineResult)localObject1).type.equals("gif"));
            for (localObject4 = "d"; ; localObject4 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(i), Integer.valueOf(i) }))
            {
              localObject3 = ((TLRPC.BotInlineResult)localObject1).content_url;
              paramInt = 0;
              localObject2 = null;
              localObject1 = null;
              break;
            }
            if ((localObject6 instanceof MediaController.SearchImage))
            {
              localObject4 = (MediaController.SearchImage)localObject6;
              if (((MediaController.SearchImage)localObject4).imagePath != null)
                localObject3 = ((MediaController.SearchImage)localObject4).imagePath;
              while (true)
              {
                localObject4 = null;
                localObject2 = "d";
                break;
                if (((MediaController.SearchImage)localObject4).document != null)
                {
                  localObject1 = ((MediaController.SearchImage)localObject4).document;
                  paramInt = ((MediaController.SearchImage)localObject4).document.size;
                  localObject3 = localObject2;
                  continue;
                }
                localObject3 = ((MediaController.SearchImage)localObject4).imageUrl;
                paramInt = ((MediaController.SearchImage)localObject4).size;
              }
              localObject2 = null;
              break label238;
              label722: localObject3 = null;
              break label253;
              if (localObject4 != null)
              {
                if (localObject5 != null);
                for (localObject1 = new BitmapDrawable(null, (Bitmap)localObject5); ; localObject1 = null)
                {
                  paramImageReceiver.setImage((TLObject)localObject4, null, (String)localObject2, (Drawable)localObject1, null, String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(i), Integer.valueOf(i) }), paramInt, null, false);
                  return;
                }
              }
              if (localObject5 != null);
              for (localObject1 = new BitmapDrawable(null, (Bitmap)localObject5); ; localObject1 = null)
              {
                paramImageReceiver.setImage((String)localObject3, (String)localObject2, (Drawable)localObject1, null, paramInt);
                return;
              }
              paramImageReceiver.setImageBitmap((Bitmap)null);
              return;
              localObject3 = new int[1];
              localObject4 = getFileLocation(paramInt, localObject3);
              if (localObject4 != null)
              {
                localObject1 = null;
                if (!this.imagesArr.isEmpty())
                  localObject1 = (MessageObject)this.imagesArr.get(paramInt);
                paramImageReceiver.setParentMessageObject((MessageObject)localObject1);
                if (localObject1 != null)
                  paramImageReceiver.setShouldGenerateQualityThumb(true);
                if ((localObject1 != null) && (((MessageObject)localObject1).isVideo()))
                {
                  paramImageReceiver.setNeedsQualityThumb(true);
                  if ((((MessageObject)localObject1).photoThumbs != null) && (!((MessageObject)localObject1).photoThumbs.isEmpty()))
                    if ((this.currentThumb == null) || (paramImageReceiver != this.centerImage))
                      break label1262;
                }
              }
              label1194: label1200: label1206: label1262: for (localObject2 = this.currentThumb; ; localObject2 = null)
              {
                localObject3 = FileLoader.getClosestPhotoSizeWithSize(((MessageObject)localObject1).photoThumbs, 100);
                if (localObject2 != null);
                for (localObject1 = new BitmapDrawable(null, (Bitmap)localObject2); ; localObject1 = null)
                {
                  paramImageReceiver.setImage(null, null, null, (Drawable)localObject1, ((TLRPC.PhotoSize)localObject3).location, "b", 0, null, true);
                  return;
                }
                paramImageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(2130838018));
                return;
                if ((localObject1 != null) && (this.currentAnimation != null))
                {
                  paramImageReceiver.setImageBitmap(this.currentAnimation);
                  this.currentAnimation.setSecondParentView(this.containerView);
                  return;
                }
                paramImageReceiver.setNeedsQualityThumb(false);
                if ((this.currentThumb != null) && (paramImageReceiver == this.centerImage));
                for (localObject2 = this.currentThumb; ; localObject2 = null)
                {
                  if (localObject3[0] == 0)
                    localObject3[0] = -1;
                  if (localObject1 != null)
                  {
                    localObject1 = FileLoader.getClosestPhotoSizeWithSize(((MessageObject)localObject1).photoThumbs, 100);
                    if (localObject2 == null)
                      break label1194;
                    localObject2 = new BitmapDrawable(null, (Bitmap)localObject2);
                    label1141: if (localObject1 == null)
                      break label1200;
                    localObject1 = ((TLRPC.PhotoSize)localObject1).location;
                    paramInt = localObject3[0];
                    if (this.avatarsDialogId == 0)
                      break label1206;
                  }
                  for (boolean bool = true; ; bool = false)
                  {
                    paramImageReceiver.setImage((TLObject)localObject4, null, null, (Drawable)localObject2, (TLRPC.FileLocation)localObject1, "b", paramInt, null, bool);
                    return;
                    localObject1 = null;
                    break;
                    localObject2 = null;
                    break label1141;
                    localObject1 = null;
                    break label1153;
                  }
                  paramImageReceiver.setNeedsQualityThumb(false);
                  paramImageReceiver.setParentMessageObject(null);
                  if (localObject3[0] == 0)
                  {
                    paramImageReceiver.setImageBitmap((Bitmap)null);
                    return;
                  }
                  paramImageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(2130838018));
                  return;
                }
              }
            }
            paramInt = 0;
            localObject4 = null;
            localObject2 = null;
            localObject1 = null;
            break;
          }
          paramInt = 0;
          localObject2 = null;
          localObject3 = null;
          localObject1 = null;
        }
        localObject1 = null;
        localObject2 = null;
      }
    }
  }

  private void setScaleToFill()
  {
    float f5 = this.centerImage.getBitmapWidth();
    float f1 = getContainerViewWidth();
    float f3 = this.centerImage.getBitmapHeight();
    float f2 = getContainerViewHeight();
    float f4 = Math.min(f2 / f3, f1 / f5);
    f5 = (int)(f5 * f4);
    f3 = (int)(f3 * f4);
    this.scale = Math.max(f1 / f5, f2 / f3);
    updateMinMax(this.scale);
  }

  private void showQualityView(boolean paramBoolean)
  {
    if (paramBoolean)
      this.previousCompression = this.selectedCompression;
    if (this.qualityChooseViewAnimation != null)
      this.qualityChooseViewAnimation.cancel();
    this.qualityChooseViewAnimation = new AnimatorSet();
    if (paramBoolean)
    {
      this.qualityChooseView.setTag(Integer.valueOf(1));
      this.qualityChooseViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(152.0F) }), ObjectAnimator.ofFloat(this.videoTimelineViewContainer, "translationY", new float[] { 0.0F, AndroidUtilities.dp(152.0F) }), ObjectAnimator.ofFloat(this.bottomLayout, "translationY", new float[] { -AndroidUtilities.dp(48.0F), AndroidUtilities.dp(104.0F) }) });
    }
    while (true)
    {
      this.qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter(paramBoolean)
      {
        public void onAnimationCancel(Animator paramAnimator)
        {
          PhotoViewer.access$13702(PhotoViewer.this, null);
        }

        public void onAnimationEnd(Animator paramAnimator)
        {
          if (!paramAnimator.equals(PhotoViewer.this.qualityChooseViewAnimation))
            return;
          PhotoViewer.access$13702(PhotoViewer.this, new AnimatorSet());
          if (this.val$show)
          {
            PhotoViewer.this.qualityChooseView.setVisibility(0);
            PhotoViewer.this.qualityPicker.setVisibility(0);
            PhotoViewer.this.qualityChooseViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.access$13800(PhotoViewer.this), "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.access$13900(PhotoViewer.this), "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.access$2700(PhotoViewer.this), "translationY", new float[] { -AndroidUtilities.dp(48.0F) }) });
          }
          while (true)
          {
            PhotoViewer.this.qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnimator)
              {
                if (paramAnimator.equals(PhotoViewer.this.qualityChooseViewAnimation))
                  PhotoViewer.access$13702(PhotoViewer.this, null);
              }
            });
            PhotoViewer.this.qualityChooseViewAnimation.setDuration(200L);
            PhotoViewer.this.qualityChooseViewAnimation.setInterpolator(new AccelerateInterpolator());
            PhotoViewer.this.qualityChooseViewAnimation.start();
            return;
            PhotoViewer.this.qualityChooseView.setVisibility(4);
            PhotoViewer.this.qualityPicker.setVisibility(4);
            PhotoViewer.this.qualityChooseViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.access$2200(PhotoViewer.this), "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.access$300(PhotoViewer.this), "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.access$2700(PhotoViewer.this), "translationY", new float[] { -AndroidUtilities.dp(48.0F) }) });
          }
        }
      });
      this.qualityChooseViewAnimation.setDuration(200L);
      this.qualityChooseViewAnimation.setInterpolator(new DecelerateInterpolator());
      this.qualityChooseViewAnimation.start();
      return;
      this.qualityChooseView.setTag(null);
      this.qualityChooseViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.qualityChooseView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(166.0F) }), ObjectAnimator.ofFloat(this.qualityPicker, "translationY", new float[] { 0.0F, AndroidUtilities.dp(166.0F) }), ObjectAnimator.ofFloat(this.bottomLayout, "translationY", new float[] { -AndroidUtilities.dp(48.0F), AndroidUtilities.dp(118.0F) }) });
    }
  }

  private void switchToEditMode(int paramInt)
  {
    if ((this.currentEditMode == paramInt) || (this.centerImage.getBitmap() == null) || (this.changeModeAnimation != null) || (this.imageMoveAnimation != null) || (this.photoProgressViews[0].backgroundState != -1) || (this.captionEditText.getTag() != null));
    int j;
    label239: Object localObject;
    label260: label284: label454: label461: label471: label600: 
    do
    {
      return;
      if (paramInt == 0)
      {
        if ((this.currentEditMode == 2) && (this.photoFilterView.getToolsView().getVisibility() != 0))
        {
          this.photoFilterView.switchToOrFromEditMode();
          return;
        }
        float f4;
        float f2;
        float f3;
        float f1;
        if (this.centerImage.getBitmap() != null)
        {
          i = this.centerImage.getBitmapWidth();
          j = this.centerImage.getBitmapHeight();
          f4 = getContainerViewWidth() / i;
          float f5 = getContainerViewHeight() / j;
          f2 = getContainerViewWidth(0) / i;
          f3 = getContainerViewHeight(0) / j;
          f1 = f4;
          if (f4 > f5)
            f1 = f5;
          if (f2 > f3)
          {
            f2 = f3;
            if ((this.sendPhotoType != 1) || (this.applying))
              break label461;
            f4 = Math.min(getContainerViewWidth(), getContainerViewHeight());
            f3 = f4 / i;
            f4 /= j;
            if (f3 <= f4)
              break label454;
            this.scale = (f3 / f1);
            this.animateToScale = (f2 * this.scale / f3);
            this.animateToX = 0.0F;
            if (this.currentEditMode != 1)
              break label471;
            this.animateToY = AndroidUtilities.dp(58.0F);
            if (Build.VERSION.SDK_INT >= 21)
              this.animateToY -= AndroidUtilities.statusBarHeight / 2;
            this.animationStartTime = System.currentTimeMillis();
            this.zoomAnimation = true;
          }
        }
        else
        {
          this.imageMoveAnimation = new AnimatorSet();
          if (this.currentEditMode != 1)
            break label521;
          this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.editorDoneLayout, "translationY", new float[] { AndroidUtilities.dp(48.0F) }), ObjectAnimator.ofFloat(this, "animationValue", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.photoCropView, "alpha", new float[] { 0.0F }) });
        }
        do
          while (true)
          {
            this.imageMoveAnimation.setDuration(200L);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter(paramInt)
            {
              public void onAnimationEnd(Animator paramAnimator)
              {
                if (PhotoViewer.this.currentEditMode == 1)
                {
                  PhotoViewer.this.editorDoneLayout.setVisibility(8);
                  PhotoViewer.this.photoCropView.setVisibility(8);
                }
                while (true)
                {
                  PhotoViewer.access$11302(PhotoViewer.this, null);
                  PhotoViewer.access$9002(PhotoViewer.this, this.val$mode);
                  PhotoViewer.access$11402(PhotoViewer.this, false);
                  PhotoViewer.access$11502(PhotoViewer.this, 1.0F);
                  PhotoViewer.access$11602(PhotoViewer.this, 0.0F);
                  PhotoViewer.access$11702(PhotoViewer.this, 0.0F);
                  PhotoViewer.access$3602(PhotoViewer.this, 1.0F);
                  PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                  PhotoViewer.this.containerView.invalidate();
                  paramAnimator = new AnimatorSet();
                  ArrayList localArrayList = new ArrayList();
                  localArrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, "translationY", new float[] { 0.0F }));
                  localArrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, "translationY", new float[] { 0.0F }));
                  if (PhotoViewer.this.needCaptionLayout)
                    localArrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.captionTextView, "translationY", new float[] { 0.0F }));
                  if (PhotoViewer.this.sendPhotoType == 0)
                    localArrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.checkImageView, "alpha", new float[] { 1.0F }));
                  paramAnimator.playTogether(localArrayList);
                  paramAnimator.setDuration(200L);
                  paramAnimator.addListener(new AnimatorListenerAdapter()
                  {
                    public void onAnimationStart(Animator paramAnimator)
                    {
                      PhotoViewer.this.pickerView.setVisibility(0);
                      PhotoViewer.this.actionBar.setVisibility(0);
                      if (PhotoViewer.this.needCaptionLayout)
                      {
                        paramAnimator = PhotoViewer.this.captionTextView;
                        if (PhotoViewer.this.captionTextView.getTag() == null)
                          break label103;
                      }
                      label103: for (int i = 0; ; i = 4)
                      {
                        paramAnimator.setVisibility(i);
                        if (PhotoViewer.this.sendPhotoType == 0)
                          PhotoViewer.this.checkImageView.setVisibility(0);
                        return;
                      }
                    }
                  });
                  paramAnimator.start();
                  return;
                  if (PhotoViewer.this.currentEditMode == 2)
                  {
                    PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoFilterView);
                    PhotoViewer.access$11102(PhotoViewer.this, null);
                    continue;
                  }
                  if (PhotoViewer.this.currentEditMode != 3)
                    continue;
                  PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoPaintView);
                  PhotoViewer.access$11202(PhotoViewer.this, null);
                }
              }
            });
            this.imageMoveAnimation.start();
            return;
            break;
            f3 = f4;
            break label239;
            this.animateToScale = (f2 / f1);
            break label260;
            if (this.currentEditMode == 2)
            {
              this.animateToY = AndroidUtilities.dp(62.0F);
              break label284;
            }
            if (this.currentEditMode != 3)
              break label284;
            this.animateToY = ((AndroidUtilities.dp(48.0F) - ActionBar.getCurrentActionBarHeight()) / 2);
            break label284;
            if (this.currentEditMode != 2)
              break label600;
            this.photoFilterView.shutdown();
            this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.photoFilterView.getToolsView(), "translationY", new float[] { AndroidUtilities.dp(126.0F) }), ObjectAnimator.ofFloat(this, "animationValue", new float[] { 0.0F, 1.0F }) });
          }
        while (this.currentEditMode != 3);
        this.photoPaintView.shutdown();
        localObject = this.imageMoveAnimation;
        ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofFloat(this.photoPaintView.getToolsView(), "translationY", new float[] { AndroidUtilities.dp(126.0F) });
        ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofFloat(this.photoPaintView.getColorPicker(), "translationX", new float[] { AndroidUtilities.dp(60.0F) });
        ActionBar localActionBar = this.photoPaintView.getActionBar();
        j = -ActionBar.getCurrentActionBarHeight();
        if (Build.VERSION.SDK_INT >= 21);
        for (i = AndroidUtilities.statusBarHeight; ; i = 0)
        {
          ((AnimatorSet)localObject).playTogether(new Animator[] { localObjectAnimator1, localObjectAnimator2, ObjectAnimator.ofFloat(localActionBar, "translationY", new float[] { j - i }), ObjectAnimator.ofFloat(this, "animationValue", new float[] { 0.0F, 1.0F }) });
          break;
        }
      }
      if (paramInt == 1)
      {
        if (this.photoCropView == null)
        {
          this.photoCropView = new PhotoCropView(this.actvityContext);
          this.photoCropView.setVisibility(8);
          this.containerView.addView(this.photoCropView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 48.0F));
          this.photoCropView.setDelegate(new PhotoCropView.PhotoCropViewDelegate()
          {
            public Bitmap getBitmap()
            {
              return PhotoViewer.this.centerImage.getBitmap();
            }

            public void needMoveImageTo(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean)
            {
              if (paramBoolean)
              {
                PhotoViewer.this.animateTo(paramFloat3, paramFloat1, paramFloat2, true);
                return;
              }
              PhotoViewer.access$3702(PhotoViewer.this, paramFloat1);
              PhotoViewer.access$3802(PhotoViewer.this, paramFloat2);
              PhotoViewer.access$3602(PhotoViewer.this, paramFloat3);
              PhotoViewer.this.containerView.invalidate();
            }

            public void onChange(boolean paramBoolean)
            {
              TextView localTextView = PhotoViewer.this.resetButton;
              if (paramBoolean);
              for (int i = 8; ; i = 0)
              {
                localTextView.setVisibility(i);
                return;
              }
            }
          });
        }
        this.photoCropView.onAppear();
        this.editorDoneLayout.doneButton.setText(LocaleController.getString("Crop", 2131165593));
        this.editorDoneLayout.doneButton.setTextColor(-11420173);
        this.changeModeAnimation = new AnimatorSet();
        localObject = new ArrayList();
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.actionBar, "translationY", new float[] { 0.0F, -this.actionBar.getHeight() }));
        if (this.needCaptionLayout)
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
        if (this.sendPhotoType == 0)
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[] { 1.0F, 0.0F }));
        this.changeModeAnimation.playTogether((Collection)localObject);
        this.changeModeAnimation.setDuration(200L);
        this.changeModeAnimation.addListener(new AnimatorListenerAdapter(paramInt)
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            PhotoViewer.access$12002(PhotoViewer.this, null);
            PhotoViewer.this.pickerView.setVisibility(8);
            if (PhotoViewer.this.needCaptionLayout)
              PhotoViewer.this.captionTextView.setVisibility(4);
            if (PhotoViewer.this.sendPhotoType == 0)
              PhotoViewer.this.checkImageView.setVisibility(8);
            paramAnimator = PhotoViewer.this.centerImage.getBitmap();
            boolean bool;
            int j;
            float f1;
            float f3;
            if (paramAnimator != null)
            {
              PhotoCropView localPhotoCropView = PhotoViewer.this.photoCropView;
              i = PhotoViewer.this.centerImage.getOrientation();
              if (PhotoViewer.this.sendPhotoType == 1)
                break label510;
              bool = true;
              localPhotoCropView.setBitmap(paramAnimator, i, bool);
              i = PhotoViewer.this.centerImage.getBitmapWidth();
              j = PhotoViewer.this.centerImage.getBitmapHeight();
              float f2 = PhotoViewer.this.getContainerViewWidth() / i;
              float f4 = PhotoViewer.this.getContainerViewHeight() / j;
              f1 = PhotoViewer.this.getContainerViewWidth(1) / i;
              f3 = PhotoViewer.this.getContainerViewHeight(1) / j;
              if (f2 <= f4)
                break label516;
              f2 = f4;
              label214: if (f1 <= f3)
                break label519;
              f1 = f3;
              label224: if (PhotoViewer.this.sendPhotoType == 1)
              {
                f3 = Math.min(PhotoViewer.this.getContainerViewWidth(1), PhotoViewer.this.getContainerViewHeight(1));
                f1 = f3 / i;
                f3 /= j;
                if (f1 <= f3)
                  break label522;
              }
              label279: PhotoViewer.access$11502(PhotoViewer.this, f1 / f2);
              PhotoViewer.access$11602(PhotoViewer.this, 0.0F);
              paramAnimator = PhotoViewer.this;
              j = -AndroidUtilities.dp(56.0F);
              if (Build.VERSION.SDK_INT < 21)
                break label528;
            }
            label516: label519: label522: label528: for (int i = AndroidUtilities.statusBarHeight / 2; ; i = 0)
            {
              PhotoViewer.access$11702(paramAnimator, i + j);
              PhotoViewer.access$12302(PhotoViewer.this, System.currentTimeMillis());
              PhotoViewer.access$12402(PhotoViewer.this, true);
              PhotoViewer.access$11302(PhotoViewer.this, new AnimatorSet());
              PhotoViewer.this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.access$11000(PhotoViewer.this), "translationY", new float[] { AndroidUtilities.dp(48.0F), 0.0F }), ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.access$9100(PhotoViewer.this), "alpha", new float[] { 0.0F, 1.0F }) });
              PhotoViewer.this.imageMoveAnimation.setDuration(200L);
              PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter()
              {
                public void onAnimationEnd(Animator paramAnimator)
                {
                  PhotoViewer.this.photoCropView.onAppeared();
                  PhotoViewer.access$11302(PhotoViewer.this, null);
                  PhotoViewer.access$9002(PhotoViewer.this, PhotoViewer.43.this.val$mode);
                  PhotoViewer.access$11502(PhotoViewer.this, 1.0F);
                  PhotoViewer.access$11602(PhotoViewer.this, 0.0F);
                  PhotoViewer.access$11702(PhotoViewer.this, 0.0F);
                  PhotoViewer.access$3602(PhotoViewer.this, 1.0F);
                  PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                  PhotoViewer.this.containerView.invalidate();
                }

                public void onAnimationStart(Animator paramAnimator)
                {
                  PhotoViewer.this.editorDoneLayout.setVisibility(0);
                  PhotoViewer.this.photoCropView.setVisibility(0);
                }
              });
              PhotoViewer.this.imageMoveAnimation.start();
              return;
              label510: bool = false;
              break;
              break label214;
              break label224;
              f1 = f3;
              break label279;
            }
          }
        });
        this.changeModeAnimation.start();
        return;
      }
      if (paramInt != 2)
        continue;
      if (this.photoFilterView == null)
      {
        this.photoFilterView = new PhotoFilterView(this.parentActivity, this.centerImage.getBitmap(), this.centerImage.getOrientation());
        this.containerView.addView(this.photoFilterView, LayoutHelper.createFrame(-1, -1.0F));
        this.photoFilterView.getDoneTextView().setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            PhotoViewer.this.applyCurrentEditMode();
            PhotoViewer.this.switchToEditMode(0);
          }
        });
        this.photoFilterView.getCancelTextView().setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (PhotoViewer.this.photoFilterView.hasChanges())
            {
              if (PhotoViewer.this.parentActivity == null)
                return;
              paramView = new AlertDialog.Builder(PhotoViewer.this.parentActivity);
              paramView.setMessage(LocaleController.getString("DiscardChanges", 2131165659));
              paramView.setTitle(LocaleController.getString("AppName", 2131165319));
              paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                  PhotoViewer.this.switchToEditMode(0);
                }
              });
              paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
              PhotoViewer.this.showAlertDialog(paramView);
              return;
            }
            PhotoViewer.this.switchToEditMode(0);
          }
        });
        this.photoFilterView.getToolsView().setTranslationY(AndroidUtilities.dp(126.0F));
      }
      this.changeModeAnimation = new AnimatorSet();
      localObject = new ArrayList();
      ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
      ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.actionBar, "translationY", new float[] { 0.0F, -this.actionBar.getHeight() }));
      if (this.needCaptionLayout)
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
      if (this.sendPhotoType == 0)
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[] { 1.0F, 0.0F }));
      this.changeModeAnimation.playTogether((Collection)localObject);
      this.changeModeAnimation.setDuration(200L);
      this.changeModeAnimation.addListener(new AnimatorListenerAdapter(paramInt)
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          PhotoViewer.access$12002(PhotoViewer.this, null);
          PhotoViewer.this.pickerView.setVisibility(8);
          PhotoViewer.this.actionBar.setVisibility(8);
          if (PhotoViewer.this.needCaptionLayout)
            PhotoViewer.this.captionTextView.setVisibility(4);
          if (PhotoViewer.this.sendPhotoType == 0)
            PhotoViewer.this.checkImageView.setVisibility(8);
          int j;
          if (PhotoViewer.this.centerImage.getBitmap() != null)
          {
            i = PhotoViewer.this.centerImage.getBitmapWidth();
            j = PhotoViewer.this.centerImage.getBitmapHeight();
            float f1 = PhotoViewer.this.getContainerViewWidth() / i;
            float f4 = PhotoViewer.this.getContainerViewHeight() / j;
            float f2 = PhotoViewer.this.getContainerViewWidth(2) / i;
            float f3 = PhotoViewer.this.getContainerViewHeight(2) / j;
            if (f1 <= f4)
              break label397;
            f1 = f4;
            if (f2 <= f3)
              break label400;
            f2 = f3;
            label189: PhotoViewer.access$11502(PhotoViewer.this, f2 / f1);
            PhotoViewer.access$11602(PhotoViewer.this, 0.0F);
            paramAnimator = PhotoViewer.this;
            j = -AndroidUtilities.dp(62.0F);
            if (Build.VERSION.SDK_INT < 21)
              break label403;
          }
          label397: label400: label403: for (int i = AndroidUtilities.statusBarHeight / 2; ; i = 0)
          {
            PhotoViewer.access$11702(paramAnimator, i + j);
            PhotoViewer.access$12302(PhotoViewer.this, System.currentTimeMillis());
            PhotoViewer.access$12402(PhotoViewer.this, true);
            PhotoViewer.access$11302(PhotoViewer.this, new AnimatorSet());
            PhotoViewer.this.imageMoveAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(PhotoViewer.access$11100(PhotoViewer.this).getToolsView(), "translationY", new float[] { AndroidUtilities.dp(126.0F), 0.0F }) });
            PhotoViewer.this.imageMoveAnimation.setDuration(200L);
            PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnimator)
              {
                PhotoViewer.this.photoFilterView.init();
                PhotoViewer.access$11302(PhotoViewer.this, null);
                PhotoViewer.access$9002(PhotoViewer.this, PhotoViewer.46.this.val$mode);
                PhotoViewer.access$11502(PhotoViewer.this, 1.0F);
                PhotoViewer.access$11602(PhotoViewer.this, 0.0F);
                PhotoViewer.access$11702(PhotoViewer.this, 0.0F);
                PhotoViewer.access$3602(PhotoViewer.this, 1.0F);
                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                PhotoViewer.this.containerView.invalidate();
              }

              public void onAnimationStart(Animator paramAnimator)
              {
              }
            });
            PhotoViewer.this.imageMoveAnimation.start();
            return;
            break;
            break label189;
          }
        }
      });
      this.changeModeAnimation.start();
      return;
    }
    while (paramInt != 3);
    label521: if (this.photoPaintView == null)
    {
      this.photoPaintView = new PhotoPaintView(this.parentActivity, this.centerImage.getBitmap(), this.centerImage.getOrientation());
      this.containerView.addView(this.photoPaintView, LayoutHelper.createFrame(-1, -1.0F));
      this.photoPaintView.getDoneTextView().setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          PhotoViewer.this.applyCurrentEditMode();
          PhotoViewer.this.switchToEditMode(0);
        }
      });
      this.photoPaintView.getCancelTextView().setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          PhotoViewer.this.photoPaintView.maybeShowDismissalAlert(PhotoViewer.this, PhotoViewer.this.parentActivity, new Runnable()
          {
            public void run()
            {
              PhotoViewer.this.switchToEditMode(0);
            }
          });
        }
      });
      this.photoPaintView.getColorPicker().setTranslationX(AndroidUtilities.dp(60.0F));
      this.photoPaintView.getToolsView().setTranslationY(AndroidUtilities.dp(126.0F));
      localObject = this.photoPaintView.getActionBar();
      j = -ActionBar.getCurrentActionBarHeight();
      if (Build.VERSION.SDK_INT < 21)
        break label1791;
    }
    label1791: for (int i = AndroidUtilities.statusBarHeight; ; i = 0)
    {
      ((ActionBar)localObject).setTranslationY(j - i);
      this.changeModeAnimation = new AnimatorSet();
      localObject = new ArrayList();
      ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
      ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.actionBar, "translationY", new float[] { 0.0F, -this.actionBar.getHeight() }));
      if (this.needCaptionLayout)
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", new float[] { 0.0F, AndroidUtilities.dp(96.0F) }));
      if (this.sendPhotoType == 0)
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[] { 1.0F, 0.0F }));
      this.changeModeAnimation.playTogether((Collection)localObject);
      this.changeModeAnimation.setDuration(200L);
      this.changeModeAnimation.addListener(new AnimatorListenerAdapter(paramInt)
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          PhotoViewer.access$12002(PhotoViewer.this, null);
          PhotoViewer.this.pickerView.setVisibility(8);
          if (PhotoViewer.this.needCaptionLayout)
            PhotoViewer.this.captionTextView.setVisibility(4);
          if (PhotoViewer.this.sendPhotoType == 0)
            PhotoViewer.this.checkImageView.setVisibility(8);
          int j;
          label177: label227: ObjectAnimator localObjectAnimator1;
          ObjectAnimator localObjectAnimator2;
          ObjectAnimator localObjectAnimator3;
          ActionBar localActionBar;
          if (PhotoViewer.this.centerImage.getBitmap() != null)
          {
            i = PhotoViewer.this.centerImage.getBitmapWidth();
            j = PhotoViewer.this.centerImage.getBitmapHeight();
            float f1 = PhotoViewer.this.getContainerViewWidth() / i;
            float f4 = PhotoViewer.this.getContainerViewHeight() / j;
            float f2 = PhotoViewer.this.getContainerViewWidth(3) / i;
            float f3 = PhotoViewer.this.getContainerViewHeight(3) / j;
            if (f1 > f4)
            {
              f1 = f4;
              if (f2 <= f3)
                break label500;
              f2 = f3;
              PhotoViewer.access$11502(PhotoViewer.this, f2 / f1);
              PhotoViewer.access$11602(PhotoViewer.this, 0.0F);
              paramAnimator = PhotoViewer.this;
              j = ActionBar.getCurrentActionBarHeight();
              int k = AndroidUtilities.dp(48.0F);
              if (Build.VERSION.SDK_INT < 21)
                break label503;
              i = AndroidUtilities.statusBarHeight;
              PhotoViewer.access$11702(paramAnimator, (i + (j - k)) / 2);
              PhotoViewer.access$12302(PhotoViewer.this, System.currentTimeMillis());
              PhotoViewer.access$12402(PhotoViewer.this, true);
            }
          }
          else
          {
            PhotoViewer.access$11302(PhotoViewer.this, new AnimatorSet());
            paramAnimator = PhotoViewer.this.imageMoveAnimation;
            localObjectAnimator1 = ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[] { 0.0F, 1.0F });
            localObjectAnimator2 = ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getColorPicker(), "translationX", new float[] { AndroidUtilities.dp(60.0F), 0.0F });
            localObjectAnimator3 = ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getToolsView(), "translationY", new float[] { AndroidUtilities.dp(126.0F), 0.0F });
            localActionBar = PhotoViewer.this.photoPaintView.getActionBar();
            j = -ActionBar.getCurrentActionBarHeight();
            if (Build.VERSION.SDK_INT < 21)
              break label509;
          }
          label500: label503: label509: for (int i = AndroidUtilities.statusBarHeight; ; i = 0)
          {
            paramAnimator.playTogether(new Animator[] { localObjectAnimator1, localObjectAnimator2, localObjectAnimator3, ObjectAnimator.ofFloat(localActionBar, "translationY", new float[] { j - i, 0.0F }) });
            PhotoViewer.this.imageMoveAnimation.setDuration(200L);
            PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnimator)
              {
                PhotoViewer.this.photoPaintView.init();
                PhotoViewer.access$11302(PhotoViewer.this, null);
                PhotoViewer.access$9002(PhotoViewer.this, PhotoViewer.49.this.val$mode);
                PhotoViewer.access$11502(PhotoViewer.this, 1.0F);
                PhotoViewer.access$11602(PhotoViewer.this, 0.0F);
                PhotoViewer.access$11702(PhotoViewer.this, 0.0F);
                PhotoViewer.access$3602(PhotoViewer.this, 1.0F);
                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                PhotoViewer.this.containerView.invalidate();
              }

              public void onAnimationStart(Animator paramAnimator)
              {
              }
            });
            PhotoViewer.this.imageMoveAnimation.start();
            return;
            break;
            break label177;
            i = 0;
            break label227;
          }
        }
      });
      this.changeModeAnimation.start();
      return;
    }
  }

  private void toggleActionBar(boolean paramBoolean1, boolean paramBoolean2)
  {
    float f1 = 1.0F;
    if ((paramBoolean1) && (!this.playAdvertising))
    {
      this.actionBar.setVisibility(0);
      if (this.canShowBottom)
      {
        this.bottomLayout.setVisibility(0);
        if (this.captionTextView.getTag() != null)
          this.captionTextView.setVisibility(0);
      }
    }
    this.isActionBarVisible = paramBoolean1;
    this.actionBar.setEnabled(paramBoolean1);
    this.bottomLayout.setEnabled(paramBoolean1);
    float f2;
    if (paramBoolean2)
    {
      localObject1 = new ArrayList();
      Object localObject2 = this.actionBar;
      if (paramBoolean1)
      {
        f2 = 1.0F;
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject2, "alpha", new float[] { f2 }));
        localObject2 = this.bottomLayout;
        if (!paramBoolean1)
          break label263;
        f2 = 1.0F;
        label136: ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject2, "alpha", new float[] { f2 }));
        if (this.captionTextView.getTag() != null)
        {
          localObject2 = this.captionTextView;
          if (!paramBoolean1)
            break label269;
        }
      }
      while (true)
      {
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject2, "alpha", new float[] { f1 }));
        this.currentActionBarAnimation = new AnimatorSet();
        this.currentActionBarAnimation.playTogether((Collection)localObject1);
        if (!paramBoolean1)
          this.currentActionBarAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              if ((PhotoViewer.this.currentActionBarAnimation != null) && (PhotoViewer.this.currentActionBarAnimation.equals(paramAnimator)))
              {
                PhotoViewer.this.actionBar.setVisibility(8);
                if (PhotoViewer.this.canShowBottom)
                {
                  PhotoViewer.this.bottomLayout.setVisibility(8);
                  if (PhotoViewer.this.captionTextView.getTag() != null)
                    PhotoViewer.this.captionTextView.setVisibility(4);
                }
                PhotoViewer.access$12502(PhotoViewer.this, null);
              }
            }
          });
        this.currentActionBarAnimation.setDuration(200L);
        this.currentActionBarAnimation.start();
        return;
        f2 = 0.0F;
        break;
        label263: f2 = 0.0F;
        break label136;
        label269: f1 = 0.0F;
      }
    }
    Object localObject1 = this.actionBar;
    if (paramBoolean1)
    {
      f2 = 1.0F;
      label287: ((ActionBar)localObject1).setAlpha(f2);
      localObject1 = this.bottomLayout;
      if (!paramBoolean1)
        break label394;
      f2 = 1.0F;
      label307: ((FrameLayout)localObject1).setAlpha(f2);
      if (this.captionTextView.getTag() != null)
      {
        localObject1 = this.captionTextView;
        if (!paramBoolean1)
          break label400;
      }
    }
    while (true)
    {
      ((TextView)localObject1).setAlpha(f1);
      if (paramBoolean1)
        break;
      this.actionBar.setVisibility(8);
      if (!this.canShowBottom)
        break;
      this.bottomLayout.setVisibility(8);
      if (this.captionTextView.getTag() == null)
        break;
      this.captionTextView.setVisibility(4);
      return;
      f2 = 0.0F;
      break label287;
      label394: f2 = 0.0F;
      break label307;
      label400: f1 = 0.0F;
    }
  }

  private void toggleCheckImageView(boolean paramBoolean)
  {
    float f2 = 1.0F;
    AnimatorSet localAnimatorSet = new AnimatorSet();
    ArrayList localArrayList = new ArrayList();
    Object localObject = this.pickerView;
    float f1;
    if (paramBoolean)
    {
      f1 = 1.0F;
      localArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f1 }));
      if (this.needCaptionLayout)
      {
        localObject = this.captionTextView;
        if (!paramBoolean)
          break label160;
        f1 = 1.0F;
        label72: localArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f1 }));
      }
      if (this.sendPhotoType == 0)
      {
        localObject = this.checkImageView;
        if (!paramBoolean)
          break label165;
        f1 = f2;
      }
    }
    while (true)
    {
      localArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f1 }));
      localAnimatorSet.playTogether(localArrayList);
      localAnimatorSet.setDuration(200L);
      localAnimatorSet.start();
      return;
      f1 = 0.0F;
      break;
      label160: f1 = 0.0F;
      break label72;
      label165: f1 = 0.0F;
    }
  }

  private void updateCaptionTextForCurrentPhoto(Object paramObject)
  {
    Object localObject2 = null;
    Object localObject1;
    if ((paramObject instanceof MediaController.PhotoEntry))
      localObject1 = ((MediaController.PhotoEntry)paramObject).caption;
    while ((localObject1 == null) || (((CharSequence)localObject1).length() == 0))
    {
      this.captionEditText.setFieldText("");
      return;
      localObject1 = localObject2;
      if ((paramObject instanceof TLRPC.BotInlineResult))
        continue;
      localObject1 = localObject2;
      if (!(paramObject instanceof MediaController.SearchImage))
        continue;
      localObject1 = ((MediaController.SearchImage)paramObject).caption;
    }
    this.captionEditText.setFieldText((CharSequence)localObject1);
  }

  private void updateMinMax(float paramFloat)
  {
    int i = (int)(this.centerImage.getImageWidth() * paramFloat - getContainerViewWidth()) / 2;
    int j = (int)(this.centerImage.getImageHeight() * paramFloat - getContainerViewHeight()) / 2;
    if (i > 0)
    {
      this.minX = (-i);
      this.maxX = i;
      if (j <= 0)
        break label160;
      this.minY = (-j);
      this.maxY = j;
    }
    while (true)
    {
      if (this.currentEditMode == 1)
      {
        this.maxX += this.photoCropView.getLimitX();
        this.maxY += this.photoCropView.getLimitY();
        this.minX -= this.photoCropView.getLimitWidth();
        this.minY -= this.photoCropView.getLimitHeight();
      }
      return;
      this.maxX = 0.0F;
      this.minX = 0.0F;
      break;
      label160: this.maxY = 0.0F;
      this.minY = 0.0F;
    }
  }

  private void updateSelectedCount()
  {
    if (this.placeProvider == null)
      return;
    this.pickerView.updateSelectedCount(this.placeProvider.getSelectedCount(), false);
  }

  private void updateVideoInfo()
  {
    if (this.actionBar == null)
      return;
    int i;
    label113: int j;
    label137: label177: label196: ActionBar localActionBar;
    if (this.selectedCompression == 0)
    {
      this.compressItem.setImageResource(2130838113);
      this.estimatedDuration = ()Math.ceil((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration);
      if ((this.compressItem.getVisibility() != 8) && ((this.compressItem.getVisibility() != 0) || (this.selectedCompression != this.compressionsCount - 1)))
        break label430;
      if ((this.rotationValue != 90) && (this.rotationValue != 270))
        break label414;
      i = this.originalHeight;
      if ((this.rotationValue != 90) && (this.rotationValue != 270))
        break label422;
      j = this.originalWidth;
      this.estimatedSize = (int)((float)this.originalSize * ((float)this.estimatedDuration / this.videoDuration));
      if (this.videoTimelineView.getLeftProgress() != 0.0F)
        break label543;
      this.startTime = -1L;
      if (this.videoTimelineView.getRightProgress() != 1.0F)
        break label567;
      this.endTime = -1L;
      str = String.format("%dx%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
      i = (int)(this.estimatedDuration / 1000L / 60L);
      this.currentSubtitle = String.format("%s, %s", new Object[] { str, String.format("%d:%02d, ~%s", new Object[] { Integer.valueOf(i), Integer.valueOf((int)Math.ceil(this.estimatedDuration / 1000L) - i * 60), AndroidUtilities.formatFileSize(this.estimatedSize) }) });
      localActionBar = this.actionBar;
      if (!this.muteVideo)
        break label591;
    }
    label414: label422: label430: label454: label591: for (String str = null; ; str = this.currentSubtitle)
    {
      localActionBar.setSubtitle(str);
      return;
      if (this.selectedCompression == 1)
      {
        this.compressItem.setImageResource(2130838114);
        break;
      }
      if (this.selectedCompression == 2)
      {
        this.compressItem.setImageResource(2130838115);
        break;
      }
      if (this.selectedCompression == 3)
      {
        this.compressItem.setImageResource(2130838116);
        break;
      }
      if (this.selectedCompression != 4)
        break;
      this.compressItem.setImageResource(2130838112);
      break;
      i = this.originalWidth;
      break label113;
      j = this.originalHeight;
      break label137;
      if ((this.rotationValue == 90) || (this.rotationValue == 270))
      {
        i = this.resultHeight;
        if ((this.rotationValue != 90) && (this.rotationValue != 270))
          break label535;
      }
      for (j = this.resultWidth; ; j = this.resultHeight)
      {
        this.estimatedSize = (int)((float)(this.audioFramesSize + this.videoFramesSize) * ((float)this.estimatedDuration / this.videoDuration));
        this.estimatedSize += this.estimatedSize / 32768 * 16;
        break;
        i = this.resultWidth;
        break label454;
      }
      this.startTime = (()(this.videoTimelineView.getLeftProgress() * this.videoDuration) * 1000L);
      break label177;
      this.endTime = (()(this.videoTimelineView.getRightProgress() * this.videoDuration) * 1000L);
      break label196;
    }
  }

  private void updateVideoPlayerTime()
  {
    String str;
    if (this.videoPlayer == null)
      str = "00:00 / 00:00";
    while (true)
    {
      if (!TextUtils.equals(this.videoPlayerTime.getText(), str))
        this.videoPlayerTime.setText(str);
      return;
      long l4 = this.videoPlayer.getCurrentPosition();
      long l3 = this.videoPlayer.getDuration();
      if ((l3 != -9223372036854775807L) && (l4 != -9223372036854775807L))
      {
        long l2 = l3;
        long l1 = l4;
        if (!this.inPreview)
        {
          l2 = l3;
          l1 = l4;
          if (this.videoTimelineViewContainer != null)
          {
            l2 = l3;
            l1 = l4;
            if (this.videoTimelineViewContainer.getVisibility() == 0)
            {
              l3 = ()((float)l3 * (this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()));
              l4 = ()((float)l4 - this.videoTimelineView.getLeftProgress() * (float)l3);
              l2 = l3;
              l1 = l4;
              if (l4 > l3)
              {
                l1 = l3;
                l2 = l3;
              }
            }
          }
        }
        l1 /= 1000L;
        l2 /= 1000L;
        str = String.format("%02d:%02d / %02d:%02d", new Object[] { Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L), Long.valueOf(l2 / 60L), Long.valueOf(l2 % 60L) });
        continue;
      }
      str = "00:00 / 00:00";
    }
  }

  private void updateWidthHeightBitrateForCompression()
  {
    if (this.selectedCompression >= this.compressionsCount)
      this.selectedCompression = (this.compressionsCount - 1);
    int i;
    float f;
    if (this.selectedCompression != this.compressionsCount - 1)
      switch (this.selectedCompression)
      {
      default:
        i = 1600000;
        f = 1280.0F;
        if (this.originalWidth <= this.originalHeight)
          break;
        f /= this.originalWidth;
      case 0:
      case 1:
      case 2:
      }
    while (true)
    {
      this.resultWidth = (Math.round(this.originalWidth * f / 2.0F) * 2);
      this.resultHeight = (Math.round(this.originalHeight * f / 2.0F) * 2);
      if (this.bitrate != 0)
      {
        this.bitrate = Math.min(i, (int)(this.originalBitrate / f));
        this.videoFramesSize = ()(this.bitrate / 8 * this.videoDuration / 1000.0F);
      }
      return;
      f = 432.0F;
      i = 400000;
      break;
      f = 640.0F;
      i = 900000;
      break;
      f = 848.0F;
      i = 1100000;
      break;
      f /= this.originalHeight;
    }
  }

  public void closePhoto(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((!paramBoolean2) && (this.currentEditMode != 0))
      if ((this.currentEditMode == 3) && (this.photoPaintView != null))
        this.photoPaintView.maybeShowDismissalAlert(this, this.parentActivity, new Runnable()
        {
          public void run()
          {
            PhotoViewer.this.switchToEditMode(0);
          }
        });
    while (true)
    {
      return;
      if (this.currentEditMode == 1)
        this.photoCropView.cancelAnimationRunnable();
      switchToEditMode(0);
      return;
      if ((Build.VERSION.SDK_INT >= 16) && (this.qualityChooseView != null) && (this.qualityChooseView.getTag() != null))
      {
        this.qualityPicker.cancelButton.callOnClick();
        return;
      }
      try
      {
        if (this.visibleDialog != null)
        {
          this.visibleDialog.dismiss();
          this.visibleDialog = null;
        }
        if (this.currentEditMode != 0)
        {
          if (this.currentEditMode == 2)
          {
            this.photoFilterView.shutdown();
            this.containerView.removeView(this.photoFilterView);
            this.photoFilterView = null;
            this.currentEditMode = 0;
          }
        }
        else
        {
          if ((this.parentActivity == null) || (!this.isVisible) || (checkAnimation()) || (this.placeProvider == null) || ((this.captionEditText.hideActionMode()) && (!paramBoolean2)))
            continue;
          releasePlayer();
          this.captionEditText.onDestroy();
          this.parentChatActivity = null;
          NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
          NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
          NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileLoadProgressChanged);
          NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaCountDidLoaded);
          NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaDidLoaded);
          NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogPhotosLoaded);
          NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
          NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FilePreparingFailed);
          NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileNewChunkAvailable);
          ConnectionsManager.getInstance().cancelRequestsForGuid(this.classGuid);
          this.isActionBarVisible = false;
          if (this.velocityTracker != null)
          {
            this.velocityTracker.recycle();
            this.velocityTracker = null;
          }
          ConnectionsManager.getInstance().cancelRequestsForGuid(this.classGuid);
          localPlaceProviderObject = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
          if (!paramBoolean1)
            break label1734;
          this.animationInProgress = 1;
          this.animatingImageView.setVisibility(0);
          this.containerView.invalidate();
          localAnimatorSet = new AnimatorSet();
          localObject3 = this.animatingImageView.getLayoutParams();
          j = this.centerImage.getOrientation();
          int k = 0;
          i = k;
          if (localPlaceProviderObject != null)
          {
            i = k;
            if (localPlaceProviderObject.imageReceiver != null)
              i = localPlaceProviderObject.imageReceiver.getAnimatedOrientation();
          }
          if (i == 0)
            break label1909;
          this.animatingImageView.setOrientation(i);
          if (localPlaceProviderObject == null)
            break label1492;
          Object localObject1 = this.animatingImageView;
          if (localPlaceProviderObject.radius == 0)
            break label1487;
          paramBoolean1 = true;
          ((ClippingImageView)localObject1).setNeedRadius(paramBoolean1);
          localObject1 = localPlaceProviderObject.imageReceiver.getDrawRegion();
          ((ViewGroup.LayoutParams)localObject3).width = (((Rect)localObject1).right - ((Rect)localObject1).left);
          ((ViewGroup.LayoutParams)localObject3).height = (((Rect)localObject1).bottom - ((Rect)localObject1).top);
          this.animatingImageView.setImageBitmap(localPlaceProviderObject.thumb);
          this.animatingImageView.setLayoutParams((ViewGroup.LayoutParams)localObject3);
          f1 = AndroidUtilities.displaySize.x / ((ViewGroup.LayoutParams)localObject3).width;
          j = AndroidUtilities.displaySize.y;
          if (Build.VERSION.SDK_INT < 21)
            break label1544;
          i = AndroidUtilities.statusBarHeight;
          float f2 = (i + j) / ((ViewGroup.LayoutParams)localObject3).height;
          if (f1 <= f2)
            break label1550;
          f1 = f2;
          float f4 = ((ViewGroup.LayoutParams)localObject3).width;
          float f5 = this.scale;
          f2 = ((ViewGroup.LayoutParams)localObject3).height;
          float f3 = this.scale;
          f4 = (AndroidUtilities.displaySize.x - f4 * f5 * f1) / 2.0F;
          j = AndroidUtilities.displaySize.y;
          if (Build.VERSION.SDK_INT < 21)
            break label1553;
          i = AndroidUtilities.statusBarHeight;
          f2 = (i + j - f2 * f3 * f1) / 2.0F;
          this.animatingImageView.setTranslationX(f4 + this.translationX);
          this.animatingImageView.setTranslationY(f2 + this.translationY);
          this.animatingImageView.setScaleX(this.scale * f1);
          this.animatingImageView.setScaleY(f1 * this.scale);
          if (localPlaceProviderObject == null)
            break label1575;
          localPlaceProviderObject.imageReceiver.setVisible(false, true);
          int m = Math.abs(((Rect)localObject1).left - localPlaceProviderObject.imageReceiver.getImageX());
          int n = Math.abs(((Rect)localObject1).top - localPlaceProviderObject.imageReceiver.getImageY());
          localObject3 = new int[2];
          localPlaceProviderObject.parentView.getLocationInWindow(localObject3);
          j = localObject3[1];
          if (Build.VERSION.SDK_INT < 21)
            break label1559;
          i = 0;
          j = j - i - (localPlaceProviderObject.viewY + ((Rect)localObject1).top) + localPlaceProviderObject.clipTopAddition;
          i = j;
          if (j < 0)
            i = 0;
          k = localPlaceProviderObject.viewY;
          int i1 = ((Rect)localObject1).top;
          int i2 = ((Rect)localObject1).bottom;
          int i3 = ((Rect)localObject1).top;
          int i4 = localObject3[1];
          int i5 = localPlaceProviderObject.parentView.getHeight();
          if (Build.VERSION.SDK_INT < 21)
            break label1567;
          j = 0;
          k = k + i1 + (i2 - i3) - (i5 + i4 - j) + localPlaceProviderObject.clipBottomAddition;
          j = k;
          if (k < 0)
            j = 0;
          i = Math.max(i, n);
          j = Math.max(j, n);
          this.animationValues[0][0] = this.animatingImageView.getScaleX();
          this.animationValues[0][1] = this.animatingImageView.getScaleY();
          this.animationValues[0][2] = this.animatingImageView.getTranslationX();
          this.animationValues[0][3] = this.animatingImageView.getTranslationY();
          this.animationValues[0][4] = 0;
          this.animationValues[0][5] = 0;
          this.animationValues[0][6] = 0;
          this.animationValues[0][7] = 0;
          this.animationValues[1][0] = localPlaceProviderObject.scale;
          this.animationValues[1][1] = localPlaceProviderObject.scale;
          this.animationValues[1][2] = (localPlaceProviderObject.viewX + ((Rect)localObject1).left * localPlaceProviderObject.scale);
          localObject3 = this.animationValues[1];
          f1 = localPlaceProviderObject.viewY;
          localObject3[3] = (((Rect)localObject1).top * localPlaceProviderObject.scale + f1);
          this.animationValues[1][4] = (m * localPlaceProviderObject.scale);
          this.animationValues[1][5] = (i * localPlaceProviderObject.scale);
          this.animationValues[1][6] = (j * localPlaceProviderObject.scale);
          this.animationValues[1][7] = localPlaceProviderObject.radius;
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[] { 0 }), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F }) });
          this.animationEndRunnable = new Runnable(localPlaceProviderObject)
          {
            public void run()
            {
              if (Build.VERSION.SDK_INT >= 18)
                PhotoViewer.this.containerView.setLayerType(0, null);
              PhotoViewer.access$12702(PhotoViewer.this, 0);
              PhotoViewer.this.onPhotoClosed(this.val$object);
            }
          };
          localAnimatorSet.setDuration(200L);
          localAnimatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (PhotoViewer.this.animationEndRunnable != null)
                  {
                    PhotoViewer.this.animationEndRunnable.run();
                    PhotoViewer.access$13302(PhotoViewer.this, null);
                  }
                }
              });
            }
          });
          this.transitionAnimationStartTime = System.currentTimeMillis();
          if (Build.VERSION.SDK_INT >= 18)
            this.containerView.setLayerType(2, null);
          localAnimatorSet.start();
          if (this.currentAnimation != null)
          {
            this.currentAnimation.setSecondParentView(null);
            this.currentAnimation = null;
            this.centerImage.setImageBitmap((Drawable)null);
          }
          if (!(this.placeProvider instanceof EmptyPhotoViewerProvider))
            continue;
          this.placeProvider.cancelButtonPressed();
          return;
        }
      }
      catch (Exception localObject2)
      {
        while (true)
        {
          PlaceProviderObject localPlaceProviderObject;
          AnimatorSet localAnimatorSet;
          Object localObject3;
          float f1;
          FileLog.e(localException);
          continue;
          if (this.currentEditMode != 1)
            continue;
          this.editorDoneLayout.setVisibility(8);
          this.photoCropView.setVisibility(8);
          continue;
          label1487: paramBoolean1 = false;
          continue;
          label1492: this.animatingImageView.setNeedRadius(false);
          ((ViewGroup.LayoutParams)localObject3).width = this.centerImage.getImageWidth();
          ((ViewGroup.LayoutParams)localObject3).height = this.centerImage.getImageHeight();
          this.animatingImageView.setImageBitmap(this.centerImage.getBitmap());
          Object localObject2 = null;
          continue;
          label1544: int i = 0;
          continue;
          label1550: continue;
          label1553: i = 0;
          continue;
          label1559: i = AndroidUtilities.statusBarHeight;
          continue;
          label1567: int j = AndroidUtilities.statusBarHeight;
          continue;
          label1575: j = AndroidUtilities.displaySize.y;
          label1596: ClippingImageView localClippingImageView;
          if (Build.VERSION.SDK_INT >= 21)
          {
            i = AndroidUtilities.statusBarHeight;
            i += j;
            localObject2 = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[] { 0 });
            localObject3 = ObjectAnimator.ofFloat(this.animatingImageView, "alpha", new float[] { 0.0F });
            localClippingImageView = this.animatingImageView;
            if (this.translationY < 0.0F)
              break label1726;
            f1 = i;
          }
          while (true)
          {
            localAnimatorSet.playTogether(new Animator[] { localObject2, localObject3, ObjectAnimator.ofFloat(localClippingImageView, "translationY", new float[] { f1 }), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F }) });
            break;
            i = 0;
            break label1596;
            label1726: f1 = -i;
          }
          label1734: localObject2 = new AnimatorSet();
          ((AnimatorSet)localObject2).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.containerView, "scaleX", new float[] { 0.9F }), ObjectAnimator.ofFloat(this.containerView, "scaleY", new float[] { 0.9F }), ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[] { 0 }), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F }) });
          this.animationInProgress = 2;
          this.animationEndRunnable = new Runnable(localPlaceProviderObject)
          {
            public void run()
            {
              if (PhotoViewer.this.containerView == null)
                return;
              if (Build.VERSION.SDK_INT >= 18)
                PhotoViewer.this.containerView.setLayerType(0, null);
              PhotoViewer.access$12702(PhotoViewer.this, 0);
              PhotoViewer.this.onPhotoClosed(this.val$object);
              PhotoViewer.this.containerView.setScaleX(1.0F);
              PhotoViewer.this.containerView.setScaleY(1.0F);
            }
          };
          ((AnimatorSet)localObject2).setDuration(200L);
          ((AnimatorSet)localObject2).addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              if (PhotoViewer.this.animationEndRunnable != null)
              {
                PhotoViewer.this.animationEndRunnable.run();
                PhotoViewer.access$13302(PhotoViewer.this, null);
              }
            }
          });
          this.transitionAnimationStartTime = System.currentTimeMillis();
          if (Build.VERSION.SDK_INT >= 18)
            this.containerView.setLayerType(2, null);
          ((AnimatorSet)localObject2).start();
          continue;
          label1909: i = j;
        }
      }
    }
  }

  public void destroyPhotoViewer()
  {
    if ((this.parentActivity == null) || (this.windowView == null))
      return;
    releasePlayer();
    this.playAdvertising = false;
    this.parentActivity.setRequestedOrientation(2);
    if ((this.containerView != null) && (this.videoUrl != null))
      this.containerView.removeView(this.videoUrl);
    try
    {
      if (this.windowView.getParent() != null)
        ((WindowManager)this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
      this.windowView = null;
      if (this.captionEditText != null)
        this.captionEditText.onDestroy();
      Instance = null;
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.FileDidFailedLoad)
    {
      paramArrayOfObject = (String)paramArrayOfObject[0];
      paramInt = 0;
      if (paramInt < 3)
      {
        if ((this.currentFileNames[paramInt] == null) || (!this.currentFileNames[paramInt].equals(paramArrayOfObject)))
          break label61;
        this.photoProgressViews[paramInt].setProgress(1.0F, true);
        checkProgress(paramInt, true);
      }
    }
    label61: label204: Object localObject1;
    label596: label981: label1265: label1790: 
    do
    {
      do
      {
        do
        {
          do
          {
            int i;
            do
            {
              Object localObject2;
              int j;
              int k;
              long l;
              do
              {
                do
                {
                  do
                  {
                    while (true)
                    {
                      return;
                      paramInt += 1;
                      break;
                      if (paramInt == NotificationCenter.FileDidLoaded)
                      {
                        paramArrayOfObject = (String)paramArrayOfObject[0];
                        paramInt = 0;
                        while (true)
                        {
                          if (paramInt >= 3)
                            break label204;
                          if ((this.currentFileNames[paramInt] != null) && (this.currentFileNames[paramInt].equals(paramArrayOfObject)))
                          {
                            this.photoProgressViews[paramInt].setProgress(1.0F, true);
                            checkProgress(paramInt, true);
                            if ((Build.VERSION.SDK_INT < 16) || (paramInt != 0) || (((this.currentMessageObject == null) || (!this.currentMessageObject.isVideo())) && ((this.currentBotInlineResult == null) || ((!this.currentBotInlineResult.type.equals("video")) && (!MessageObject.isVideoDocument(this.currentBotInlineResult.document))))))
                              break;
                            onActionClick(false);
                            return;
                          }
                          paramInt += 1;
                        }
                        continue;
                      }
                      if (paramInt == NotificationCenter.FileLoadProgressChanged)
                      {
                        localObject1 = (String)paramArrayOfObject[0];
                        paramInt = 0;
                        while (paramInt < 3)
                        {
                          if ((this.currentFileNames[paramInt] != null) && (this.currentFileNames[paramInt].equals(localObject1)))
                          {
                            localObject2 = (Float)paramArrayOfObject[1];
                            this.photoProgressViews[paramInt].setProgress(((Float)localObject2).floatValue(), true);
                          }
                          paramInt += 1;
                        }
                        continue;
                      }
                      if (paramInt != NotificationCenter.dialogPhotosLoaded)
                        break label721;
                      paramInt = ((Integer)paramArrayOfObject[4]).intValue();
                      i = ((Integer)paramArrayOfObject[0]).intValue();
                      if ((this.avatarsDialogId != i) || (this.classGuid != paramInt))
                        continue;
                      bool = ((Boolean)paramArrayOfObject[3]).booleanValue();
                      paramArrayOfObject = (ArrayList)paramArrayOfObject[5];
                      if (paramArrayOfObject.isEmpty())
                        continue;
                      this.imagesArrLocations.clear();
                      this.imagesArrLocationsSizes.clear();
                      this.avatarsArr.clear();
                      j = 0;
                      paramInt = -1;
                      while (true)
                      {
                        if (j >= paramArrayOfObject.size())
                          break label596;
                        localObject1 = (TLRPC.Photo)paramArrayOfObject.get(j);
                        i = paramInt;
                        if (localObject1 != null)
                        {
                          i = paramInt;
                          if (!(localObject1 instanceof TLRPC.TL_photoEmpty))
                          {
                            if (((TLRPC.Photo)localObject1).sizes == null)
                            {
                              j += 1;
                              continue;
                            }
                            localObject2 = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo)localObject1).sizes, 640);
                            i = paramInt;
                            if (localObject2 != null)
                            {
                              i = paramInt;
                              if (paramInt != -1)
                                break;
                              i = paramInt;
                              if (this.currentFileLocation == null)
                                break;
                              k = 0;
                            }
                          }
                        }
                      }
                      while (true)
                      {
                        i = paramInt;
                        if (k < ((TLRPC.Photo)localObject1).sizes.size())
                        {
                          TLRPC.PhotoSize localPhotoSize = (TLRPC.PhotoSize)((TLRPC.Photo)localObject1).sizes.get(k);
                          if ((localPhotoSize.location.local_id == this.currentFileLocation.local_id) && (localPhotoSize.location.volume_id == this.currentFileLocation.volume_id))
                            i = this.imagesArrLocations.size();
                        }
                        else
                        {
                          this.imagesArrLocations.add(((TLRPC.PhotoSize)localObject2).location);
                          this.imagesArrLocationsSizes.add(Integer.valueOf(((TLRPC.PhotoSize)localObject2).size));
                          this.avatarsArr.add(localObject1);
                          paramInt = i;
                          break;
                        }
                        k += 1;
                      }
                      if (!this.avatarsArr.isEmpty())
                      {
                        this.menuItem.showSubItem(6);
                        this.needSearchImageInArr = false;
                        this.currentIndex = -1;
                        if (paramInt == -1)
                          break label673;
                        setImageIndex(paramInt, true);
                      }
                      while (true)
                      {
                        if (!bool)
                          break label719;
                        MessagesController.getInstance().loadDialogPhotos(this.avatarsDialogId, 0, 80, 0L, false, this.classGuid);
                        return;
                        this.menuItem.hideSubItem(6);
                        break;
                        this.avatarsArr.add(0, new TLRPC.TL_photoEmpty());
                        this.imagesArrLocations.add(0, this.currentFileLocation);
                        this.imagesArrLocationsSizes.add(0, Integer.valueOf(0));
                        setImageIndex(0, true);
                      }
                    }
                    if (paramInt != NotificationCenter.mediaCountDidLoaded)
                      break label981;
                    l = ((Long)paramArrayOfObject[0]).longValue();
                  }
                  while ((l != this.currentDialogId) && (l != this.mergeDialogId));
                  if (l == this.currentDialogId)
                    this.totalImagesCount = ((Integer)paramArrayOfObject[1]).intValue();
                  while ((this.needSearchImageInArr) && (this.isFirstLoading))
                  {
                    this.isFirstLoading = false;
                    this.loadingMoreImages = true;
                    SharedMediaQuery.loadMedia(this.currentDialogId, 0, 80, 0, 0, true, this.classGuid);
                    return;
                    if (l != this.mergeDialogId)
                      continue;
                    this.totalImagesCountMerge = ((Integer)paramArrayOfObject[1]).intValue();
                  }
                }
                while (this.imagesArr.isEmpty());
                if (this.opennedFromMedia)
                {
                  this.actionBar.setTitle(LocaleController.formatString("Of", 2131166154, new Object[] { Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge) }));
                  return;
                }
                this.actionBar.setTitle(LocaleController.formatString("Of", 2131166154, new Object[] { Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge - this.imagesArr.size() + this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge) }));
                return;
                if (paramInt != NotificationCenter.mediaDidLoaded)
                  break label2015;
                l = ((Long)paramArrayOfObject[0]).longValue();
                paramInt = ((Integer)paramArrayOfObject[3]).intValue();
              }
              while (((l != this.currentDialogId) && (l != this.mergeDialogId)) || (paramInt != this.classGuid));
              this.loadingMoreImages = false;
              if (l == this.currentDialogId)
                i = 0;
              while (true)
              {
                localObject1 = (ArrayList)paramArrayOfObject[2];
                this.endReached[i] = ((Boolean)paramArrayOfObject[5]).booleanValue();
                if (!this.needSearchImageInArr)
                  break;
                if ((((ArrayList)localObject1).isEmpty()) && ((i != 0) || (this.mergeDialogId == 0L)))
                {
                  this.needSearchImageInArr = false;
                  return;
                  i = 1;
                  continue;
                }
                paramArrayOfObject = (MessageObject)this.imagesArr.get(this.currentIndex);
                paramInt = -1;
                j = 0;
                int n = 0;
                if (n < ((ArrayList)localObject1).size())
                {
                  localObject2 = (MessageObject)((ArrayList)localObject1).get(n);
                  k = j;
                  int m = paramInt;
                  if (!this.imagesByIdsTemp[i].containsKey(Integer.valueOf(((MessageObject)localObject2).getId())))
                  {
                    this.imagesByIdsTemp[i].put(Integer.valueOf(((MessageObject)localObject2).getId()), localObject2);
                    if (!this.opennedFromMedia)
                      break label1265;
                    this.imagesArrTemp.add(localObject2);
                    if (((MessageObject)localObject2).getId() == paramArrayOfObject.getId())
                      paramInt = j;
                    k = j + 1;
                    m = paramInt;
                  }
                  while (true)
                  {
                    n += 1;
                    j = k;
                    paramInt = m;
                    break;
                    j += 1;
                    this.imagesArrTemp.add(0, localObject2);
                    k = j;
                    m = paramInt;
                    if (((MessageObject)localObject2).getId() != paramArrayOfObject.getId())
                      continue;
                    m = ((ArrayList)localObject1).size() - j;
                    k = j;
                  }
                }
                if ((j == 0) && ((i != 0) || (this.mergeDialogId == 0L)))
                {
                  this.totalImagesCount = this.imagesArr.size();
                  this.totalImagesCountMerge = 0;
                }
                if (paramInt != -1)
                {
                  this.imagesArr.clear();
                  this.imagesArr.addAll(this.imagesArrTemp);
                  i = 0;
                  while (i < 2)
                  {
                    this.imagesByIds[i].clear();
                    this.imagesByIds[i].putAll(this.imagesByIdsTemp[i]);
                    this.imagesByIdsTemp[i].clear();
                    i += 1;
                  }
                  this.imagesArrTemp.clear();
                  this.needSearchImageInArr = false;
                  this.currentIndex = -1;
                  i = paramInt;
                  if (paramInt >= this.imagesArr.size())
                    i = this.imagesArr.size() - 1;
                  setImageIndex(i, true);
                  return;
                }
                if (this.opennedFromMedia)
                  if (this.imagesArrTemp.isEmpty())
                  {
                    k = 0;
                    j = i;
                    paramInt = k;
                    if (i == 0)
                    {
                      j = i;
                      paramInt = k;
                      if (this.endReached[i] != 0)
                      {
                        j = i;
                        paramInt = k;
                        if (this.mergeDialogId != 0L)
                        {
                          i = 1;
                          j = i;
                          paramInt = k;
                          if (!this.imagesArrTemp.isEmpty())
                          {
                            j = i;
                            paramInt = k;
                            if (((MessageObject)this.imagesArrTemp.get(this.imagesArrTemp.size() - 1)).getDialogId() != this.mergeDialogId)
                            {
                              paramInt = 0;
                              j = i;
                            }
                          }
                        }
                      }
                    }
                    if (this.endReached[j] != 0)
                      break label1769;
                    this.loadingMoreImages = true;
                    if (!this.opennedFromMedia)
                      break label1799;
                    if (j != 0)
                      break label1790;
                    l = this.currentDialogId;
                  }
                while (true)
                {
                  SharedMediaQuery.loadMedia(l, 0, 80, paramInt, 0, true, this.classGuid);
                  return;
                  k = ((MessageObject)this.imagesArrTemp.get(this.imagesArrTemp.size() - 1)).getId();
                  break label1489;
                  if (this.imagesArrTemp.isEmpty());
                  for (k = 0; ; k = ((MessageObject)this.imagesArrTemp.get(0)).getId())
                  {
                    j = i;
                    paramInt = k;
                    if (i != 0)
                      break label1588;
                    j = i;
                    paramInt = k;
                    if (this.endReached[i] == 0)
                      break label1588;
                    j = i;
                    paramInt = k;
                    if (this.mergeDialogId == 0L)
                      break label1588;
                    i = 1;
                    j = i;
                    paramInt = k;
                    if (this.imagesArrTemp.isEmpty())
                      break label1588;
                    j = i;
                    paramInt = k;
                    if (((MessageObject)this.imagesArrTemp.get(0)).getDialogId() == this.mergeDialogId)
                      break label1588;
                    paramInt = 0;
                    j = i;
                    break label1588;
                    break;
                  }
                  l = this.mergeDialogId;
                }
                if (j == 0)
                  l = this.currentDialogId;
                while (true)
                {
                  SharedMediaQuery.loadMedia(l, 0, 80, paramInt, 0, true, this.classGuid);
                  return;
                  l = this.mergeDialogId;
                }
              }
              paramInt = 0;
              paramArrayOfObject = ((ArrayList)localObject1).iterator();
              if (paramArrayOfObject.hasNext())
              {
                localObject1 = (MessageObject)paramArrayOfObject.next();
                j = paramInt;
                if (!this.imagesByIds[i].containsKey(Integer.valueOf(((MessageObject)localObject1).getId())))
                {
                  j = paramInt + 1;
                  if (!this.opennedFromMedia)
                    break label1934;
                  this.imagesArr.add(localObject1);
                }
                while (true)
                {
                  this.imagesByIds[i].put(Integer.valueOf(((MessageObject)localObject1).getId()), localObject1);
                  paramInt = j;
                  break;
                  this.imagesArr.add(0, localObject1);
                }
              }
              if (!this.opennedFromMedia)
                break label1975;
            }
            while (paramInt != 0);
            this.totalImagesCount = this.imagesArr.size();
            this.totalImagesCountMerge = 0;
            return;
            if (paramInt != 0)
            {
              i = this.currentIndex;
              this.currentIndex = -1;
              setImageIndex(i + paramInt, true);
              return;
            }
            this.totalImagesCount = this.imagesArr.size();
            this.totalImagesCountMerge = 0;
            return;
            if (paramInt != NotificationCenter.emojiDidLoaded)
              break label2037;
          }
          while (this.captionTextView == null);
          this.captionTextView.invalidate();
          return;
          if (paramInt != NotificationCenter.FilePreparingFailed)
            break label2156;
          paramArrayOfObject = (MessageObject)paramArrayOfObject[0];
          if (this.loadInitialVideo)
          {
            this.loadInitialVideo = false;
            this.progressView.setVisibility(4);
            paramArrayOfObject = e.a(ApplicationLoader.applicationContext).c();
            preparePlayer(this.currentPlayingVideoFile, false, false, paramArrayOfObject);
            return;
          }
          if (!this.tryStartRequestPreviewOnFinish)
            continue;
          releasePlayer();
          if (!MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true));
          for (boolean bool = true; ; bool = false)
          {
            this.tryStartRequestPreviewOnFinish = bool;
            return;
          }
        }
        while (paramArrayOfObject != this.videoPreviewMessageObject);
        this.requestingPreview = false;
        this.progressView.setVisibility(4);
        return;
      }
      while ((paramInt != NotificationCenter.FileNewChunkAvailable) || ((MessageObject)paramArrayOfObject[0] != this.videoPreviewMessageObject));
      localObject1 = (String)paramArrayOfObject[1];
    }
    while (((Long)paramArrayOfObject[2]).longValue() == 0L);
    label673: label719: label721: label1489: label1769: label2037: this.requestingPreview = false;
    label1588: label1975: label2015: label2156: this.progressView.setVisibility(4);
    label1799: label1934: paramArrayOfObject = e.a(ApplicationLoader.applicationContext).c();
    preparePlayer(new File((String)localObject1), false, true, paramArrayOfObject);
  }

  public float getAnimationValue()
  {
    return this.animationValue;
  }

  public boolean isMuteVideo()
  {
    return this.muteVideo;
  }

  public boolean isShowingImage(String paramString)
  {
    return (this.isVisible) && (!this.disableShowCheck) && (paramString != null) && (this.currentPathObject != null) && (paramString.equals(this.currentPathObject));
  }

  public boolean isShowingImage(MessageObject paramMessageObject)
  {
    return (this.isVisible) && (!this.disableShowCheck) && (paramMessageObject != null) && (this.currentMessageObject != null) && (this.currentMessageObject.getId() == paramMessageObject.getId());
  }

  public boolean isShowingImage(TLRPC.FileLocation paramFileLocation)
  {
    return (this.isVisible) && (!this.disableShowCheck) && (paramFileLocation != null) && (this.currentFileLocation != null) && (paramFileLocation.local_id == this.currentFileLocation.local_id) && (paramFileLocation.volume_id == this.currentFileLocation.volume_id) && (paramFileLocation.dc_id == this.currentFileLocation.dc_id);
  }

  public boolean isVisible()
  {
    return (this.isVisible) && (this.placeProvider != null);
  }

  public boolean onDoubleTap(MotionEvent paramMotionEvent)
  {
    if ((!this.canZoom) || ((this.scale == 1.0F) && ((this.translationY != 0.0F) || (this.translationX != 0.0F))));
    do
      return false;
    while ((this.animationStartTime != 0L) || (this.animationInProgress != 0));
    float f2;
    float f3;
    float f1;
    if (this.scale == 1.0F)
    {
      f2 = paramMotionEvent.getX() - getContainerViewWidth() / 2 - (paramMotionEvent.getX() - getContainerViewWidth() / 2 - this.translationX) * (3.0F / this.scale);
      f3 = paramMotionEvent.getY() - getContainerViewHeight() / 2 - (paramMotionEvent.getY() - getContainerViewHeight() / 2 - this.translationY) * (3.0F / this.scale);
      updateMinMax(3.0F);
      if (f2 < this.minX)
      {
        f1 = this.minX;
        if (f3 >= this.minY)
          break label214;
        f2 = this.minY;
        label178: animateTo(3.0F, f1, f2, true);
      }
    }
    while (true)
    {
      this.doubleTap = true;
      return true;
      f1 = f2;
      if (f2 <= this.maxX)
        break;
      f1 = this.maxX;
      break;
      label214: f2 = f3;
      if (f3 <= this.maxY)
        break label178;
      f2 = this.maxY;
      break label178;
      animateTo(1.0F, 0.0F, 0.0F, true);
    }
  }

  public boolean onDoubleTapEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }

  public boolean onDown(MotionEvent paramMotionEvent)
  {
    return false;
  }

  public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    if (this.scale != 1.0F)
    {
      this.scroller.abortAnimation();
      this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(paramFloat1), Math.round(paramFloat2), (int)this.minX, (int)this.maxX, (int)this.minY, (int)this.maxY);
      this.containerView.postInvalidate();
    }
    return false;
  }

  public void onLongPress(MotionEvent paramMotionEvent)
  {
  }

  public void onPause()
  {
    if (this.currentAnimation != null)
      closePhoto(false, false);
    do
      return;
    while (this.lastTitle == null);
    closeCaptionEnter(true);
  }

  public void onResume()
  {
    redraw(0);
    if (this.videoPlayer != null)
      this.videoPlayer.seekTo(this.videoPlayer.getCurrentPosition() + 1L);
  }

  public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    return false;
  }

  public void onShowPress(MotionEvent paramMotionEvent)
  {
  }

  public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent)
  {
    boolean bool1 = false;
    boolean bool2 = true;
    if (this.discardTap)
      bool1 = false;
    float f2;
    do
    {
      do
      {
        float f1;
        do
        {
          do
          {
            int i;
            do
            {
              do
              {
                do
                {
                  do
                  {
                    return bool1;
                    if (this.canShowBottom)
                    {
                      if ((Build.VERSION.SDK_INT >= 16) && (this.aspectRatioFrameLayout != null) && (this.aspectRatioFrameLayout.getVisibility() == 0));
                      for (i = 1; (this.photoProgressViews[0] != null) && (this.containerView != null) && (i == 0); i = 0)
                      {
                        i = this.photoProgressViews[0].backgroundState;
                        if ((i <= 0) || (i > 3))
                          break;
                        f1 = paramMotionEvent.getX();
                        f2 = paramMotionEvent.getY();
                        if ((f1 < (getContainerViewWidth() - AndroidUtilities.dp(100.0F)) / 2.0F) || (f1 > (getContainerViewWidth() + AndroidUtilities.dp(100.0F)) / 2.0F) || (f2 < (getContainerViewHeight() - AndroidUtilities.dp(100.0F)) / 2.0F) || (f2 > (getContainerViewHeight() + AndroidUtilities.dp(100.0F)) / 2.0F))
                          break;
                        onActionClick(true);
                        checkProgress(0, true);
                        return true;
                      }
                      if (!this.isActionBarVisible)
                        bool1 = true;
                      toggleActionBar(bool1, true);
                      return true;
                    }
                    if (this.sendPhotoType == 0)
                    {
                      this.checkImageView.performClick();
                      return true;
                    }
                    bool1 = bool2;
                  }
                  while (this.currentBotInlineResult == null);
                  if (this.currentBotInlineResult.type.equals("video"))
                    break;
                  bool1 = bool2;
                }
                while (!MessageObject.isVideoDocument(this.currentBotInlineResult.document));
                i = this.photoProgressViews[0].backgroundState;
                bool1 = bool2;
              }
              while (i <= 0);
              bool1 = bool2;
            }
            while (i > 3);
            f1 = paramMotionEvent.getX();
            f2 = paramMotionEvent.getY();
            bool1 = bool2;
          }
          while (f1 < (getContainerViewWidth() - AndroidUtilities.dp(100.0F)) / 2.0F);
          bool1 = bool2;
        }
        while (f1 > (getContainerViewWidth() + AndroidUtilities.dp(100.0F)) / 2.0F);
        bool1 = bool2;
      }
      while (f2 < (getContainerViewHeight() - AndroidUtilities.dp(100.0F)) / 2.0F);
      bool1 = bool2;
    }
    while (f2 > (getContainerViewHeight() + AndroidUtilities.dp(100.0F)) / 2.0F);
    onActionClick(true);
    checkProgress(0, true);
    return true;
  }

  public boolean onSingleTapUp(MotionEvent paramMotionEvent)
  {
    return false;
  }

  public boolean openPhoto(ArrayList<MessageObject> paramArrayList, int paramInt, long paramLong1, long paramLong2, PhotoViewerProvider paramPhotoViewerProvider)
  {
    return openPhoto((MessageObject)paramArrayList.get(paramInt), null, paramArrayList, null, paramInt, paramPhotoViewerProvider, null, paramLong1, paramLong2);
  }

  public boolean openPhoto(MessageObject paramMessageObject, long paramLong1, long paramLong2, PhotoViewerProvider paramPhotoViewerProvider)
  {
    return openPhoto(paramMessageObject, null, null, null, 0, paramPhotoViewerProvider, null, paramLong1, paramLong2);
  }

  public boolean openPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, ArrayList<MessageObject> paramArrayList, ArrayList<Object> paramArrayList1, int paramInt, PhotoViewerProvider paramPhotoViewerProvider, ChatActivity paramChatActivity, long paramLong1, long paramLong2)
  {
    if ((this.parentActivity == null) || (this.isVisible) || ((paramPhotoViewerProvider == null) && (checkAnimation())) || ((paramMessageObject == null) && (paramFileLocation == null) && (paramArrayList == null) && (paramArrayList1 == null)))
      return false;
    PlaceProviderObject localPlaceProviderObject = paramPhotoViewerProvider.getPlaceForPhoto(paramMessageObject, paramFileLocation, paramInt);
    if ((localPlaceProviderObject == null) && (paramArrayList1 == null))
      return false;
    this.lastInsets = null;
    WindowManager localWindowManager = (WindowManager)this.parentActivity.getSystemService("window");
    if (this.attachedToWindow);
    try
    {
      localWindowManager.removeView(this.windowView);
      while (true)
      {
        try
        {
          this.windowLayoutParams.type = 99;
          if (Build.VERSION.SDK_INT < 21)
            continue;
          this.windowLayoutParams.flags = -2147417848;
          this.windowLayoutParams.softInputMode = 272;
          this.windowView.setFocusable(false);
          this.containerView.setFocusable(false);
          localWindowManager.addView(this.windowView, this.windowLayoutParams);
          this.doneButtonPressed = false;
          this.parentChatActivity = paramChatActivity;
          this.actionBar.setTitle(LocaleController.formatString("Of", 2131166154, new Object[] { Integer.valueOf(1), Integer.valueOf(1) }));
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileLoadProgressChanged);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaCountDidLoaded);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaDidLoaded);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogPhotosLoaded);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingFailed);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
          this.placeProvider = paramPhotoViewerProvider;
          this.mergeDialogId = paramLong2;
          this.currentDialogId = paramLong1;
          if (this.velocityTracker != null)
            continue;
          this.velocityTracker = VelocityTracker.obtain();
          this.isVisible = true;
          toggleActionBar(true, false);
          if (localPlaceProviderObject == null)
            break;
          this.disableShowCheck = true;
          this.animationInProgress = 1;
          if (paramMessageObject == null)
            continue;
          this.currentAnimation = localPlaceProviderObject.imageReceiver.getAnimation();
          onPhotoShow(paramMessageObject, paramFileLocation, paramArrayList, paramArrayList1, paramInt, localPlaceProviderObject);
          paramMessageObject = localPlaceProviderObject.imageReceiver.getDrawRegion();
          paramInt = localPlaceProviderObject.imageReceiver.getOrientation();
          i = localPlaceProviderObject.imageReceiver.getAnimatedOrientation();
          if (i == 0)
            break label1559;
          paramInt = i;
          this.animatingImageView.setVisibility(0);
          this.animatingImageView.setRadius(localPlaceProviderObject.radius);
          this.animatingImageView.setOrientation(paramInt);
          paramFileLocation = this.animatingImageView;
          if (localPlaceProviderObject.radius != 0)
          {
            bool = true;
            paramFileLocation.setNeedRadius(bool);
            this.animatingImageView.setImageBitmap(localPlaceProviderObject.thumb);
            this.animatingImageView.setAlpha(1.0F);
            this.animatingImageView.setPivotX(0.0F);
            this.animatingImageView.setPivotY(0.0F);
            this.animatingImageView.setScaleX(localPlaceProviderObject.scale);
            this.animatingImageView.setScaleY(localPlaceProviderObject.scale);
            this.animatingImageView.setTranslationX(localPlaceProviderObject.viewX + paramMessageObject.left * localPlaceProviderObject.scale);
            this.animatingImageView.setTranslationY(localPlaceProviderObject.viewY + paramMessageObject.top * localPlaceProviderObject.scale);
            paramFileLocation = this.animatingImageView.getLayoutParams();
            paramFileLocation.width = (paramMessageObject.right - paramMessageObject.left);
            paramFileLocation.height = (paramMessageObject.bottom - paramMessageObject.top);
            this.animatingImageView.setLayoutParams(paramFileLocation);
            float f1 = AndroidUtilities.displaySize.x / paramFileLocation.width;
            i = AndroidUtilities.displaySize.y;
            if (Build.VERSION.SDK_INT < 21)
              break label1406;
            paramInt = AndroidUtilities.statusBarHeight;
            float f2 = (paramInt + i) / paramFileLocation.height;
            if (f1 <= f2)
              break label1412;
            f1 = f2;
            float f3 = paramFileLocation.width;
            f2 = paramFileLocation.height;
            f3 = (AndroidUtilities.displaySize.x - f3 * f1) / 2.0F;
            i = AndroidUtilities.displaySize.y;
            if (Build.VERSION.SDK_INT < 21)
              break label1415;
            paramInt = AndroidUtilities.statusBarHeight;
            f2 = (paramInt + i - f2 * f1) / 2.0F;
            int k = Math.abs(paramMessageObject.left - localPlaceProviderObject.imageReceiver.getImageX());
            int m = Math.abs(paramMessageObject.top - localPlaceProviderObject.imageReceiver.getImageY());
            paramArrayList = new int[2];
            localPlaceProviderObject.parentView.getLocationInWindow(paramArrayList);
            i = paramArrayList[1];
            if (Build.VERSION.SDK_INT < 21)
              break label1421;
            paramInt = 0;
            i = i - paramInt - (localPlaceProviderObject.viewY + paramMessageObject.top) + localPlaceProviderObject.clipTopAddition;
            paramInt = i;
            if (i >= 0)
              continue;
            paramInt = 0;
            int j = localPlaceProviderObject.viewY;
            int n = paramMessageObject.top;
            int i1 = paramFileLocation.height;
            int i2 = paramArrayList[1];
            int i3 = localPlaceProviderObject.parentView.getHeight();
            if (Build.VERSION.SDK_INT < 21)
              break label1429;
            i = 0;
            j = i1 + (n + j) - (i3 + i2 - i) + localPlaceProviderObject.clipBottomAddition;
            i = j;
            if (j >= 0)
              continue;
            i = 0;
            paramInt = Math.max(paramInt, m);
            i = Math.max(i, m);
            this.animationValues[0][0] = this.animatingImageView.getScaleX();
            this.animationValues[0][1] = this.animatingImageView.getScaleY();
            this.animationValues[0][2] = this.animatingImageView.getTranslationX();
            this.animationValues[0][3] = this.animatingImageView.getTranslationY();
            this.animationValues[0][4] = (k * localPlaceProviderObject.scale);
            this.animationValues[0][5] = (paramInt * localPlaceProviderObject.scale);
            this.animationValues[0][6] = (i * localPlaceProviderObject.scale);
            this.animationValues[0][7] = this.animatingImageView.getRadius();
            this.animationValues[1][0] = f1;
            this.animationValues[1][1] = f1;
            this.animationValues[1][2] = f3;
            this.animationValues[1][3] = f2;
            this.animationValues[1][4] = 0;
            this.animationValues[1][5] = 0;
            this.animationValues[1][6] = 0;
            this.animationValues[1][7] = 0;
            this.animatingImageView.setAnimationProgress(0.0F);
            this.backgroundDrawable.setAlpha(0);
            this.containerView.setAlpha(0.0F);
            paramMessageObject = new AnimatorSet();
            paramMessageObject.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[] { 0, 255 }), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F, 1.0F }) });
            this.animationEndRunnable = new Runnable(paramArrayList1)
            {
              public void run()
              {
                if ((PhotoViewer.this.containerView == null) || (PhotoViewer.this.windowView == null));
                do
                {
                  return;
                  if (Build.VERSION.SDK_INT >= 18)
                    PhotoViewer.this.containerView.setLayerType(0, null);
                  PhotoViewer.access$12702(PhotoViewer.this, 0);
                  PhotoViewer.access$12802(PhotoViewer.this, 0L);
                  PhotoViewer.this.setImages();
                  PhotoViewer.this.containerView.invalidate();
                  PhotoViewer.this.animatingImageView.setVisibility(8);
                  if (PhotoViewer.this.showAfterAnimation != null)
                    PhotoViewer.this.showAfterAnimation.imageReceiver.setVisible(true, true);
                  if (PhotoViewer.this.hideAfterAnimation == null)
                    continue;
                  PhotoViewer.this.hideAfterAnimation.imageReceiver.setVisible(false, true);
                }
                while ((this.val$photos == null) || (PhotoViewer.this.sendPhotoType == 3));
                if (Build.VERSION.SDK_INT >= 21);
                for (PhotoViewer.this.windowLayoutParams.flags = -2147417856; ; PhotoViewer.this.windowLayoutParams.flags = 0)
                {
                  PhotoViewer.this.windowLayoutParams.softInputMode = 272;
                  ((WindowManager)PhotoViewer.this.parentActivity.getSystemService("window")).updateViewLayout(PhotoViewer.this.windowView, PhotoViewer.this.windowLayoutParams);
                  PhotoViewer.this.windowView.setFocusable(true);
                  PhotoViewer.this.containerView.setFocusable(true);
                  return;
                }
              }
            };
            paramMessageObject.setDuration(200L);
            paramMessageObject.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnimator)
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    NotificationCenter.getInstance().setAnimationInProgress(false);
                    if (PhotoViewer.this.animationEndRunnable != null)
                    {
                      PhotoViewer.this.animationEndRunnable.run();
                      PhotoViewer.access$13302(PhotoViewer.this, null);
                    }
                  }
                });
              }
            });
            this.transitionAnimationStartTime = System.currentTimeMillis();
            AndroidUtilities.runOnUIThread(new Runnable(paramMessageObject)
            {
              public void run()
              {
                NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[] { NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded, NotificationCenter.mediaDidLoaded, NotificationCenter.dialogPhotosLoaded });
                NotificationCenter.getInstance().setAnimationInProgress(true);
                this.val$animatorSet.start();
              }
            });
            if (Build.VERSION.SDK_INT < 18)
              continue;
            this.containerView.setLayerType(2, null);
            BackgroundDrawable.access$13402(this.backgroundDrawable, new Runnable(localPlaceProviderObject)
            {
              public void run()
              {
                PhotoViewer.access$5002(PhotoViewer.this, false);
                this.val$object.imageReceiver.setVisible(false, true);
              }
            });
            return true;
            this.windowLayoutParams.flags = 8;
            continue;
          }
        }
        catch (Exception paramMessageObject)
        {
          FileLog.e(paramMessageObject);
          return false;
        }
        boolean bool = false;
        continue;
        label1406: paramInt = 0;
        continue;
        label1412: continue;
        label1415: paramInt = 0;
        continue;
        label1421: paramInt = AndroidUtilities.statusBarHeight;
        continue;
        label1429: int i = AndroidUtilities.statusBarHeight;
      }
      if ((paramArrayList1 != null) && (this.sendPhotoType != 3))
        if (Build.VERSION.SDK_INT < 21)
          break label1543;
      label1543: for (this.windowLayoutParams.flags = -2147417856; ; this.windowLayoutParams.flags = 0)
      {
        this.windowLayoutParams.softInputMode = 272;
        localWindowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        this.windowView.setFocusable(true);
        this.containerView.setFocusable(true);
        this.backgroundDrawable.setAlpha(255);
        this.containerView.setAlpha(1.0F);
        onPhotoShow(paramMessageObject, paramFileLocation, paramArrayList, paramArrayList1, paramInt, localPlaceProviderObject);
        break;
      }
    }
    catch (Exception localException)
    {
      label1559: 
      while (true)
        continue;
    }
  }

  public boolean openPhoto(TLRPC.FileLocation paramFileLocation, PhotoViewerProvider paramPhotoViewerProvider)
  {
    return openPhoto(null, paramFileLocation, null, null, 0, paramPhotoViewerProvider, null, 0L, 0L);
  }

  public boolean openPhotoForSelect(ArrayList<Object> paramArrayList, int paramInt1, int paramInt2, PhotoViewerProvider paramPhotoViewerProvider, ChatActivity paramChatActivity)
  {
    this.sendPhotoType = paramInt2;
    TextView localTextView;
    if (this.pickerView != null)
    {
      localTextView = this.pickerView.doneButton;
      if (this.sendPhotoType != 1)
        break label66;
    }
    label66: for (String str = LocaleController.getString("Set", 2131166437).toUpperCase(); ; str = LocaleController.getString("Send", 2131166409).toUpperCase())
    {
      localTextView.setText(str);
      return openPhoto(null, null, null, paramArrayList, paramInt1, paramPhotoViewerProvider, paramChatActivity, 0L, 0L);
    }
  }

  public void setAnimationValue(float paramFloat)
  {
    this.animationValue = paramFloat;
    this.containerView.invalidate();
  }

  public void setParentActivity(Activity paramActivity)
  {
    if (this.parentActivity == paramActivity)
      return;
    this.parentActivity = paramActivity;
    this.actvityContext = new ContextThemeWrapper(this.parentActivity, 2131361942);
    if (progressDrawables == null)
    {
      progressDrawables = new Drawable[4];
      progressDrawables[0] = this.parentActivity.getResources().getDrawable(2130837672);
      progressDrawables[1] = this.parentActivity.getResources().getDrawable(2130837661);
      progressDrawables[2] = this.parentActivity.getResources().getDrawable(2130837897);
      progressDrawables[3] = this.parentActivity.getResources().getDrawable(2130838029);
    }
    this.scroller = new Scroller(paramActivity);
    this.windowView = new FrameLayout(paramActivity)
    {
      private Runnable attachRunnable;

      public boolean dispatchKeyEventPreIme(KeyEvent paramKeyEvent)
      {
        if ((paramKeyEvent != null) && (paramKeyEvent.getKeyCode() == 4) && (paramKeyEvent.getAction() == 1))
        {
          if ((PhotoViewer.this.captionEditText.isPopupShowing()) || (PhotoViewer.this.captionEditText.isKeyboardVisible()))
          {
            PhotoViewer.this.closeCaptionEnter(false);
            return false;
          }
          PhotoViewer.getInstance().closePhoto(true, false);
          return true;
        }
        return super.dispatchKeyEventPreIme(paramKeyEvent);
      }

      protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
      {
        boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
        if ((Build.VERSION.SDK_INT >= 21) && (paramView == PhotoViewer.this.animatingImageView) && (PhotoViewer.this.lastInsets != null))
        {
          paramView = (WindowInsets)PhotoViewer.this.lastInsets;
          float f1 = getMeasuredHeight();
          float f2 = getMeasuredWidth();
          int i = getMeasuredHeight();
          paramCanvas.drawRect(0.0F, f1, f2, paramView.getSystemWindowInsetBottom() + i, PhotoViewer.this.blackPaint);
        }
        return bool;
      }

      protected void onAttachedToWindow()
      {
        super.onAttachedToWindow();
        PhotoViewer.access$4102(PhotoViewer.this, true);
      }

      protected void onDetachedFromWindow()
      {
        super.onDetachedFromWindow();
        PhotoViewer.access$4102(PhotoViewer.this, false);
        PhotoViewer.access$3402(PhotoViewer.this, false);
      }

      public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
      {
        return (PhotoViewer.this.isVisible) && (super.onInterceptTouchEvent(paramMotionEvent));
      }

      protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        if ((Build.VERSION.SDK_INT >= 21) && (PhotoViewer.this.lastInsets != null));
        for (paramInt1 = ((WindowInsets)PhotoViewer.this.lastInsets).getSystemWindowInsetLeft() + 0; ; paramInt1 = 0)
        {
          PhotoViewer.this.animatingImageView.layout(paramInt1, 0, PhotoViewer.this.animatingImageView.getMeasuredWidth() + paramInt1, PhotoViewer.this.animatingImageView.getMeasuredHeight());
          PhotoViewer.this.containerView.layout(paramInt1, 0, PhotoViewer.this.containerView.getMeasuredWidth() + paramInt1, PhotoViewer.this.containerView.getMeasuredHeight());
          PhotoViewer.access$3402(PhotoViewer.this, true);
          if (paramBoolean)
          {
            if (!PhotoViewer.this.dontResetZoomOnFirstLayout)
            {
              PhotoViewer.access$3602(PhotoViewer.this, 1.0F);
              PhotoViewer.access$3702(PhotoViewer.this, 0.0F);
              PhotoViewer.access$3802(PhotoViewer.this, 0.0F);
              PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
            }
            if (PhotoViewer.this.checkImageView != null)
              PhotoViewer.this.checkImageView.post(new Runnable()
              {
                public void run()
                {
                  FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)PhotoViewer.this.checkImageView.getLayoutParams();
                  int i = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                  float f;
                  int j;
                  if ((i == 3) || (i == 1))
                  {
                    f = 58.0F;
                    j = AndroidUtilities.dp(f);
                    if (Build.VERSION.SDK_INT < 21)
                      break label98;
                  }
                  label98: for (i = AndroidUtilities.statusBarHeight; ; i = 0)
                  {
                    localLayoutParams.topMargin = (i + j);
                    PhotoViewer.this.checkImageView.setLayoutParams(localLayoutParams);
                    return;
                    f = 68.0F;
                    break;
                  }
                }
              });
          }
          if (PhotoViewer.this.dontResetZoomOnFirstLayout)
          {
            PhotoViewer.this.setScaleToFill();
            PhotoViewer.access$3502(PhotoViewer.this, false);
          }
          return;
        }
      }

      protected void onMeasure(int paramInt1, int paramInt2)
      {
        int j = View.MeasureSpec.getSize(paramInt1);
        paramInt1 = View.MeasureSpec.getSize(paramInt2);
        Object localObject;
        int i;
        if ((Build.VERSION.SDK_INT >= 21) && (PhotoViewer.this.lastInsets != null))
        {
          localObject = (WindowInsets)PhotoViewer.this.lastInsets;
          paramInt2 = paramInt1;
          if (AndroidUtilities.incorrectDisplaySizeFix)
          {
            paramInt2 = paramInt1;
            if (paramInt1 > AndroidUtilities.displaySize.y)
              paramInt2 = AndroidUtilities.displaySize.y;
            paramInt2 += AndroidUtilities.statusBarHeight;
          }
          paramInt2 -= ((WindowInsets)localObject).getSystemWindowInsetBottom();
          i = j - ((WindowInsets)localObject).getSystemWindowInsetRight();
        }
        while (true)
        {
          setMeasuredDimension(i, paramInt2);
          paramInt1 = i;
          if (Build.VERSION.SDK_INT >= 21)
          {
            paramInt1 = i;
            if (PhotoViewer.this.lastInsets != null)
              paramInt1 = i - ((WindowInsets)PhotoViewer.this.lastInsets).getSystemWindowInsetLeft();
          }
          localObject = PhotoViewer.this.animatingImageView.getLayoutParams();
          PhotoViewer.this.animatingImageView.measure(View.MeasureSpec.makeMeasureSpec(((ViewGroup.LayoutParams)localObject).width, -2147483648), View.MeasureSpec.makeMeasureSpec(((ViewGroup.LayoutParams)localObject).height, -2147483648));
          PhotoViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824));
          return;
          paramInt2 = paramInt1;
          i = j;
          if (paramInt1 <= AndroidUtilities.displaySize.y)
            continue;
          paramInt2 = AndroidUtilities.displaySize.y;
          i = j;
        }
      }

      public boolean onTouchEvent(MotionEvent paramMotionEvent)
      {
        return (PhotoViewer.this.isVisible) && (PhotoViewer.this.onTouchEvent(paramMotionEvent));
      }

      public ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback, int paramInt)
      {
        if (Build.VERSION.SDK_INT >= 23)
        {
          Object localObject = PhotoViewer.this.parentActivity.findViewById(16908290);
          if ((localObject instanceof ViewGroup))
            try
            {
              localObject = ((ViewGroup)localObject).startActionModeForChild(paramView, paramCallback, paramInt);
              return localObject;
            }
            catch (Throwable localThrowable)
            {
              FileLog.e(localThrowable);
            }
        }
        return (ActionMode)super.startActionModeForChild(paramView, paramCallback, paramInt);
      }
    };
    this.windowView.setBackgroundDrawable(this.backgroundDrawable);
    this.windowView.setClipChildren(true);
    this.windowView.setFocusable(false);
    this.animatingImageView = new ClippingImageView(paramActivity);
    this.animatingImageView.setAnimationValues(this.animationValues);
    this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0F));
    this.containerView = new FrameLayoutDrawer(paramActivity);
    this.containerView.setFocusable(false);
    this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.containerView.setFitsSystemWindows(true);
      this.containerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
      {
        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View paramView, WindowInsets paramWindowInsets)
        {
          paramView = (WindowInsets)PhotoViewer.this.lastInsets;
          PhotoViewer.access$3102(PhotoViewer.this, paramWindowInsets);
          if ((paramView == null) || (!paramView.toString().equals(paramWindowInsets.toString())))
            PhotoViewer.this.windowView.requestLayout();
          return paramWindowInsets.consumeSystemWindowInsets();
        }
      });
      this.containerView.setSystemUiVisibility(1280);
    }
    this.windowLayoutParams = new WindowManager.LayoutParams();
    this.windowLayoutParams.height = -1;
    this.windowLayoutParams.format = -3;
    this.windowLayoutParams.width = -1;
    this.windowLayoutParams.gravity = 51;
    this.windowLayoutParams.type = 99;
    boolean bool;
    label417: Object localObject;
    float f;
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.windowLayoutParams.flags = -2147417848;
      this.actionBar = new ActionBar(paramActivity);
      this.actionBar.setTitleColor(-1);
      this.actionBar.setSubtitleColor(-1);
      this.actionBar.setBackgroundColor(2130706432);
      paramActivity = this.actionBar;
      if (Build.VERSION.SDK_INT < 21)
        break label3413;
      bool = true;
      paramActivity.setOccupyStatusBar(bool);
      this.actionBar.setItemsBackgroundColor(1090519039, false);
      this.actionBar.setBackButtonImage(2130837732);
      this.actionBar.setTitle(LocaleController.formatString("Of", 2131166154, new Object[] { Integer.valueOf(1), Integer.valueOf(1) }));
      this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0F));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public boolean canOpenMenu()
        {
          if (PhotoViewer.this.currentMessageObject != null)
            if (!FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner).exists())
              break label72;
          while (true)
          {
            return true;
            TLRPC.FileLocation localFileLocation;
            if (PhotoViewer.this.currentFileLocation != null)
            {
              localFileLocation = PhotoViewer.this.currentFileLocation;
              if (PhotoViewer.this.avatarsDialogId == 0)
                break label74;
            }
            label72: label74: for (boolean bool = true; !FileLoader.getPathToAttach(localFileLocation, bool).exists(); bool = false)
              return false;
          }
        }

        public void onItemClick(int paramInt)
        {
          int i = 1;
          if (paramInt == -1)
          {
            if ((PhotoViewer.this.needCaptionLayout) && ((PhotoViewer.this.captionEditText.isPopupShowing()) || (PhotoViewer.this.captionEditText.isKeyboardVisible())))
            {
              PhotoViewer.this.closeCaptionEnter(false);
              return;
            }
            PhotoViewer.this.closePhoto(true, false);
            return;
          }
          Object localObject1;
          if (paramInt == 1)
          {
            if ((Build.VERSION.SDK_INT >= 23) && (PhotoViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
            {
              PhotoViewer.this.parentActivity.requestPermissions(new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 4);
              return;
            }
            if (PhotoViewer.this.currentMessageObject != null)
              localObject1 = FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner);
          }
          while (true)
          {
            Object localObject3;
            if ((localObject1 != null) && (((File)localObject1).exists()))
            {
              localObject1 = ((File)localObject1).toString();
              localObject3 = PhotoViewer.this.parentActivity;
              if ((PhotoViewer.this.currentMessageObject != null) && (PhotoViewer.this.currentMessageObject.isVideo()));
              for (paramInt = i; ; paramInt = 0)
              {
                MediaController.saveFile((String)localObject1, (Context)localObject3, paramInt, null, null);
                return;
                if (PhotoViewer.this.currentFileLocation == null)
                  break label1185;
                localObject1 = PhotoViewer.this.currentFileLocation;
                if (PhotoViewer.this.avatarsDialogId != 0);
                for (boolean bool = true; ; bool = false)
                {
                  localObject1 = FileLoader.getPathToAttach((TLObject)localObject1, bool);
                  break;
                }
              }
            }
            localObject1 = new AlertDialog.Builder(PhotoViewer.this.parentActivity);
            ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165319));
            ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166153), null);
            ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("PleaseDownload", 2131166288));
            PhotoViewer.this.showAlertDialog((AlertDialog.Builder)localObject1);
            return;
            if (paramInt == 2)
            {
              if (PhotoViewer.this.opennedFromMedia)
              {
                PhotoViewer.this.closePhoto(true, false);
                return;
              }
              if (PhotoViewer.this.currentDialogId == 0L)
                break;
              PhotoViewer.access$5002(PhotoViewer.this, true);
              localObject1 = new Bundle();
              ((Bundle)localObject1).putLong("dialog_id", PhotoViewer.this.currentDialogId);
              localObject1 = new MediaActivity((Bundle)localObject1);
              if (PhotoViewer.this.parentChatActivity != null)
                ((MediaActivity)localObject1).setChatInfo(PhotoViewer.this.parentChatActivity.getCurrentChatInfo());
              PhotoViewer.this.closePhoto(false, false);
              ((LaunchActivity)PhotoViewer.this.parentActivity).presentFragment((BaseFragment)localObject1, false, true);
              return;
            }
            if (paramInt == 3)
              break;
            if (paramInt == 6)
            {
              if (PhotoViewer.this.parentActivity == null)
                break;
              AlertDialog.Builder localBuilder = new AlertDialog.Builder(PhotoViewer.this.parentActivity);
              boolean[] arrayOfBoolean;
              label592: FrameLayout localFrameLayout;
              CheckBoxCell localCheckBoxCell;
              if ((PhotoViewer.this.currentMessageObject != null) && (PhotoViewer.this.currentMessageObject.isVideo()))
              {
                localBuilder.setMessage(LocaleController.formatString("AreYouSureDeleteVideo", 2131165345, new Object[0]));
                localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
                arrayOfBoolean = new boolean[1];
                if (PhotoViewer.this.currentMessageObject != null)
                {
                  paramInt = (int)PhotoViewer.this.currentMessageObject.getDialogId();
                  if (paramInt != 0)
                  {
                    if (paramInt <= 0)
                      break label962;
                    localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(paramInt));
                    localObject3 = null;
                    if ((localObject1 != null) || (!ChatObject.isChannel((TLRPC.Chat)localObject3)))
                    {
                      paramInt = ConnectionsManager.getInstance().getCurrentTime();
                      if (((localObject1 != null) && (((TLRPC.User)localObject1).id != UserConfig.getClientUserId())) || ((localObject3 != null) && ((PhotoViewer.this.currentMessageObject.messageOwner.action == null) || ((PhotoViewer.this.currentMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty))) && (PhotoViewer.this.currentMessageObject.isOut()) && (paramInt - PhotoViewer.this.currentMessageObject.messageOwner.date <= 172800)))
                      {
                        localFrameLayout = new FrameLayout(PhotoViewer.this.parentActivity);
                        localCheckBoxCell = new CheckBoxCell(PhotoViewer.this.parentActivity, true);
                        localCheckBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        if (localObject3 == null)
                          break label981;
                        localCheckBoxCell.setText(LocaleController.getString("DeleteForAll", 2131165640), "", false, false);
                        label768: if (!LocaleController.isRTL)
                          break label1015;
                        paramInt = AndroidUtilities.dp(16.0F);
                        label781: if (!LocaleController.isRTL)
                          break label1025;
                      }
                    }
                  }
                }
              }
              label1025: for (i = AndroidUtilities.dp(8.0F); ; i = AndroidUtilities.dp(16.0F))
              {
                localCheckBoxCell.setPadding(paramInt, 0, i, 0);
                localFrameLayout.addView(localCheckBoxCell, LayoutHelper.createFrame(-1, 48.0F, 51, 0.0F, 0.0F, 0.0F, 0.0F));
                localCheckBoxCell.setOnClickListener(new View.OnClickListener(arrayOfBoolean)
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
                localBuilder.setView(localFrameLayout);
                localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(arrayOfBoolean)
                {
                  public void onClick(DialogInterface paramDialogInterface, int paramInt)
                  {
                    Object localObject1 = null;
                    Object localObject2;
                    ArrayList localArrayList;
                    if (!PhotoViewer.this.imagesArr.isEmpty())
                    {
                      if ((PhotoViewer.this.currentIndex < 0) || (PhotoViewer.this.currentIndex >= PhotoViewer.this.imagesArr.size()));
                      do
                      {
                        return;
                        localObject2 = (MessageObject)PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                      }
                      while (!((MessageObject)localObject2).isSent());
                      PhotoViewer.this.closePhoto(false, false);
                      localArrayList = new ArrayList();
                      localArrayList.add(Integer.valueOf(((MessageObject)localObject2).getId()));
                      if (((int)((MessageObject)localObject2).getDialogId() != 0) || (((MessageObject)localObject2).messageOwner.random_id == 0L))
                        break label754;
                      paramDialogInterface = new ArrayList();
                      paramDialogInterface.add(Long.valueOf(((MessageObject)localObject2).messageOwner.random_id));
                      localObject1 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(((MessageObject)localObject2).getDialogId() >> 32)));
                    }
                    while (true)
                    {
                      MessagesController.getInstance().deleteMessages(localArrayList, paramDialogInterface, (TLRPC.EncryptedChat)localObject1, ((MessageObject)localObject2).messageOwner.to_id.channel_id, this.val$deleteForAll[0]);
                      return;
                      if ((PhotoViewer.this.avatarsArr.isEmpty()) || (PhotoViewer.this.currentIndex < 0) || (PhotoViewer.this.currentIndex >= PhotoViewer.this.avatarsArr.size()))
                        break;
                      paramDialogInterface = (TLRPC.Photo)PhotoViewer.this.avatarsArr.get(PhotoViewer.this.currentIndex);
                      localObject1 = (TLRPC.FileLocation)PhotoViewer.this.imagesArrLocations.get(PhotoViewer.this.currentIndex);
                      if ((paramDialogInterface instanceof TLRPC.TL_photoEmpty))
                        paramDialogInterface = null;
                      while (true)
                      {
                        if (PhotoViewer.this.currentUserAvatarLocation != null)
                          if (paramDialogInterface != null)
                          {
                            localObject1 = paramDialogInterface.sizes.iterator();
                            while (((Iterator)localObject1).hasNext())
                            {
                              localObject2 = (TLRPC.PhotoSize)((Iterator)localObject1).next();
                              if ((((TLRPC.PhotoSize)localObject2).location.local_id != PhotoViewer.this.currentUserAvatarLocation.local_id) || (((TLRPC.PhotoSize)localObject2).location.volume_id != PhotoViewer.this.currentUserAvatarLocation.volume_id))
                                continue;
                              paramInt = 1;
                            }
                          }
                        while (true)
                        {
                          if (paramInt != 0)
                          {
                            MessagesController.getInstance().deleteUserPhoto(null);
                            PhotoViewer.this.closePhoto(false, false);
                            return;
                            if ((((TLRPC.FileLocation)localObject1).local_id == PhotoViewer.this.currentUserAvatarLocation.local_id) && (((TLRPC.FileLocation)localObject1).volume_id == PhotoViewer.this.currentUserAvatarLocation.volume_id))
                            {
                              paramInt = 1;
                              continue;
                            }
                          }
                          else
                          {
                            if (paramDialogInterface == null)
                              break;
                            localObject1 = new TLRPC.TL_inputPhoto();
                            ((TLRPC.TL_inputPhoto)localObject1).id = paramDialogInterface.id;
                            ((TLRPC.TL_inputPhoto)localObject1).access_hash = paramDialogInterface.access_hash;
                            MessagesController.getInstance().deleteUserPhoto((TLRPC.InputPhoto)localObject1);
                            MessagesStorage.getInstance().clearUserPhoto(PhotoViewer.this.avatarsDialogId, paramDialogInterface.id);
                            PhotoViewer.this.imagesArrLocations.remove(PhotoViewer.this.currentIndex);
                            PhotoViewer.this.imagesArrLocationsSizes.remove(PhotoViewer.this.currentIndex);
                            PhotoViewer.this.avatarsArr.remove(PhotoViewer.this.currentIndex);
                            if (PhotoViewer.this.imagesArrLocations.isEmpty())
                            {
                              PhotoViewer.this.closePhoto(false, false);
                              return;
                            }
                            int i = PhotoViewer.this.currentIndex;
                            paramInt = i;
                            if (i >= PhotoViewer.this.avatarsArr.size())
                              paramInt = PhotoViewer.this.avatarsArr.size() - 1;
                            PhotoViewer.access$5302(PhotoViewer.this, -1);
                            PhotoViewer.this.setImageIndex(paramInt, true);
                            return;
                          }
                          paramInt = 0;
                        }
                      }
                      label754: paramDialogInterface = null;
                    }
                  }
                });
                localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
                PhotoViewer.this.showAlertDialog(localBuilder);
                return;
                if ((PhotoViewer.this.currentMessageObject != null) && (PhotoViewer.this.currentMessageObject.isGif()))
                {
                  localBuilder.setMessage(LocaleController.formatString("AreYouSure", 2131165335, new Object[0]));
                  break;
                }
                localBuilder.setMessage(LocaleController.formatString("AreYouSureDeletePhoto", 2131165343, new Object[0]));
                break;
                label962: localObject3 = MessagesController.getInstance().getChat(Integer.valueOf(-paramInt));
                localObject1 = null;
                break label592;
                label981: localCheckBoxCell.setText(LocaleController.formatString("DeleteForUser", 2131165641, new Object[] { UserObject.getFirstName((TLRPC.User)localObject1) }), "", false, false);
                break label768;
                label1015: paramInt = AndroidUtilities.dp(8.0F);
                break label781;
              }
            }
            if (paramInt == 10)
            {
              PhotoViewer.this.onSharePressed();
              return;
            }
            if (paramInt == 11)
              try
              {
                AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                PhotoViewer.this.closePhoto(false, false);
                return;
              }
              catch (Exception localException)
              {
                FileLog.e(localException);
                return;
              }
            if ((paramInt != 13) || (PhotoViewer.this.parentActivity == null) || (PhotoViewer.this.currentMessageObject == null) || (PhotoViewer.this.currentMessageObject.messageOwner.media == null) || (PhotoViewer.this.currentMessageObject.messageOwner.media.photo == null))
              break;
            new StickersAlert(PhotoViewer.this.parentActivity, PhotoViewer.this.currentMessageObject.messageOwner.media.photo).show();
            return;
            label1185: Object localObject2 = null;
          }
        }
      });
      paramActivity = this.actionBar.createMenu();
      this.masksItem = paramActivity.addItem(13, 2130837808);
      this.menuItem = paramActivity.addItem(0, 2130837738);
      this.menuItem.addSubItem(11, LocaleController.getString("OpenInExternalApp", 2131166167));
      this.menuItem.addSubItem(2, LocaleController.getString("ShowAllMedia", 2131166468));
      this.menuItem.addSubItem(10, LocaleController.getString("ShareFile", 2131166451));
      this.menuItem.addSubItem(1, LocaleController.getString("SaveToGallery", 2131166375));
      this.menuItem.addSubItem(6, LocaleController.getString("Delete", 2131165628));
      this.bottomLayout = new FrameLayout(this.actvityContext);
      this.bottomLayout.setBackgroundColor(2130706432);
      this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
      this.captionTextViewOld = new TextView(this.actvityContext)
      {
        public boolean onTouchEvent(MotionEvent paramMotionEvent)
        {
          return (PhotoViewer.this.bottomTouchEnabled) && (super.onTouchEvent(paramMotionEvent));
        }
      };
      this.captionTextViewOld.setMaxLines(10);
      this.captionTextViewOld.setBackgroundColor(2130706432);
      this.captionTextViewOld.setPadding(AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F));
      this.captionTextViewOld.setLinkTextColor(-1);
      this.captionTextViewOld.setTextColor(-1);
      this.captionTextViewOld.setGravity(19);
      this.captionTextViewOld.setTextSize(1, 16.0F);
      this.captionTextViewOld.setVisibility(4);
      this.containerView.addView(this.captionTextViewOld, LayoutHelper.createFrame(-1, -2.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
      this.captionTextViewOld.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (PhotoViewer.this.cropItem.getVisibility() == 0)
            PhotoViewer.this.openCaptionEnter();
        }
      });
      paramActivity = new TextView(this.actvityContext)
      {
        public boolean onTouchEvent(MotionEvent paramMotionEvent)
        {
          return (PhotoViewer.this.bottomTouchEnabled) && (super.onTouchEvent(paramMotionEvent));
        }
      };
      this.captionTextViewNew = paramActivity;
      this.captionTextView = paramActivity;
      this.captionTextViewNew.setMaxLines(10);
      this.captionTextViewNew.setBackgroundColor(2130706432);
      this.captionTextViewNew.setPadding(AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F));
      this.captionTextViewNew.setLinkTextColor(-1);
      this.captionTextViewNew.setTextColor(-1);
      this.captionTextViewNew.setGravity(19);
      this.captionTextViewNew.setTextSize(1, 16.0F);
      this.captionTextViewNew.setVisibility(4);
      this.containerView.addView(this.captionTextViewNew, LayoutHelper.createFrame(-1, -2.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
      this.captionTextViewNew.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (PhotoViewer.this.cropItem.getVisibility() == 0)
            PhotoViewer.this.openCaptionEnter();
        }
      });
      this.photoProgressViews[0] = new PhotoProgressView(this.containerView.getContext(), this.containerView);
      this.photoProgressViews[0].setBackgroundState(0, false);
      this.photoProgressViews[1] = new PhotoProgressView(this.containerView.getContext(), this.containerView);
      this.photoProgressViews[1].setBackgroundState(0, false);
      this.photoProgressViews[2] = new PhotoProgressView(this.containerView.getContext(), this.containerView);
      this.photoProgressViews[2].setBackgroundState(0, false);
      this.shareButton = new ImageView(this.containerView.getContext());
      this.shareButton.setImageResource(2130838058);
      this.shareButton.setScaleType(ImageView.ScaleType.CENTER);
      this.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(1090519039));
      this.bottomLayout.addView(this.shareButton, LayoutHelper.createFrame(50, -1, 53));
      this.shareButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          PhotoViewer.this.onSharePressed();
        }
      });
      this.nameTextView = new TextView(this.containerView.getContext());
      this.nameTextView.setTextSize(1, 14.0F);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.nameTextView.setTextColor(-1);
      this.nameTextView.setGravity(3);
      this.bottomLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 16.0F, 5.0F, 60.0F, 0.0F));
      this.dateTextView = new TextView(this.containerView.getContext());
      this.dateTextView.setTextSize(1, 13.0F);
      this.dateTextView.setSingleLine(true);
      this.dateTextView.setMaxLines(1);
      this.dateTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.dateTextView.setTextColor(-1);
      this.dateTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.dateTextView.setGravity(3);
      this.bottomLayout.addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 16.0F, 25.0F, 50.0F, 0.0F));
      if (Build.VERSION.SDK_INT >= 16)
      {
        this.videoPlayerSeekbar = new SeekBar(this.containerView.getContext());
        this.videoPlayerSeekbar.setColors(1728053247, -1, -1);
        this.videoPlayerSeekbar.setDelegate(new SeekBar.SeekBarDelegate()
        {
          public void onSeekBarDrag(float paramFloat)
          {
            if (PhotoViewer.this.videoPlayer != null)
            {
              float f = paramFloat;
              if (!PhotoViewer.this.inPreview)
              {
                f = paramFloat;
                if (PhotoViewer.this.videoTimelineViewContainer != null)
                {
                  f = paramFloat;
                  if (PhotoViewer.this.videoTimelineViewContainer.getVisibility() == 0)
                    f = PhotoViewer.this.videoTimelineView.getLeftProgress() + (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress()) * paramFloat;
                }
              }
              PhotoViewer.this.videoPlayer.seekTo((int)((float)PhotoViewer.this.videoPlayer.getDuration() * f));
            }
          }
        });
        this.videoPlayerControlFrameLayout = new FrameLayout(this.containerView.getContext())
        {
          protected void onDraw(Canvas paramCanvas)
          {
            paramCanvas.save();
            paramCanvas.translate(AndroidUtilities.dp(48.0F), 0.0F);
            PhotoViewer.this.videoPlayerSeekbar.draw(paramCanvas);
            paramCanvas.restore();
          }

          protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
          {
            float f1 = 0.0F;
            float f2 = 0.0F;
            super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
            if (PhotoViewer.this.videoPlayer != null)
            {
              f1 = (float)PhotoViewer.this.videoPlayer.getCurrentPosition() / (float)PhotoViewer.this.videoPlayer.getDuration();
              if ((PhotoViewer.this.inPreview) || (PhotoViewer.this.videoTimelineViewContainer == null) || (PhotoViewer.this.videoTimelineViewContainer.getVisibility() != 0))
                break label167;
              f1 -= PhotoViewer.this.videoTimelineView.getLeftProgress();
              if (f1 >= 0.0F)
                break label164;
              f1 = f2;
              f2 = f1 / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
              f1 = f2;
              if (f2 > 1.0F)
                f1 = 1.0F;
            }
            label164: label167: 
            while (true)
            {
              PhotoViewer.this.videoPlayerSeekbar.setProgress(f1);
              return;
              break;
            }
          }

          protected void onMeasure(int paramInt1, int paramInt2)
          {
            long l2 = 0L;
            super.onMeasure(paramInt1, paramInt2);
            long l1 = l2;
            if (PhotoViewer.this.videoPlayer != null)
            {
              l1 = PhotoViewer.this.videoPlayer.getDuration();
              if (l1 != -9223372036854775807L)
                break label149;
              l1 = l2;
            }
            label149: 
            while (true)
            {
              l1 /= 1000L;
              paramInt1 = (int)Math.ceil(PhotoViewer.this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[] { Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L), Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L) })));
              PhotoViewer.this.videoPlayerSeekbar.setSize(getMeasuredWidth() - AndroidUtilities.dp(64.0F) - paramInt1, getMeasuredHeight());
              return;
            }
          }

          public boolean onTouchEvent(MotionEvent paramMotionEvent)
          {
            int i = (int)paramMotionEvent.getX();
            i = (int)paramMotionEvent.getY();
            if (PhotoViewer.this.videoPlayerSeekbar.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX() - AndroidUtilities.dp(48.0F), paramMotionEvent.getY()))
            {
              getParent().requestDisallowInterceptTouchEvent(true);
              invalidate();
              return true;
            }
            return super.onTouchEvent(paramMotionEvent);
          }
        };
        this.videoPlayerControlFrameLayout.setWillNotDraw(false);
        this.bottomLayout.addView(this.videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
        this.videoPlayButton = new ImageView(this.containerView.getContext());
        this.videoPlayButton.setScaleType(ImageView.ScaleType.CENTER);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48, 48, 51));
        this.videoPlayButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (PhotoViewer.this.videoPlayer == null)
              return;
            if (PhotoViewer.this.isPlaying)
            {
              PhotoViewer.this.videoPlayer.pause();
              return;
            }
            if ((Math.abs(PhotoViewer.this.videoPlayerSeekbar.getProgress() - 1.0F) < 0.01F) || (PhotoViewer.this.videoPlayer.getCurrentPosition() == PhotoViewer.this.videoPlayer.getDuration()))
              PhotoViewer.this.videoPlayer.seekTo(0L);
            PhotoViewer.this.videoPlayer.play();
          }
        });
        this.videoPlayerTime = new TextView(this.containerView.getContext());
        this.videoPlayerTime.setTextColor(-1);
        this.videoPlayerTime.setGravity(16);
        this.videoPlayerTime.setTextSize(1, 13.0F);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -1.0F, 53, 0.0F, 0.0F, 8.0F, 0.0F));
        this.videoTimelineViewContainer = new FrameLayout(this.parentActivity);
        this.videoTimelineViewContainer.setBackgroundColor(2130706432);
        this.containerView.addView(this.videoTimelineViewContainer, LayoutHelper.createFrame(-1, 52.0F, 83, 0.0F, 0.0F, 0.0F, 96.0F));
        this.videoTimelineView = new VideoTimelineView(this.parentActivity);
        this.videoTimelineView.setDelegate(new VideoTimelineView.VideoTimelineViewDelegate()
        {
          public void onLeftProgressChanged(float paramFloat)
          {
            if (PhotoViewer.this.videoPlayer == null)
              return;
            if (PhotoViewer.this.videoPlayer.isPlaying())
              PhotoViewer.this.videoPlayer.pause();
            PhotoViewer.this.videoPlayer.seekTo((int)(PhotoViewer.this.videoDuration * paramFloat));
            PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0F);
            PhotoViewer.this.updateVideoInfo();
          }

          public void onRifhtProgressChanged(float paramFloat)
          {
            if (PhotoViewer.this.videoPlayer == null)
              return;
            if (PhotoViewer.this.videoPlayer.isPlaying())
              PhotoViewer.this.videoPlayer.pause();
            PhotoViewer.this.videoPlayer.seekTo((int)(PhotoViewer.this.videoDuration * paramFloat));
            PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0F);
            PhotoViewer.this.updateVideoInfo();
          }
        });
        this.videoTimelineViewContainer.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, 44.0F, 51, 0.0F, 8.0F, 0.0F, 0.0F));
        this.progressView = new RadialProgressView(this.parentActivity);
        this.progressView.setProgressColor(-1);
        this.progressView.setBackgroundResource(2130837672);
        this.progressView.setVisibility(4);
        this.containerView.addView(this.progressView, LayoutHelper.createFrame(54, 54.0F, 17, 0.0F, 0.0F, 0.0F, 60.0F));
        this.qualityPicker = new PickerBottomLayoutViewer(this.parentActivity);
        this.qualityPicker.setBackgroundColor(2130706432);
        this.qualityPicker.updateSelectedCount(0, false);
        this.qualityPicker.setTranslationY(AndroidUtilities.dp(120.0F));
        this.qualityPicker.doneButton.setText(LocaleController.getString("Done", 2131165661).toUpperCase());
        this.containerView.addView(this.qualityPicker, LayoutHelper.createFrame(-1, 48, 83));
        this.qualityPicker.cancelButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            PhotoViewer.access$6502(PhotoViewer.this, PhotoViewer.this.previousCompression);
            PhotoViewer.this.didChangedCompressionLevel(false);
            PhotoViewer.this.showQualityView(false);
            PhotoViewer.this.requestVideoPreview(2);
          }
        });
        this.qualityPicker.doneButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            PhotoViewer.this.showQualityView(false);
            PhotoViewer.this.requestVideoPreview(2);
          }
        });
        this.qualityChooseView = new QualityChooseView(this.parentActivity);
        this.qualityChooseView.setTranslationY(AndroidUtilities.dp(120.0F));
        this.qualityChooseView.setVisibility(4);
        this.qualityChooseView.setBackgroundColor(2130706432);
        this.containerView.addView(this.qualityChooseView, LayoutHelper.createFrame(-1, 70.0F, 83, 0.0F, 0.0F, 0.0F, 96.0F));
      }
      this.pickerView = new PickerBottomLayoutViewer(this.actvityContext)
      {
        public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
        {
          return (PhotoViewer.this.bottomTouchEnabled) && (super.dispatchTouchEvent(paramMotionEvent));
        }

        public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
        {
          return (PhotoViewer.this.bottomTouchEnabled) && (super.onInterceptTouchEvent(paramMotionEvent));
        }

        public boolean onTouchEvent(MotionEvent paramMotionEvent)
        {
          return (PhotoViewer.this.bottomTouchEnabled) && (super.onTouchEvent(paramMotionEvent));
        }
      };
      this.pickerView.setBackgroundColor(2130706432);
      this.containerView.addView(this.pickerView, LayoutHelper.createFrame(-1, 48, 83));
      this.pickerView.cancelButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if ((PhotoViewer.this.placeProvider instanceof PhotoViewer.EmptyPhotoViewerProvider))
            PhotoViewer.this.closePhoto(false, false);
          do
            return;
          while (PhotoViewer.this.placeProvider == null);
          paramView = PhotoViewer.this;
          if (!PhotoViewer.this.placeProvider.cancelButtonPressed());
          for (boolean bool = true; ; bool = false)
          {
            paramView.closePhoto(bool, false);
            return;
          }
        }
      });
      this.pickerView.doneButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          int i = -1;
          if ((PhotoViewer.this.placeProvider != null) && (!PhotoViewer.this.doneButtonPressed))
          {
            if (PhotoViewer.this.captionItem.getVisibility() != 0)
              break label357;
            paramView = new VideoEditedInfo();
            paramView.startTime = PhotoViewer.this.startTime;
            paramView.endTime = PhotoViewer.this.endTime;
            paramView.rotationValue = PhotoViewer.this.rotationValue;
            paramView.originalWidth = PhotoViewer.this.originalWidth;
            paramView.originalHeight = PhotoViewer.this.originalHeight;
            paramView.bitrate = PhotoViewer.this.bitrate;
            paramView.originalPath = PhotoViewer.this.currentPlayingVideoFile.getAbsolutePath();
            paramView.estimatedSize = PhotoViewer.this.estimatedSize;
            paramView.estimatedDuration = PhotoViewer.this.estimatedDuration;
            if ((PhotoViewer.this.compressItem.getVisibility() != 8) && ((PhotoViewer.this.compressItem.getVisibility() != 0) || (PhotoViewer.this.selectedCompression != PhotoViewer.this.compressionsCount - 1)))
              break label280;
            paramView.resultWidth = PhotoViewer.this.originalWidth;
            paramView.resultHeight = PhotoViewer.this.originalHeight;
            if (!PhotoViewer.this.muteVideo)
              break label269;
            paramView.bitrate = i;
          }
          while (true)
          {
            PhotoViewer.this.placeProvider.sendButtonPressed(PhotoViewer.this.currentIndex, paramView);
            PhotoViewer.access$7102(PhotoViewer.this, true);
            PhotoViewer.this.closePhoto(false, false);
            return;
            label269: i = PhotoViewer.this.originalBitrate;
            break;
            label280: if (PhotoViewer.this.muteVideo)
            {
              PhotoViewer.access$6502(PhotoViewer.this, 1);
              PhotoViewer.this.updateWidthHeightBitrateForCompression();
            }
            paramView.resultWidth = PhotoViewer.this.resultWidth;
            paramView.resultHeight = PhotoViewer.this.resultHeight;
            if (PhotoViewer.this.muteVideo);
            while (true)
            {
              paramView.bitrate = i;
              break;
              i = PhotoViewer.this.bitrate;
            }
            label357: paramView = null;
          }
        }
      });
      paramActivity = new LinearLayout(this.parentActivity);
      paramActivity.setOrientation(0);
      this.pickerView.addView(paramActivity, LayoutHelper.createFrame(-2, 48, 49));
      this.tuneItem = new ImageView(this.parentActivity);
      this.tuneItem.setScaleType(ImageView.ScaleType.CENTER);
      this.tuneItem.setImageResource(2130838009);
      this.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(1090519039));
      paramActivity.addView(this.tuneItem, LayoutHelper.createLinear(56, 48));
      this.tuneItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          PhotoViewer.this.switchToEditMode(2);
        }
      });
      this.paintItem = new ImageView(this.parentActivity);
      this.paintItem.setScaleType(ImageView.ScaleType.CENTER);
      this.paintItem.setImageResource(2130838001);
      this.paintItem.setBackgroundDrawable(Theme.createSelectorDrawable(1090519039));
      paramActivity.addView(this.paintItem, LayoutHelper.createLinear(56, 48));
      this.paintItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          PhotoViewer.this.switchToEditMode(3);
        }
      });
      this.cropItem = new ImageView(this.parentActivity);
      this.cropItem.setScaleType(ImageView.ScaleType.CENTER);
      this.cropItem.setImageResource(2130837998);
      this.cropItem.setBackgroundDrawable(Theme.createSelectorDrawable(1090519039));
      paramActivity.addView(this.cropItem, LayoutHelper.createLinear(56, 48));
      this.cropItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          PhotoViewer.this.switchToEditMode(1);
        }
      });
      this.captionItem = new ImageView(this.parentActivity);
      this.captionItem.setScaleType(ImageView.ScaleType.CENTER);
      this.captionItem.setBackgroundDrawable(Theme.createSelectorDrawable(1090519039));
      paramActivity.addView(this.captionItem, LayoutHelper.createLinear(56, 48));
      this.captionItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          PhotoViewer.this.openCaptionEnter();
        }
      });
      this.compressItem = new ImageView(this.parentActivity);
      this.compressItem.setScaleType(ImageView.ScaleType.CENTER);
      this.compressItem.setBackgroundDrawable(Theme.createSelectorDrawable(1090519039));
      paramActivity.addView(this.compressItem, LayoutHelper.createLinear(56, 48));
      this.compressItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          PhotoViewer.this.showQualityView(true);
          PhotoViewer.this.requestVideoPreview(1);
        }
      });
      this.muteItem = new ImageView(this.parentActivity);
      this.muteItem.setScaleType(ImageView.ScaleType.CENTER);
      this.muteItem.setBackgroundDrawable(Theme.createSelectorDrawable(1090519039));
      paramActivity.addView(this.muteItem, LayoutHelper.createLinear(56, 48));
      this.muteItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          paramView = PhotoViewer.this;
          if (!PhotoViewer.this.muteVideo);
          for (boolean bool = true; ; bool = false)
          {
            PhotoViewer.access$8402(paramView, bool);
            PhotoViewer.this.updateMuteButton();
            return;
          }
        }
      });
      this.editorDoneLayout = new PickerBottomLayoutViewer(this.actvityContext);
      this.editorDoneLayout.setBackgroundColor(2130706432);
      this.editorDoneLayout.updateSelectedCount(0, false);
      this.editorDoneLayout.setVisibility(8);
      this.containerView.addView(this.editorDoneLayout, LayoutHelper.createFrame(-1, 48, 83));
      this.editorDoneLayout.cancelButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (PhotoViewer.this.currentEditMode == 1)
            PhotoViewer.this.photoCropView.cancelAnimationRunnable();
          PhotoViewer.this.switchToEditMode(0);
        }
      });
      this.editorDoneLayout.doneButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if ((PhotoViewer.this.currentEditMode == 1) && (!PhotoViewer.this.photoCropView.isReady()))
            return;
          PhotoViewer.this.applyCurrentEditMode();
          PhotoViewer.this.switchToEditMode(0);
        }
      });
      this.resetButton = new TextView(this.actvityContext);
      this.resetButton.setVisibility(8);
      this.resetButton.setTextSize(1, 14.0F);
      this.resetButton.setTextColor(-1);
      this.resetButton.setGravity(17);
      this.resetButton.setBackgroundDrawable(Theme.createSelectorDrawable(-12763843, 0));
      this.resetButton.setPadding(AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F), 0);
      this.resetButton.setText(LocaleController.getString("Reset", 2131165596).toUpperCase());
      this.resetButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.editorDoneLayout.addView(this.resetButton, LayoutHelper.createFrame(-2, -1, 49));
      this.resetButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          PhotoViewer.this.photoCropView.reset();
        }
      });
      this.gestureDetector = new GestureDetector(this.containerView.getContext(), this);
      this.gestureDetector.setOnDoubleTapListener(this);
      paramActivity = new ImageReceiver.ImageReceiverDelegate()
      {
        public void didSetImage(ImageReceiver paramImageReceiver, boolean paramBoolean1, boolean paramBoolean2)
        {
          Bitmap localBitmap;
          PhotoCropView localPhotoCropView;
          int i;
          if ((paramImageReceiver == PhotoViewer.this.centerImage) && (paramBoolean1) && (!paramBoolean2) && (PhotoViewer.this.currentEditMode == 1) && (PhotoViewer.this.photoCropView != null))
          {
            localBitmap = paramImageReceiver.getBitmap();
            if (localBitmap != null)
            {
              localPhotoCropView = PhotoViewer.this.photoCropView;
              i = paramImageReceiver.getOrientation();
              if (PhotoViewer.this.sendPhotoType == 1)
                break label159;
            }
          }
          label159: for (paramBoolean2 = true; ; paramBoolean2 = false)
          {
            localPhotoCropView.setBitmap(localBitmap, i, paramBoolean2);
            if ((paramImageReceiver == PhotoViewer.this.centerImage) && (paramBoolean1) && (PhotoViewer.this.placeProvider != null) && (PhotoViewer.this.placeProvider.scaleToFill()) && (!PhotoViewer.this.ignoreDidSetImage))
            {
              if (PhotoViewer.this.wasLayout)
                break;
              PhotoViewer.access$3502(PhotoViewer.this, true);
            }
            return;
          }
          PhotoViewer.this.setScaleToFill();
        }
      };
      this.centerImage.setParentView(this.containerView);
      this.centerImage.setCrossfadeAlpha(2);
      this.centerImage.setInvalidateAll(true);
      this.centerImage.setDelegate(paramActivity);
      this.leftImage.setParentView(this.containerView);
      this.leftImage.setCrossfadeAlpha(2);
      this.leftImage.setInvalidateAll(true);
      this.leftImage.setDelegate(paramActivity);
      this.rightImage.setParentView(this.containerView);
      this.rightImage.setCrossfadeAlpha(2);
      this.rightImage.setInvalidateAll(true);
      this.rightImage.setDelegate(paramActivity);
      int i = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
      this.checkImageView = new CheckBox(this.containerView.getContext(), 2130838056)
      {
        public boolean onTouchEvent(MotionEvent paramMotionEvent)
        {
          return (PhotoViewer.this.bottomTouchEnabled) && (super.onTouchEvent(paramMotionEvent));
        }
      };
      this.checkImageView.setDrawBackground(true);
      this.checkImageView.setSize(45);
      this.checkImageView.setCheckOffset(AndroidUtilities.dp(1.0F));
      this.checkImageView.setColor(-12793105, -1);
      this.checkImageView.setVisibility(8);
      paramActivity = this.containerView;
      localObject = this.checkImageView;
      if ((i != 3) && (i != 1))
        break label3419;
      f = 58.0F;
    }
    while (true)
    {
      paramActivity.addView((View)localObject, LayoutHelper.createFrame(45, 45.0F, 53, 0.0F, f, 10.0F, 0.0F));
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramActivity = (FrameLayout.LayoutParams)this.checkImageView.getLayoutParams();
        paramActivity.topMargin += AndroidUtilities.statusBarHeight;
      }
      this.checkImageView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (PhotoViewer.this.placeProvider != null)
          {
            PhotoViewer.this.placeProvider.setPhotoChecked(PhotoViewer.this.currentIndex);
            PhotoViewer.this.checkImageView.setChecked(PhotoViewer.this.placeProvider.isPhotoChecked(PhotoViewer.this.currentIndex), true);
            PhotoViewer.this.updateSelectedCount();
          }
        }
      });
      this.captionEditText = new PhotoViewerCaptionEnterView(this.actvityContext, this.containerView, this.windowView)
      {
        public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
        {
          int j = 0;
          int i = j;
          try
          {
            if (!PhotoViewer.this.bottomTouchEnabled)
            {
              boolean bool = super.dispatchTouchEvent(paramMotionEvent);
              i = j;
              if (bool)
                i = 1;
            }
            return i;
          }
          catch (Exception paramMotionEvent)
          {
            FileLog.e(paramMotionEvent);
          }
          return false;
        }

        public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
        {
          int j = 0;
          int i = j;
          try
          {
            if (!PhotoViewer.this.bottomTouchEnabled)
            {
              boolean bool = super.onInterceptTouchEvent(paramMotionEvent);
              i = j;
              if (bool)
                i = 1;
            }
            return i;
          }
          catch (Exception paramMotionEvent)
          {
            FileLog.e(paramMotionEvent);
          }
          return false;
        }

        public boolean onTouchEvent(MotionEvent paramMotionEvent)
        {
          return (!PhotoViewer.this.bottomTouchEnabled) && (super.onTouchEvent(paramMotionEvent));
        }
      };
      this.captionEditText.setDelegate(new PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate()
      {
        public void onCaptionEnter()
        {
          PhotoViewer.this.closeCaptionEnter(true);
        }

        public void onTextChanged(CharSequence paramCharSequence)
        {
          if ((PhotoViewer.this.mentionsAdapter != null) && (PhotoViewer.this.captionEditText != null) && (PhotoViewer.this.parentChatActivity != null) && (paramCharSequence != null))
            PhotoViewer.this.mentionsAdapter.searchUsernameOrHashtag(paramCharSequence.toString(), PhotoViewer.this.captionEditText.getCursorPosition(), PhotoViewer.this.parentChatActivity.messages);
        }

        public void onWindowSizeChanged(int paramInt)
        {
          int j = Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount());
          int i;
          if (PhotoViewer.this.mentionsAdapter.getItemCount() > 3)
          {
            i = 18;
            i = AndroidUtilities.dp(i + j * 36);
            if (paramInt - ActionBar.getCurrentActionBarHeight() * 2 >= i)
              break label103;
            PhotoViewer.access$9802(PhotoViewer.this, false);
            if ((PhotoViewer.this.mentionListView != null) && (PhotoViewer.this.mentionListView.getVisibility() == 0))
              PhotoViewer.this.mentionListView.setVisibility(4);
          }
          label103: 
          do
          {
            return;
            i = 0;
            break;
            PhotoViewer.access$9802(PhotoViewer.this, true);
          }
          while ((PhotoViewer.this.mentionListView == null) || (PhotoViewer.this.mentionListView.getVisibility() != 4));
          PhotoViewer.this.mentionListView.setVisibility(0);
        }
      });
      this.containerView.addView(this.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
      this.mentionListView = new RecyclerListView(this.actvityContext)
      {
        public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
        {
          return (!PhotoViewer.this.bottomTouchEnabled) && (super.dispatchTouchEvent(paramMotionEvent));
        }

        public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
        {
          return (!PhotoViewer.this.bottomTouchEnabled) && (super.onInterceptTouchEvent(paramMotionEvent));
        }

        public boolean onTouchEvent(MotionEvent paramMotionEvent)
        {
          return (!PhotoViewer.this.bottomTouchEnabled) && (super.onTouchEvent(paramMotionEvent));
        }
      };
      this.mentionListView.setTag(Integer.valueOf(5));
      this.mentionLayoutManager = new LinearLayoutManager(this.actvityContext)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      this.mentionLayoutManager.setOrientation(1);
      this.mentionListView.setLayoutManager(this.mentionLayoutManager);
      this.mentionListView.setBackgroundColor(2130706432);
      this.mentionListView.setVisibility(8);
      this.mentionListView.setClipToPadding(true);
      this.mentionListView.setOverScrollMode(2);
      this.containerView.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
      paramActivity = this.mentionListView;
      localObject = new MentionsAdapter(this.actvityContext, true, 0L, new MentionsAdapter.MentionsAdapterDelegate()
      {
        public void needChangePanelVisibility(boolean paramBoolean)
        {
          if (paramBoolean)
          {
            FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)PhotoViewer.this.mentionListView.getLayoutParams();
            int k = Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount());
            if (PhotoViewer.this.mentionsAdapter.getItemCount() > 3)
            {
              int i = 18;
              i += k * 36;
              localLayoutParams.height = AndroidUtilities.dp(i);
              localLayoutParams.topMargin = (-AndroidUtilities.dp(i));
              PhotoViewer.this.mentionListView.setLayoutParams(localLayoutParams);
              if (PhotoViewer.this.mentionListAnimation != null)
              {
                PhotoViewer.this.mentionListAnimation.cancel();
                PhotoViewer.access$9902(PhotoViewer.this, null);
              }
              if (PhotoViewer.this.mentionListView.getVisibility() != 0)
                break label150;
              PhotoViewer.this.mentionListView.setAlpha(1.0F);
            }
          }
          label150: 
          do
          {
            return;
            int j = 0;
            break;
            PhotoViewer.this.mentionLayoutManager.scrollToPositionWithOffset(0, 10000);
            if (PhotoViewer.this.allowMentions)
            {
              PhotoViewer.this.mentionListView.setVisibility(0);
              PhotoViewer.access$9902(PhotoViewer.this, new AnimatorSet());
              PhotoViewer.this.mentionListAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.access$2000(PhotoViewer.this), "alpha", new float[] { 0.0F, 1.0F }) });
              PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapter()
              {
                public void onAnimationEnd(Animator paramAnimator)
                {
                  if ((PhotoViewer.this.mentionListAnimation != null) && (PhotoViewer.this.mentionListAnimation.equals(paramAnimator)))
                    PhotoViewer.access$9902(PhotoViewer.this, null);
                }
              });
              PhotoViewer.this.mentionListAnimation.setDuration(200L);
              PhotoViewer.this.mentionListAnimation.start();
              return;
            }
            PhotoViewer.this.mentionListView.setAlpha(1.0F);
            PhotoViewer.this.mentionListView.setVisibility(4);
            return;
            if (PhotoViewer.this.mentionListAnimation == null)
              continue;
            PhotoViewer.this.mentionListAnimation.cancel();
            PhotoViewer.access$9902(PhotoViewer.this, null);
          }
          while (PhotoViewer.this.mentionListView.getVisibility() == 8);
          if (PhotoViewer.this.allowMentions)
          {
            PhotoViewer.access$9902(PhotoViewer.this, new AnimatorSet());
            PhotoViewer.this.mentionListAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(PhotoViewer.access$2000(PhotoViewer.this), "alpha", new float[] { 0.0F }) });
            PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnimator)
              {
                if ((PhotoViewer.this.mentionListAnimation != null) && (PhotoViewer.this.mentionListAnimation.equals(paramAnimator)))
                {
                  PhotoViewer.this.mentionListView.setVisibility(8);
                  PhotoViewer.access$9902(PhotoViewer.this, null);
                }
              }
            });
            PhotoViewer.this.mentionListAnimation.setDuration(200L);
            PhotoViewer.this.mentionListAnimation.start();
            return;
          }
          PhotoViewer.this.mentionListView.setVisibility(8);
        }

        public void onContextClick(TLRPC.BotInlineResult paramBotInlineResult)
        {
        }

        public void onContextSearch(boolean paramBoolean)
        {
        }
      });
      this.mentionsAdapter = ((MentionsAdapter)localObject);
      paramActivity.setAdapter((RecyclerView.Adapter)localObject);
      this.mentionsAdapter.setAllowNewMentions(false);
      this.mentionListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          paramView = PhotoViewer.this.mentionsAdapter.getItem(paramInt);
          paramInt = PhotoViewer.this.mentionsAdapter.getResultStartPosition();
          int i = PhotoViewer.this.mentionsAdapter.getResultLength();
          if ((paramView instanceof TLRPC.User))
          {
            paramView = (TLRPC.User)paramView;
            if (paramView != null)
              PhotoViewer.this.captionEditText.replaceWithText(paramInt, i, "@" + paramView.username + " ");
          }
          do
            return;
          while (!(paramView instanceof String));
          PhotoViewer.this.captionEditText.replaceWithText(paramInt, i, paramView + " ");
        }
      });
      this.mentionListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramView, int paramInt)
        {
          if ((PhotoViewer.this.mentionsAdapter.getItem(paramInt) instanceof String))
          {
            paramView = new AlertDialog.Builder(PhotoViewer.this.parentActivity);
            paramView.setTitle(LocaleController.getString("AppName", 2131165319));
            paramView.setMessage(LocaleController.getString("ClearSearch", 2131165555));
            paramView.setPositiveButton(LocaleController.getString("ClearButton", 2131165549).toUpperCase(), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                PhotoViewer.this.mentionsAdapter.clearRecentHashtags();
              }
            });
            paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
            PhotoViewer.this.showAlertDialog(paramView);
            return true;
          }
          return false;
        }
      });
      return;
      this.windowLayoutParams.flags = 8;
      break;
      label3413: bool = false;
      break label417;
      label3419: f = 68.0F;
    }
  }

  public void setParentAlert(ChatAttachAlert paramChatAttachAlert)
  {
    this.parentAlert = paramChatAttachAlert;
  }

  public void setParentChatActivity(ChatActivity paramChatActivity)
  {
    this.parentChatActivity = paramChatActivity;
  }

  public void showAlertDialog(AlertDialog.Builder paramBuilder)
  {
    if (this.parentActivity == null)
      return;
    try
    {
      if (this.visibleDialog != null)
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
      try
      {
        this.visibleDialog = paramBuilder.show();
        this.visibleDialog.setCanceledOnTouchOutside(true);
        this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
          public void onDismiss(DialogInterface paramDialogInterface)
          {
            PhotoViewer.access$10802(PhotoViewer.this, null);
          }
        });
        return;
      }
      catch (Exception paramBuilder)
      {
        FileLog.e(paramBuilder);
        return;
      }
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void updateMuteButton()
  {
    if (this.videoPlayer != null)
      this.videoPlayer.setMute(this.muteVideo);
    if (this.muteVideo)
    {
      this.actionBar.setTitle(LocaleController.getString("AttachGif", 2131165364));
      this.actionBar.setSubtitle(null);
      this.muteItem.setImageResource(2130838122);
      if (this.compressItem.getVisibility() == 0)
      {
        this.compressItem.setClickable(false);
        this.compressItem.setAlpha(0.5F);
        this.compressItem.setEnabled(false);
      }
      this.videoTimelineView.setMaxProgressDiff(30000.0F / this.videoDuration);
      return;
    }
    this.actionBar.setTitle(LocaleController.getString("AttachVideo", 2131165369));
    this.actionBar.setSubtitle(this.currentSubtitle);
    this.muteItem.setImageResource(2130838123);
    if (this.compressItem.getVisibility() == 0)
    {
      this.compressItem.setClickable(true);
      this.compressItem.setAlpha(1.0F);
      this.compressItem.setEnabled(true);
    }
    this.videoTimelineView.setMaxProgressDiff(1.0F);
  }

  private class BackgroundDrawable extends ColorDrawable
  {
    private boolean allowDrawContent;
    private Runnable drawRunnable;

    public BackgroundDrawable(int arg2)
    {
      super();
    }

    public void draw(Canvas paramCanvas)
    {
      super.draw(paramCanvas);
      if ((getAlpha() != 0) && (this.drawRunnable != null))
      {
        this.drawRunnable.run();
        this.drawRunnable = null;
      }
    }

    public void setAlpha(int paramInt)
    {
      boolean bool;
      if ((PhotoViewer.this.parentActivity instanceof LaunchActivity))
      {
        if ((PhotoViewer.this.isVisible) && (paramInt == 255))
          break label94;
        bool = true;
        this.allowDrawContent = bool;
        ((LaunchActivity)PhotoViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent(this.allowDrawContent);
        if (PhotoViewer.this.parentAlert != null)
        {
          if (this.allowDrawContent)
            break label99;
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (PhotoViewer.this.parentAlert != null)
                PhotoViewer.this.parentAlert.setAllowDrawContent(PhotoViewer.BackgroundDrawable.this.allowDrawContent);
            }
          }
          , 50L);
        }
      }
      while (true)
      {
        super.setAlpha(paramInt);
        return;
        label94: bool = false;
        break;
        label99: if (PhotoViewer.this.parentAlert == null)
          continue;
        PhotoViewer.this.parentAlert.setAllowDrawContent(this.allowDrawContent);
      }
    }
  }

  public static class EmptyPhotoViewerProvider
    implements PhotoViewer.PhotoViewerProvider
  {
    public boolean allowCaption()
    {
      return true;
    }

    public boolean cancelButtonPressed()
    {
      return true;
    }

    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
    {
      return null;
    }

    public int getSelectedCount()
    {
      return 0;
    }

    public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
    {
      return null;
    }

    public boolean isPhotoChecked(int paramInt)
    {
      return false;
    }

    public boolean scaleToFill()
    {
      return false;
    }

    public void sendButtonPressed(int paramInt, VideoEditedInfo paramVideoEditedInfo)
    {
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
  }

  private class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto
  {
    private Paint paint = new Paint();

    public FrameLayoutDrawer(Context arg2)
    {
      super();
      setWillNotDraw(false);
      this.paint.setColor(855638016);
    }

    protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
    {
      if ((paramView == PhotoViewer.this.mentionListView) || (paramView == PhotoViewer.this.captionEditText))
        if ((PhotoViewer.this.captionEditText.isPopupShowing()) || (PhotoViewer.this.captionEditText.getEmojiPadding() != 0) || (((!AndroidUtilities.usingHardwareInput) || (getTag() != null)) && (getKeyboardHeight() != 0)))
          break label243;
      label243: 
      do
      {
        return false;
        if ((paramView != PhotoViewer.this.pickerView) && (paramView != PhotoViewer.this.captionTextViewNew) && (paramView != PhotoViewer.this.captionTextViewOld) && (paramView != PhotoViewer.this.checkImageView) && (paramView != PhotoViewer.this.videoTimelineViewContainer) && ((PhotoViewer.this.muteItem.getVisibility() != 0) || (paramView != PhotoViewer.this.bottomLayout)))
          continue;
        if ((getKeyboardHeight() <= AndroidUtilities.dp(20.0F)) && (!AndroidUtilities.isInMultiwindow));
        for (int i = PhotoViewer.this.captionEditText.getEmojiPadding(); (PhotoViewer.this.captionEditText.isPopupShowing()) || ((AndroidUtilities.usingHardwareInput) && (getTag() != null)) || (getKeyboardHeight() > 0) || (i != 0); i = 0)
        {
          PhotoViewer.access$2802(PhotoViewer.this, false);
          return false;
        }
        PhotoViewer.access$2802(PhotoViewer.this, true);
      }
      while ((paramView == PhotoViewer.this.aspectRatioFrameLayout) || (!super.drawChild(paramCanvas, paramView, paramLong)));
      return true;
    }

    protected void onDraw(Canvas paramCanvas)
    {
      PhotoViewer.this.onDraw(paramCanvas);
      if ((Build.VERSION.SDK_INT >= 21) && (AndroidUtilities.statusBarHeight != 0))
        paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), AndroidUtilities.statusBarHeight, this.paint);
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int n = getChildCount();
      int k;
      if ((getKeyboardHeight() <= AndroidUtilities.dp(20.0F)) && (!AndroidUtilities.isInMultiwindow))
        k = PhotoViewer.this.captionEditText.getEmojiPadding();
      while (true)
      {
        int m = 0;
        View localView;
        while (true)
        {
          if (m >= n)
            break label430;
          localView = getChildAt(m);
          if (localView.getVisibility() == 8)
          {
            m += 1;
            continue;
            k = 0;
            break;
          }
        }
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
        int i1 = localView.getMeasuredWidth();
        int i2 = localView.getMeasuredHeight();
        int j = localLayoutParams.gravity;
        int i = j;
        if (j == -1)
          i = 51;
        switch (i & 0x7 & 0x7)
        {
        default:
          j = localLayoutParams.leftMargin;
          label167: switch (i & 0x70)
          {
          default:
            i = localLayoutParams.topMargin;
            label215: if (localView == PhotoViewer.this.mentionListView)
              i -= PhotoViewer.this.captionEditText.getMeasuredHeight();
          case 48:
          case 16:
          case 80:
          }
        case 1:
        case 5:
        }
        while (true)
        {
          localView.layout(j, i, i1 + j, i2 + i);
          break;
          j = (paramInt3 - paramInt1 - i1) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
          break label167;
          j = paramInt3 - paramInt1 - i1 - localLayoutParams.rightMargin;
          break label167;
          i = localLayoutParams.topMargin;
          break label215;
          i = (paramInt4 - k - paramInt2 - i2) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
          break label215;
          i = paramInt4 - k - paramInt2 - i2 - localLayoutParams.bottomMargin;
          break label215;
          if (!PhotoViewer.this.captionEditText.isPopupView(localView))
            continue;
          if (AndroidUtilities.isInMultiwindow)
          {
            i = PhotoViewer.this.captionEditText.getTop() - localView.getMeasuredHeight() + AndroidUtilities.dp(1.0F);
            continue;
          }
          i = PhotoViewer.this.captionEditText.getBottom();
        }
      }
      label430: notifyHeightChanged();
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int k = View.MeasureSpec.getSize(paramInt1);
      int m = View.MeasureSpec.getSize(paramInt2);
      setMeasuredDimension(k, m);
      measureChildWithMargins(PhotoViewer.this.captionEditText, paramInt1, 0, paramInt2, 0);
      int n = PhotoViewer.this.captionEditText.getMeasuredHeight();
      int i1 = getChildCount();
      int i = 0;
      if (i < i1)
      {
        View localView = getChildAt(i);
        if ((localView.getVisibility() == 8) || (localView == PhotoViewer.this.captionEditText));
        while (true)
        {
          i += 1;
          break;
          if (localView == PhotoViewer.this.aspectRatioFrameLayout)
          {
            int i2 = AndroidUtilities.displaySize.y;
            if (Build.VERSION.SDK_INT >= 21);
            for (int j = AndroidUtilities.statusBarHeight; ; j = 0)
            {
              measureChildWithMargins(localView, paramInt1, 0, View.MeasureSpec.makeMeasureSpec(j + i2, 1073741824), 0);
              break;
            }
          }
          if (PhotoViewer.this.captionEditText.isPopupView(localView))
          {
            if (AndroidUtilities.isInMultiwindow)
            {
              if (AndroidUtilities.isTablet())
              {
                localView.measure(View.MeasureSpec.makeMeasureSpec(k, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0F), m - n - AndroidUtilities.statusBarHeight), 1073741824));
                continue;
              }
              localView.measure(View.MeasureSpec.makeMeasureSpec(k, 1073741824), View.MeasureSpec.makeMeasureSpec(m - n - AndroidUtilities.statusBarHeight, 1073741824));
              continue;
            }
            localView.measure(View.MeasureSpec.makeMeasureSpec(k, 1073741824), View.MeasureSpec.makeMeasureSpec(localView.getLayoutParams().height, 1073741824));
            continue;
          }
          measureChildWithMargins(localView, paramInt1, 0, paramInt2, 0);
        }
      }
    }
  }

  private class PhotoProgressView
  {
    private float alpha = 1.0F;
    private float animatedAlphaValue = 1.0F;
    private float animatedProgressValue = 0.0F;
    private float animationProgressStart = 0.0F;
    private int backgroundState = -1;
    private float currentProgress = 0.0F;
    private long currentProgressTime = 0L;
    private long lastUpdateTime = 0L;
    private View parent = null;
    private int previousBackgroundState = -2;
    private RectF progressRect = new RectF();
    private float radOffset = 0.0F;
    private float scale = 1.0F;
    private int size = AndroidUtilities.dp(64.0F);

    public PhotoProgressView(Context paramView, View arg3)
    {
      if (PhotoViewer.decelerateInterpolator == null)
      {
        PhotoViewer.access$1302(new DecelerateInterpolator(1.5F));
        PhotoViewer.access$1402(new Paint(1));
        PhotoViewer.progressPaint.setStyle(Paint.Style.STROKE);
        PhotoViewer.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        PhotoViewer.progressPaint.setStrokeWidth(AndroidUtilities.dp(3.0F));
        PhotoViewer.progressPaint.setColor(-1);
      }
      Object localObject;
      this.parent = localObject;
    }

    private void updateAnimation()
    {
      long l1 = System.currentTimeMillis();
      long l2 = l1 - this.lastUpdateTime;
      this.lastUpdateTime = l1;
      float f1;
      if (this.animatedProgressValue != 1.0F)
      {
        this.radOffset += (float)(360L * l2) / 3000.0F;
        f1 = this.currentProgress - this.animationProgressStart;
        if (f1 > 0.0F)
        {
          this.currentProgressTime += l2;
          if (this.currentProgressTime < 300L)
            break label172;
          this.animatedProgressValue = this.currentProgress;
          this.animationProgressStart = this.currentProgress;
          this.currentProgressTime = 0L;
        }
      }
      while (true)
      {
        this.parent.invalidate();
        if ((this.animatedProgressValue >= 1.0F) && (this.previousBackgroundState != -2))
        {
          this.animatedAlphaValue -= (float)l2 / 200.0F;
          if (this.animatedAlphaValue <= 0.0F)
          {
            this.animatedAlphaValue = 0.0F;
            this.previousBackgroundState = -2;
          }
          this.parent.invalidate();
        }
        return;
        label172: float f2 = this.animationProgressStart;
        this.animatedProgressValue = (f1 * PhotoViewer.decelerateInterpolator.getInterpolation((float)this.currentProgressTime / 300.0F) + f2);
      }
    }

    public void onDraw(Canvas paramCanvas)
    {
      int i = (int)(this.size * this.scale);
      int j = (PhotoViewer.this.getContainerViewWidth() - i) / 2;
      int k = (PhotoViewer.this.getContainerViewHeight() - i) / 2;
      Drawable localDrawable;
      if ((this.previousBackgroundState >= 0) && (this.previousBackgroundState < 4))
      {
        localDrawable = PhotoViewer.progressDrawables[this.previousBackgroundState];
        if (localDrawable != null)
        {
          localDrawable.setAlpha((int)(this.animatedAlphaValue * 255.0F * this.alpha));
          localDrawable.setBounds(j, k, j + i, k + i);
          localDrawable.draw(paramCanvas);
        }
      }
      if ((this.backgroundState >= 0) && (this.backgroundState < 4))
      {
        localDrawable = PhotoViewer.progressDrawables[this.backgroundState];
        if (localDrawable != null)
        {
          if (this.previousBackgroundState == -2)
            break label320;
          localDrawable.setAlpha((int)((1.0F - this.animatedAlphaValue) * 255.0F * this.alpha));
          localDrawable.setBounds(j, k, j + i, k + i);
          localDrawable.draw(paramCanvas);
        }
      }
      int m;
      if ((this.backgroundState == 0) || (this.backgroundState == 1) || (this.previousBackgroundState == 0) || (this.previousBackgroundState == 1))
      {
        m = AndroidUtilities.dp(4.0F);
        if (this.previousBackgroundState == -2)
          break label336;
        PhotoViewer.progressPaint.setAlpha((int)(this.animatedAlphaValue * 255.0F * this.alpha));
      }
      while (true)
      {
        this.progressRect.set(j + m, k + m, j + i - m, i + k - m);
        paramCanvas.drawArc(this.progressRect, this.radOffset - 90.0F, Math.max(4.0F, 360.0F * this.animatedProgressValue), false, PhotoViewer.progressPaint);
        updateAnimation();
        return;
        label320: localDrawable.setAlpha((int)(this.alpha * 255.0F));
        break;
        label336: PhotoViewer.progressPaint.setAlpha((int)(this.alpha * 255.0F));
      }
    }

    public void setAlpha(float paramFloat)
    {
      this.alpha = paramFloat;
    }

    public void setBackgroundState(int paramInt, boolean paramBoolean)
    {
      this.lastUpdateTime = System.currentTimeMillis();
      if ((paramBoolean) && (this.backgroundState != paramInt))
      {
        this.previousBackgroundState = this.backgroundState;
        this.animatedAlphaValue = 1.0F;
      }
      while (true)
      {
        this.backgroundState = paramInt;
        this.parent.invalidate();
        return;
        this.previousBackgroundState = -2;
      }
    }

    public void setProgress(float paramFloat, boolean paramBoolean)
    {
      if (!paramBoolean)
        this.animatedProgressValue = paramFloat;
      for (this.animationProgressStart = paramFloat; ; this.animationProgressStart = this.animatedProgressValue)
      {
        this.currentProgress = paramFloat;
        this.currentProgressTime = 0L;
        return;
      }
    }

    public void setScale(float paramFloat)
    {
      this.scale = paramFloat;
    }
  }

  public static abstract interface PhotoViewerProvider
  {
    public abstract boolean allowCaption();

    public abstract boolean cancelButtonPressed();

    public abstract PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt);

    public abstract int getSelectedCount();

    public abstract Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt);

    public abstract boolean isPhotoChecked(int paramInt);

    public abstract boolean scaleToFill();

    public abstract void sendButtonPressed(int paramInt, VideoEditedInfo paramVideoEditedInfo);

    public abstract void setPhotoChecked(int paramInt);

    public abstract void updatePhotoAtIndex(int paramInt);

    public abstract void willHidePhotoViewer();

    public abstract void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt);
  }

  public static class PlaceProviderObject
  {
    public int clipBottomAddition;
    public int clipTopAddition;
    public int dialogId;
    public ImageReceiver imageReceiver;
    public int index;
    public View parentView;
    public int radius;
    public float scale = 1.0F;
    public int size;
    public Bitmap thumb;
    public int viewX;
    public int viewY;
  }

  private class QualityChooseView extends View
  {
    private int circleSize;
    private int gapSize;
    private int lineSize;
    private boolean moving;
    private Paint paint = new Paint(1);
    private int sideSide;
    private boolean startMoving;
    private int startMovingQuality;
    private float startX;
    private TextPaint textPaint = new TextPaint(1);

    public QualityChooseView(Context arg2)
    {
      super();
      this.textPaint.setTextSize(AndroidUtilities.dp(12.0F));
      this.textPaint.setColor(-3289651);
    }

    protected void onDraw(Canvas paramCanvas)
    {
      int j = getMeasuredHeight() / 2 + AndroidUtilities.dp(6.0F);
      int i = 0;
      if (i < PhotoViewer.this.compressionsCount)
      {
        int k = this.sideSide + (this.lineSize + this.gapSize * 2 + this.circleSize) * i + this.circleSize / 2;
        label83: String str;
        label124: float f2;
        float f3;
        float f4;
        float f1;
        if (i <= PhotoViewer.this.selectedCompression)
        {
          this.paint.setColor(-11292945);
          if (i != PhotoViewer.this.compressionsCount - 1)
            break label284;
          str = PhotoViewer.this.originalHeight + "p";
          f2 = this.textPaint.measureText(str);
          f3 = k;
          f4 = j;
          if (i != PhotoViewer.this.selectedCompression)
            break label329;
          f1 = AndroidUtilities.dp(8.0F);
        }
        while (true)
        {
          paramCanvas.drawCircle(f3, f4, f1, this.paint);
          paramCanvas.drawText(str, k - f2 / 2.0F, j - AndroidUtilities.dp(16.0F), this.textPaint);
          if (i != 0)
          {
            k = k - this.circleSize / 2 - this.gapSize - this.lineSize;
            paramCanvas.drawRect(k, j - AndroidUtilities.dp(1.0F), k + this.lineSize, AndroidUtilities.dp(2.0F) + j, this.paint);
          }
          i += 1;
          break;
          this.paint.setColor(1728053247);
          break label83;
          label284: if (i == 0)
          {
            str = "240p";
            break label124;
          }
          if (i == 1)
          {
            str = "360p";
            break label124;
          }
          if (i == 2)
          {
            str = "480p";
            break label124;
          }
          str = "720p";
          break label124;
          label329: f1 = this.circleSize / 2;
        }
      }
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
      View.MeasureSpec.getSize(paramInt1);
      this.circleSize = AndroidUtilities.dp(12.0F);
      this.gapSize = AndroidUtilities.dp(2.0F);
      this.sideSide = AndroidUtilities.dp(18.0F);
      this.lineSize = ((getMeasuredWidth() - this.circleSize * PhotoViewer.this.compressionsCount - this.gapSize * 8 - this.sideSide * 2) / (PhotoViewer.this.compressionsCount - 1));
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool = false;
      float f = paramMotionEvent.getX();
      int i;
      int j;
      if (paramMotionEvent.getAction() == 0)
      {
        getParent().requestDisallowInterceptTouchEvent(true);
        i = 0;
        if (i < PhotoViewer.this.compressionsCount)
        {
          j = this.sideSide + (this.lineSize + this.gapSize * 2 + this.circleSize) * i + this.circleSize / 2;
          if ((f <= j - AndroidUtilities.dp(15.0F)) || (f >= j + AndroidUtilities.dp(15.0F)))
            break label136;
          if (i == PhotoViewer.this.selectedCompression)
            bool = true;
          this.startMoving = bool;
          this.startX = f;
          this.startMovingQuality = PhotoViewer.this.selectedCompression;
        }
      }
      label136: label322: label324: 
      do
        while (true)
        {
          return true;
          i += 1;
          break;
          if (paramMotionEvent.getAction() != 2)
            break label324;
          if (this.startMoving)
          {
            if (Math.abs(this.startX - f) < AndroidUtilities.getPixelsInCM(0.5F, true))
              continue;
            this.moving = true;
            this.startMoving = false;
            return true;
          }
          if (!this.moving)
            continue;
          i = 0;
          while (true)
          {
            if (i >= PhotoViewer.this.compressionsCount)
              break label322;
            j = this.sideSide + (this.lineSize + this.gapSize * 2 + this.circleSize) * i + this.circleSize / 2;
            int k = this.lineSize / 2 + this.circleSize / 2 + this.gapSize;
            if ((f > j - k) && (f < j + k))
            {
              if (PhotoViewer.this.selectedCompression == i)
                break;
              PhotoViewer.access$6502(PhotoViewer.this, i);
              PhotoViewer.this.didChangedCompressionLevel(false);
              invalidate();
              return true;
            }
            i += 1;
          }
        }
      while ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3));
      if (!this.moving)
      {
        i = 0;
        if (i < PhotoViewer.this.compressionsCount)
        {
          j = this.sideSide + (this.lineSize + this.gapSize * 2 + this.circleSize) * i + this.circleSize / 2;
          if ((f <= j - AndroidUtilities.dp(15.0F)) || (f >= j + AndroidUtilities.dp(15.0F)))
            break label464;
          if (PhotoViewer.this.selectedCompression != i)
          {
            PhotoViewer.access$6502(PhotoViewer.this, i);
            PhotoViewer.this.didChangedCompressionLevel(true);
            invalidate();
          }
        }
      }
      while (true)
      {
        this.startMoving = false;
        this.moving = false;
        return true;
        label464: i += 1;
        break;
        if (PhotoViewer.this.selectedCompression == this.startMovingQuality)
          continue;
        PhotoViewer.this.requestVideoPreview(1);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.PhotoViewer
 * JD-Core Version:    0.6.0
 */