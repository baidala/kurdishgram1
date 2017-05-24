package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.f;
import android.support.v4.view.ab;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MediaController.FileDownloadProgressListener;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.browser.Browser;
import org.vidogram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.vidogram.messenger.support.widget.GridLayoutManager;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.State;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.messenger.time.FastDateFormat;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Page;
import org.vidogram.tgnet.TLRPC.PageBlock;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.RichText;
import org.vidogram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.vidogram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.vidogram.tgnet.TLRPC.TL_messages_getWebPage;
import org.vidogram.tgnet.TLRPC.TL_pageBlockAnchor;
import org.vidogram.tgnet.TLRPC.TL_pageBlockAuthorDate;
import org.vidogram.tgnet.TLRPC.TL_pageBlockBlockquote;
import org.vidogram.tgnet.TLRPC.TL_pageBlockCollage;
import org.vidogram.tgnet.TLRPC.TL_pageBlockCover;
import org.vidogram.tgnet.TLRPC.TL_pageBlockDivider;
import org.vidogram.tgnet.TLRPC.TL_pageBlockEmbed;
import org.vidogram.tgnet.TLRPC.TL_pageBlockEmbedPost;
import org.vidogram.tgnet.TLRPC.TL_pageBlockFooter;
import org.vidogram.tgnet.TLRPC.TL_pageBlockHeader;
import org.vidogram.tgnet.TLRPC.TL_pageBlockList;
import org.vidogram.tgnet.TLRPC.TL_pageBlockParagraph;
import org.vidogram.tgnet.TLRPC.TL_pageBlockPhoto;
import org.vidogram.tgnet.TLRPC.TL_pageBlockPreformatted;
import org.vidogram.tgnet.TLRPC.TL_pageBlockPullquote;
import org.vidogram.tgnet.TLRPC.TL_pageBlockSlideshow;
import org.vidogram.tgnet.TLRPC.TL_pageBlockSubheader;
import org.vidogram.tgnet.TLRPC.TL_pageBlockSubtitle;
import org.vidogram.tgnet.TLRPC.TL_pageBlockTitle;
import org.vidogram.tgnet.TLRPC.TL_pageBlockUnsupported;
import org.vidogram.tgnet.TLRPC.TL_pageBlockVideo;
import org.vidogram.tgnet.TLRPC.TL_pageFull;
import org.vidogram.tgnet.TLRPC.TL_textBold;
import org.vidogram.tgnet.TLRPC.TL_textConcat;
import org.vidogram.tgnet.TLRPC.TL_textEmail;
import org.vidogram.tgnet.TLRPC.TL_textEmpty;
import org.vidogram.tgnet.TLRPC.TL_textFixed;
import org.vidogram.tgnet.TLRPC.TL_textItalic;
import org.vidogram.tgnet.TLRPC.TL_textPlain;
import org.vidogram.tgnet.TLRPC.TL_textStrike;
import org.vidogram.tgnet.TLRPC.TL_textUnderline;
import org.vidogram.tgnet.TLRPC.TL_textUrl;
import org.vidogram.tgnet.TLRPC.TL_webPage;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.WebPage;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BackDrawable;
import org.vidogram.ui.ActionBar.BottomSheet.Builder;
import org.vidogram.ui.ActionBar.DrawerLayoutContainer;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.AnimatedFileDrawable;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.ClippingImageView;
import org.vidogram.ui.Components.ContextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.LinkPath;
import org.vidogram.ui.Components.RadialProgress;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;
import org.vidogram.ui.Components.Scroller;
import org.vidogram.ui.Components.SeekBar;
import org.vidogram.ui.Components.SeekBar.SeekBarDelegate;
import org.vidogram.ui.Components.ShareAlert;
import org.vidogram.ui.Components.TextPaintSpan;
import org.vidogram.ui.Components.TextPaintUrlSpan;
import org.vidogram.ui.Components.VideoPlayer;
import org.vidogram.ui.Components.VideoPlayer.VideoPlayerDelegate;
import org.vidogram.ui.Components.WebPlayerView;
import org.vidogram.ui.Components.WebPlayerView.WebPlayerViewDelegate;

@TargetApi(16)
public class ArticleViewer
  implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener, NotificationCenter.NotificationCenterDelegate
{

  @SuppressLint({"StaticFieldLeak"})
  private static volatile ArticleViewer Instance = null;
  private static final int TEXT_FLAG_ITALIC = 2;
  private static final int TEXT_FLAG_MEDIUM = 1;
  private static final int TEXT_FLAG_MONO = 4;
  private static final int TEXT_FLAG_REGULAR = 0;
  private static final int TEXT_FLAG_STRIKE = 32;
  private static final int TEXT_FLAG_UNDERLINE = 16;
  private static final int TEXT_FLAG_URL = 8;
  private static HashMap<Integer, TextPaint> authorTextPaints;
  private static HashMap<Integer, TextPaint> captionTextPaints = new HashMap();
  private static DecelerateInterpolator decelerateInterpolator;
  private static Paint dividerPaint;
  private static Paint dotsPaint;
  private static TextPaint embedPostAuthorPaint;
  private static HashMap<Integer, TextPaint> embedPostCaptionTextPaints;
  private static TextPaint embedPostDatePaint;
  private static HashMap<Integer, TextPaint> embedPostTextPaints;
  private static HashMap<Integer, TextPaint> embedTextPaints;
  private static TextPaint errorTextPaint;
  private static HashMap<Integer, TextPaint> footerTextPaints;
  private static final int gallery_menu_openin = 3;
  private static final int gallery_menu_save = 1;
  private static final int gallery_menu_share = 2;
  private static HashMap<Integer, TextPaint> headerTextPaints;
  private static HashMap<Integer, TextPaint> listTextPaints;
  private static HashMap<Integer, TextPaint> paragraphTextPaints;
  private static Paint preformattedBackgroundPaint;
  private static HashMap<Integer, TextPaint> preformattedTextPaints;
  private static Drawable[] progressDrawables;
  private static Paint progressPaint;
  private static Paint quoteLinePaint;
  private static HashMap<Integer, TextPaint> quoteTextPaints;
  private static HashMap<Integer, TextPaint> slideshowTextPaints;
  private static HashMap<Integer, TextPaint> subheaderTextPaints;
  private static HashMap<Integer, TextPaint> subquoteTextPaints;
  private static HashMap<Integer, TextPaint> subtitleTextPaints;
  private static HashMap<Integer, TextPaint> titleTextPaints = new HashMap();
  private static Paint urlPaint;
  private static HashMap<Integer, TextPaint> videoTextPaints;
  private ActionBar actionBar;
  private WebpageAdapter adapter;
  public HashMap<String, Integer> anchors = new HashMap();
  private float animateToScale;
  private float animateToX;
  private float animateToY;
  private ClippingImageView animatingImageView;
  private Runnable animationEndRunnable;
  private int animationInProgress;
  private long animationStartTime;
  private float animationValue;
  private float[][] animationValues = (float[][])Array.newInstance(Float.TYPE, new int[] { 2, 8 });
  private AspectRatioFrameLayout aspectRatioFrameLayout;
  private boolean attachedToWindow;
  private ImageView backButton;
  private BackDrawable backDrawable;
  private Paint backgroundPaint;
  private View barBackground;
  private Paint blackPaint = new Paint();
  public ArrayList<TLRPC.PageBlock> blocks = new ArrayList();
  private FrameLayout bottomLayout;
  private boolean canDragDown = true;
  private boolean canZoom = true;
  private TextView captionTextView;
  private TextView captionTextViewNew;
  private TextView captionTextViewOld;
  private ImageReceiver centerImage = new ImageReceiver();
  private boolean changingPage;
  private boolean checkingForLongPress = false;
  private boolean collapsed;
  private FrameLayout containerView;
  private int[] coords = new int[2];
  private ArrayList<BlockEmbedCell> createdWebViews = new ArrayList();
  private AnimatorSet currentActionBarAnimation;
  private AnimatedFileDrawable currentAnimation;
  private String[] currentFileNames = new String[3];
  private int currentHeaderHeight;
  private int currentIndex;
  private TLRPC.PageBlock currentMedia;
  private TLRPC.WebPage currentPage;
  private PlaceProviderObject currentPlaceObject;
  private WebPlayerView currentPlayingVideo;
  private int currentRotation;
  private Bitmap currentThumb;
  private View customView;
  private WebChromeClient.CustomViewCallback customViewCallback;
  private boolean disableShowCheck;
  private boolean discardTap;
  private boolean dontResetZoomOnFirstLayout;
  private boolean doubleTap;
  private float dragY;
  private boolean draggingDown;
  private AspectRatioFrameLayout fullscreenAspectRatioView;
  private TextureView fullscreenTextureView;
  private FrameLayout fullscreenVideoContainer;
  private WebPlayerView fullscreenedVideo;
  private GestureDetector gestureDetector;
  private FrameLayout headerView;
  private PlaceProviderObject hideAfterAnimation;
  private AnimatorSet imageMoveAnimation;
  private ArrayList<TLRPC.PageBlock> imagesArr = new ArrayList();
  private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5F);
  private boolean invalidCoords;
  private boolean isActionBarVisible = true;
  private boolean isPhotoVisible;
  private boolean isPlaying;
  private boolean isVisible;
  private Object lastInsets;
  private Drawable layerShadowDrawable;
  private LinearLayoutManager layoutManager;
  private ImageReceiver leftImage = new ImageReceiver();
  private RecyclerListView listView;
  private float maxX;
  private float maxY;
  private ActionBarMenuItem menuItem;
  private float minX;
  private float minY;
  private float moveStartX;
  private float moveStartY;
  private boolean moving;
  private int openUrlReqId;
  private ArrayList<TLRPC.WebPage> pagesStack = new ArrayList();
  private Activity parentActivity;
  private CheckForLongPress pendingCheckForLongPress = null;
  private CheckForTap pendingCheckForTap = null;
  private Runnable photoAnimationEndRunnable;
  private int photoAnimationInProgress;
  private PhotoBackgroundDrawable photoBackgroundDrawable = new PhotoBackgroundDrawable(-16777216);
  public ArrayList<TLRPC.PageBlock> photoBlocks = new ArrayList();
  private View photoContainerBackground;
  private FrameLayoutDrawer photoContainerView;
  private long photoTransitionAnimationStartTime;
  private float pinchCenterX;
  private float pinchCenterY;
  private float pinchStartDistance;
  private float pinchStartScale = 1.0F;
  private float pinchStartX;
  private float pinchStartY;
  private int pressCount = 0;
  private TextPaintUrlSpan pressedLink;
  private StaticLayout pressedLinkOwnerLayout;
  private View pressedLinkOwnerView;
  private int previewsReqId;
  private ContextProgressView progressView;
  private AnimatorSet progressViewAnimation;
  private RadialProgressView[] radialProgressViews = new RadialProgressView[3];
  private ImageReceiver rightImage = new ImageReceiver();
  private float scale = 1.0F;
  private Paint scrimPaint;
  private Scroller scroller;
  private ImageView shareButton;
  private FrameLayout shareContainer;
  private PlaceProviderObject showAfterAnimation;
  private Drawable slideDotBigDrawable;
  private Drawable slideDotDrawable;
  private int switchImageAfterAnimation;
  private boolean textureUploaded;
  private long transitionAnimationStartTime;
  private float translationX;
  private float translationY;
  private Runnable updateProgressRunnable = new Runnable()
  {
    public void run()
    {
      if ((ArticleViewer.this.videoPlayer != null) && (ArticleViewer.this.videoPlayerSeekbar != null) && (!ArticleViewer.this.videoPlayerSeekbar.isDragging()))
      {
        float f = (float)ArticleViewer.this.videoPlayer.getCurrentPosition() / (float)ArticleViewer.this.videoPlayer.getDuration();
        ArticleViewer.this.videoPlayerSeekbar.setProgress(f);
        ArticleViewer.this.videoPlayerControlFrameLayout.invalidate();
        ArticleViewer.this.updateVideoPlayerTime();
      }
      if (ArticleViewer.this.isPlaying)
        AndroidUtilities.runOnUIThread(ArticleViewer.this.updateProgressRunnable, 100L);
    }
  };
  private LinkPath urlPath = new LinkPath();
  private VelocityTracker velocityTracker;
  private float videoCrossfadeAlpha;
  private long videoCrossfadeAlphaLastTime;
  private boolean videoCrossfadeStarted;
  private ImageView videoPlayButton;
  private VideoPlayer videoPlayer;
  private FrameLayout videoPlayerControlFrameLayout;
  private SeekBar videoPlayerSeekbar;
  private TextView videoPlayerTime;
  private TextureView videoTextureView;
  private Dialog visibleDialog;
  private boolean wasLayout;
  private WindowManager.LayoutParams windowLayoutParams;
  private WindowView windowView;
  private boolean zoomAnimation;
  private boolean zooming;

  static
  {
    headerTextPaints = new HashMap();
    subtitleTextPaints = new HashMap();
    subheaderTextPaints = new HashMap();
    authorTextPaints = new HashMap();
    footerTextPaints = new HashMap();
    paragraphTextPaints = new HashMap();
    listTextPaints = new HashMap();
    preformattedTextPaints = new HashMap();
    quoteTextPaints = new HashMap();
    subquoteTextPaints = new HashMap();
    embedTextPaints = new HashMap();
    slideshowTextPaints = new HashMap();
    embedPostTextPaints = new HashMap();
    embedPostCaptionTextPaints = new HashMap();
    videoTextPaints = new HashMap();
  }

  private void addAllMediaFromBlock(TLRPC.PageBlock paramPageBlock)
  {
    if (((paramPageBlock instanceof TLRPC.TL_pageBlockPhoto)) || (((paramPageBlock instanceof TLRPC.TL_pageBlockVideo)) && (isVideoBlock(paramPageBlock))))
      this.photoBlocks.add(paramPageBlock);
    do
      while (true)
      {
        return;
        TLRPC.PageBlock localPageBlock;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockSlideshow))
        {
          localObject = (TLRPC.TL_pageBlockSlideshow)paramPageBlock;
          j = ((TLRPC.TL_pageBlockSlideshow)localObject).items.size();
          i = 0;
          while (i < j)
          {
            localPageBlock = (TLRPC.PageBlock)((TLRPC.TL_pageBlockSlideshow)localObject).items.get(i);
            if (((localPageBlock instanceof TLRPC.TL_pageBlockPhoto)) || (((localPageBlock instanceof TLRPC.TL_pageBlockVideo)) && (isVideoBlock(paramPageBlock))))
              this.photoBlocks.add(localPageBlock);
            i += 1;
          }
          continue;
        }
        if (!(paramPageBlock instanceof TLRPC.TL_pageBlockCollage))
          break;
        Object localObject = (TLRPC.TL_pageBlockCollage)paramPageBlock;
        int j = ((TLRPC.TL_pageBlockCollage)localObject).items.size();
        int i = 0;
        while (i < j)
        {
          localPageBlock = (TLRPC.PageBlock)((TLRPC.TL_pageBlockCollage)localObject).items.get(i);
          if (((localPageBlock instanceof TLRPC.TL_pageBlockPhoto)) || (((localPageBlock instanceof TLRPC.TL_pageBlockVideo)) && (isVideoBlock(paramPageBlock))))
            this.photoBlocks.add(localPageBlock);
          i += 1;
        }
      }
    while ((!(paramPageBlock instanceof TLRPC.TL_pageBlockCover)) || ((!(paramPageBlock.cover instanceof TLRPC.TL_pageBlockPhoto)) && ((!(paramPageBlock.cover instanceof TLRPC.TL_pageBlockVideo)) || (!isVideoBlock(paramPageBlock.cover)))));
    this.photoBlocks.add(paramPageBlock.cover);
  }

  private void addPageToStack(TLRPC.WebPage paramWebPage, String paramString)
  {
    saveCurrentPagePosition();
    this.currentPage = paramWebPage;
    this.pagesStack.add(paramWebPage);
    updateInterfaceForCurrentPage(false);
    if (paramString != null)
    {
      paramWebPage = (Integer)this.anchors.get(paramString);
      if (paramWebPage != null)
        this.layoutManager.scrollToPositionWithOffset(paramWebPage.intValue(), 0);
    }
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
        ArticleViewer.access$11802(ArticleViewer.this, null);
        ArticleViewer.this.photoContainerView.invalidate();
      }
    });
    this.imageMoveAnimation.start();
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

  private boolean checkLayoutForLinks(MotionEvent paramMotionEvent, View paramView, StaticLayout paramStaticLayout, int paramInt1, int paramInt2)
  {
    if ((paramView == null) || (paramStaticLayout == null))
      return false;
    if (!(paramStaticLayout.getText() instanceof Spannable))
      return false;
    int j = (int)paramMotionEvent.getX();
    int i = (int)paramMotionEvent.getY();
    if (paramMotionEvent.getAction() == 0)
    {
      if ((j < paramInt1) || (j > paramStaticLayout.getWidth() + paramInt1) || (i < paramInt2) || (i > paramStaticLayout.getHeight() + paramInt2))
        break label629;
      paramInt1 = j - paramInt1;
    }
    while (true)
    {
      Object localObject;
      try
      {
        paramInt2 = paramStaticLayout.getLineForVertical(i - paramInt2);
        i = paramStaticLayout.getOffsetForHorizontal(paramInt2, paramInt1);
        float f = paramStaticLayout.getLineLeft(paramInt2);
        if ((f > paramInt1) || (paramStaticLayout.getLineWidth(paramInt2) + f < paramInt1))
          continue;
        localObject = (Spannable)paramStaticLayout.getText();
        TextPaintUrlSpan[] arrayOfTextPaintUrlSpan = (TextPaintUrlSpan[])((Spannable)localObject).getSpans(i, i, TextPaintUrlSpan.class);
        if ((arrayOfTextPaintUrlSpan == null) || (arrayOfTextPaintUrlSpan.length <= 0))
          continue;
        this.pressedLink = arrayOfTextPaintUrlSpan[0];
        paramInt1 = ((Spannable)localObject).getSpanStart(this.pressedLink);
        i = ((Spannable)localObject).getSpanEnd(this.pressedLink);
        paramInt2 = 1;
        if (paramInt2 >= arrayOfTextPaintUrlSpan.length)
          continue;
        TextPaintUrlSpan localTextPaintUrlSpan = arrayOfTextPaintUrlSpan[paramInt2];
        j = ((Spannable)localObject).getSpanStart(localTextPaintUrlSpan);
        int k = ((Spannable)localObject).getSpanEnd(localTextPaintUrlSpan);
        if ((paramInt1 <= j) && (k <= i))
          break label644;
        this.pressedLink = localTextPaintUrlSpan;
        i = k;
        paramInt1 = j;
        break label644;
        this.pressedLinkOwnerLayout = paramStaticLayout;
        this.pressedLinkOwnerView = paramView;
        try
        {
          this.urlPath.setCurrentLayout(paramStaticLayout, paramInt1, 0.0F);
          paramStaticLayout.getSelectionPath(paramInt1, i, this.urlPath);
          paramView.invalidate();
          paramInt1 = 0;
          if ((paramInt1 == 0) || (this.pressedLink == null))
            continue;
          this.pressedLink = null;
          this.pressedLinkOwnerLayout = null;
          this.pressedLinkOwnerView = null;
          paramView.invalidate();
          if ((this.pressedLink == null) || (paramMotionEvent.getAction() != 0))
            continue;
          startCheckLongPress();
          if ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2))
            continue;
          cancelCheckLongPress();
          if (this.pressedLink == null)
            break label627;
          return true;
        }
        catch (Exception paramStaticLayout)
        {
          FileLog.e(paramStaticLayout);
          continue;
        }
      }
      catch (Exception paramStaticLayout)
      {
        FileLog.e(paramStaticLayout);
        paramInt1 = 0;
        continue;
      }
      if (paramMotionEvent.getAction() == 1)
      {
        if (this.pressedLink == null)
          break label629;
        localObject = this.pressedLink.getUrl();
        if (localObject != null)
        {
          paramInt1 = 0;
          paramInt2 = ((String)localObject).lastIndexOf('#');
          if (paramInt2 == -1)
            break label608;
          paramStaticLayout = ((String)localObject).substring(paramInt2 + 1);
          if (!((String)localObject).toLowerCase().contains(this.currentPage.url.toLowerCase()))
            break label641;
          localObject = (Integer)this.anchors.get(paramStaticLayout);
          if (localObject == null)
            break label635;
          this.layoutManager.scrollToPositionWithOffset(((Integer)localObject).intValue(), 0);
          paramInt1 = 1;
        }
      }
      label641: 
      while (true)
      {
        if ((paramInt1 == 0) && (this.openUrlReqId == 0))
        {
          showProgressView(true);
          localObject = new TLRPC.TL_messages_getWebPage();
          ((TLRPC.TL_messages_getWebPage)localObject).url = this.pressedLink.getUrl();
          ((TLRPC.TL_messages_getWebPage)localObject).hash = 0;
          this.openUrlReqId = ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate(paramStaticLayout, (TLRPC.TL_messages_getWebPage)localObject)
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
              AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
              {
                public void run()
                {
                  if (ArticleViewer.this.openUrlReqId == 0);
                  do
                  {
                    return;
                    ArticleViewer.access$3102(ArticleViewer.this, 0);
                    ArticleViewer.this.showProgressView(false);
                  }
                  while (!ArticleViewer.this.isVisible);
                  if (((this.val$response instanceof TLRPC.TL_webPage)) && ((((TLRPC.TL_webPage)this.val$response).cached_page instanceof TLRPC.TL_pageFull)))
                  {
                    ArticleViewer.this.addPageToStack((TLRPC.TL_webPage)this.val$response, ArticleViewer.1.this.val$anchor);
                    return;
                  }
                  Browser.openUrl(ArticleViewer.this.parentActivity, ArticleViewer.1.this.val$req.url);
                }
              });
            }
          });
        }
        paramInt1 = 1;
        break;
        label608: paramStaticLayout = null;
        continue;
        if (paramMotionEvent.getAction() == 3)
        {
          paramInt1 = 1;
          break;
          label627: return false;
        }
        label629: paramInt1 = 0;
        break;
        label635: paramInt1 = 0;
        continue;
      }
      label644: paramInt2 += 1;
    }
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

  private boolean checkPhotoAnimation()
  {
    int i = 0;
    if ((this.photoAnimationInProgress != 0) && (Math.abs(this.photoTransitionAnimationStartTime - System.currentTimeMillis()) >= 500L))
    {
      if (this.photoAnimationEndRunnable != null)
      {
        this.photoAnimationEndRunnable.run();
        this.photoAnimationEndRunnable = null;
      }
      this.photoAnimationInProgress = 0;
    }
    if (this.photoAnimationInProgress != 0)
      i = 1;
    return i;
  }

  private void checkProgress(int paramInt, boolean paramBoolean)
  {
    if (this.currentFileNames[paramInt] != null)
    {
      int j = this.currentIndex;
      int i;
      Object localObject;
      boolean bool;
      if (paramInt == 1)
      {
        i = j + 1;
        localObject = getMediaFile(i);
        bool = isMediaVideo(i);
        if ((localObject == null) || (!((File)localObject).exists()))
          break label136;
        if (!bool)
          break label122;
        this.radialProgressViews[paramInt].setBackgroundState(3, paramBoolean);
        label68: if (paramInt == 0)
          if ((this.currentFileNames[0] == null) || (bool) || (this.radialProgressViews[0].backgroundState == 0))
            break label242;
      }
      label136: label242: for (paramBoolean = true; ; paramBoolean = false)
      {
        this.canZoom = paramBoolean;
        return;
        i = j;
        if (paramInt != 2)
          break;
        i = j - 1;
        break;
        label122: this.radialProgressViews[paramInt].setBackgroundState(-1, paramBoolean);
        break label68;
        if (bool)
          if (!FileLoader.getInstance().isLoadingFile(this.currentFileNames[paramInt]))
            this.radialProgressViews[paramInt].setBackgroundState(2, false);
        while (true)
        {
          Float localFloat = ImageLoader.getInstance().getFileProgress(this.currentFileNames[paramInt]);
          localObject = localFloat;
          if (localFloat == null)
            localObject = Float.valueOf(0.0F);
          this.radialProgressViews[paramInt].setProgress(((Float)localObject).floatValue(), false);
          break;
          this.radialProgressViews[paramInt].setBackgroundState(1, false);
          continue;
          this.radialProgressViews[paramInt].setBackgroundState(0, paramBoolean);
        }
      }
    }
    this.radialProgressViews[paramInt].setBackgroundState(-1, paramBoolean);
  }

  private void checkScroll(int paramInt)
  {
    int i = AndroidUtilities.dp(56.0F);
    int j = Math.max(AndroidUtilities.statusBarHeight, AndroidUtilities.dp(24.0F));
    float f = i - j;
    int k = this.currentHeaderHeight - paramInt;
    if (k < j)
      paramInt = j;
    while (true)
    {
      this.currentHeaderHeight = paramInt;
      f = 0.8F + (this.currentHeaderHeight - j) / f * 0.2F;
      paramInt = (int)(i * f);
      this.backButton.setScaleX(f);
      this.backButton.setScaleY(f);
      this.backButton.setTranslationY((i - this.currentHeaderHeight) / 2);
      this.shareContainer.setScaleX(f);
      this.shareContainer.setScaleY(f);
      this.shareContainer.setTranslationY((i - this.currentHeaderHeight) / 2);
      this.headerView.setTranslationY(this.currentHeaderHeight - i);
      this.listView.setTopGlowOffset(this.currentHeaderHeight);
      return;
      paramInt = k;
      if (k <= i)
        continue;
      paramInt = i;
    }
  }

  private StaticLayout createLayoutForText(CharSequence paramCharSequence, TLRPC.RichText paramRichText, int paramInt, TLRPC.PageBlock paramPageBlock)
  {
    if ((paramCharSequence == null) && ((paramRichText == null) || ((paramRichText instanceof TLRPC.TL_textEmpty))))
      return null;
    if (quoteLinePaint == null)
    {
      quoteLinePaint = new Paint();
      quoteLinePaint.setColor(-16777216);
      preformattedBackgroundPaint = new Paint();
      preformattedBackgroundPaint.setColor(-657156);
      urlPaint = new Paint();
      urlPaint.setColor(862104035);
    }
    CharSequence localCharSequence;
    if (paramCharSequence != null)
    {
      localCharSequence = paramCharSequence;
      label87: if (TextUtils.isEmpty(localCharSequence))
        break label196;
      if ((!(paramPageBlock instanceof TLRPC.TL_pageBlockEmbedPost)) || (paramRichText != null))
        break label244;
      if (paramPageBlock.author != paramCharSequence)
        break label198;
      if (embedPostAuthorPaint == null)
      {
        embedPostAuthorPaint = new TextPaint(1);
        embedPostAuthorPaint.setColor(-16777216);
      }
      embedPostAuthorPaint.setTextSize(AndroidUtilities.dp(15.0F));
      paramCharSequence = embedPostAuthorPaint;
    }
    while (true)
    {
      if (!(paramPageBlock instanceof TLRPC.TL_pageBlockPullquote))
        break label256;
      return new StaticLayout(localCharSequence, paramCharSequence, paramInt, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
      localCharSequence = getText(paramRichText, paramRichText, paramPageBlock);
      break label87;
      label196: break;
      label198: if (embedPostDatePaint == null)
      {
        embedPostDatePaint = new TextPaint(1);
        embedPostDatePaint.setColor(-7366752);
      }
      embedPostDatePaint.setTextSize(AndroidUtilities.dp(14.0F));
      paramCharSequence = embedPostDatePaint;
      continue;
      label244: paramCharSequence = getTextPaint(paramRichText, paramRichText, paramPageBlock);
    }
    label256: return new StaticLayout(localCharSequence, paramCharSequence, paramInt, Layout.Alignment.ALIGN_NORMAL, 1.0F, AndroidUtilities.dp(4.0F), false);
  }

  private void drawContent(Canvas paramCanvas)
  {
    if ((this.photoAnimationInProgress == 1) || ((!this.isPhotoVisible) && (this.photoAnimationInProgress != 2)))
      return;
    float f1 = -1.0F;
    float f4;
    float f5;
    float f6;
    float f7;
    float f8;
    float f3;
    float f2;
    label256: ImageReceiver localImageReceiver;
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
      this.photoContainerView.invalidate();
      f1 = (f9 - f10) * f11 + f8;
      f4 = (f5 - f6) * f7 + f4;
      if ((this.scale != 1.0F) || (f2 == -1.0F) || (this.zoomAnimation))
        break label1740;
      f5 = getContainerViewHeight() / 4.0F;
      this.photoBackgroundDrawable.setAlpha((int)Math.max(127.0F, (1.0F - Math.min(Math.abs(f2), f5) / f5) * 255.0F));
      if ((this.scale < 1.0F) || (this.zoomAnimation) || (this.zooming))
        break label1812;
      if (f1 <= this.maxX + AndroidUtilities.dp(5.0F))
        break label1753;
      localImageReceiver = this.leftImage;
    }
    while (true)
    {
      label302: boolean bool;
      if (localImageReceiver != null)
      {
        bool = true;
        label310: this.changingPage = bool;
        if (localImageReceiver == this.rightImage)
        {
          f5 = 0.0F;
          f2 = 1.0F;
          if ((this.zoomAnimation) || (f1 >= this.minX))
            break label1806;
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
            label502: j = (int)(j * f7);
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
          this.radialProgressViews[1].setScale(1.0F - f5);
          this.radialProgressViews[1].setAlpha(f2);
          this.radialProgressViews[1].onDraw(paramCanvas);
          paramCanvas.restore();
          f5 = 0.0F;
          f2 = 1.0F;
          if ((this.zoomAnimation) || (f1 <= this.maxX))
            break label1800;
          f2 = Math.min(1.0F, (f1 - this.maxX) / paramCanvas.getWidth());
          f5 = 0.3F * f2;
          f2 = 1.0F - f2;
          f6 = this.maxX;
        }
        while (true)
        {
          if ((this.aspectRatioFrameLayout != null) && (this.aspectRatioFrameLayout.getVisibility() == 0))
          {
            i = 1;
            label716: if (this.centerImage.hasBitmapImage())
            {
              paramCanvas.save();
              paramCanvas.translate(getContainerViewWidth() / 2, getContainerViewHeight() / 2);
              paramCanvas.translate(f6, f3);
              paramCanvas.scale(f4 - f5, f4 - f5);
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
                break label1794;
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
                  this.photoContainerView.invalidate();
                  if (this.videoCrossfadeAlpha > 1.0F)
                    this.videoCrossfadeAlpha = 1.0F;
                }
              }
              paramCanvas.restore();
            }
            label912: if ((i == 0) && (this.bottomLayout.getVisibility() != 0))
            {
              paramCanvas.save();
              paramCanvas.translate(f6, f3 / f4);
              this.radialProgressViews[0].setScale(1.0F - f5);
              this.radialProgressViews[0].setAlpha(f2);
              this.radialProgressViews[0].onDraw(paramCanvas);
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
                break label1797;
              f2 = f5;
            }
          }
          label1794: label1797: 
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
            this.radialProgressViews[2].setScale(1.0F);
            this.radialProgressViews[2].setAlpha(1.0F);
            this.radialProgressViews[2].onDraw(paramCanvas);
            paramCanvas.restore();
            return;
            if (this.animationStartTime != 0L)
            {
              this.translationX = this.animateToX;
              this.translationY = this.animateToY;
              this.scale = this.animateToScale;
              this.animationStartTime = 0L;
              updateMinMax(this.scale);
              this.zoomAnimation = false;
            }
            if ((!this.scroller.isFinished()) && (this.scroller.computeScrollOffset()))
            {
              if ((this.scroller.getStartX() < this.maxX) && (this.scroller.getStartX() > this.minX))
                this.translationX = this.scroller.getCurrX();
              if ((this.scroller.getStartY() < this.maxY) && (this.scroller.getStartY() > this.minY))
                this.translationY = this.scroller.getCurrY();
              this.photoContainerView.invalidate();
            }
            if (this.switchImageAfterAnimation != 0)
            {
              if (this.switchImageAfterAnimation != 1)
                break label1718;
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
              label1718: if (this.switchImageAfterAnimation != 2)
                continue;
              setImageIndex(this.currentIndex - 1, false);
            }
            label1740: this.photoBackgroundDrawable.setAlpha(255);
            break label256;
            label1753: if (f1 >= this.minX - AndroidUtilities.dp(5.0F))
              break label1812;
            localImageReceiver = this.rightImage;
            break label302;
            bool = false;
            break label310;
            break label502;
            i = 0;
            break label716;
            break label912;
          }
          label1800: f6 = f1;
        }
        label1806: f6 = f1;
      }
      label1812: localImageReceiver = null;
    }
  }

  private void drawLayoutLink(Canvas paramCanvas, StaticLayout paramStaticLayout)
  {
    if ((paramCanvas == null) || (this.pressedLink == null) || (this.pressedLinkOwnerLayout != paramStaticLayout));
    do
      return;
    while (this.pressedLink == null);
    paramCanvas.drawPath(this.urlPath, urlPaint);
  }

  private int getContainerViewHeight()
  {
    return this.photoContainerView.getHeight();
  }

  private int getContainerViewWidth()
  {
    return this.photoContainerView.getWidth();
  }

  private TLRPC.Document getDocumentWithId(long paramLong)
  {
    Object localObject;
    if ((this.currentPage == null) || (this.currentPage.cached_page == null))
    {
      localObject = null;
      return localObject;
    }
    if ((this.currentPage.document != null) && (this.currentPage.document.id == paramLong))
      return this.currentPage.document;
    int i = 0;
    while (true)
    {
      if (i >= this.currentPage.cached_page.videos.size())
        break label115;
      TLRPC.Document localDocument = (TLRPC.Document)this.currentPage.cached_page.videos.get(i);
      localObject = localDocument;
      if (localDocument.id == paramLong)
        break;
      i += 1;
    }
    label115: return null;
  }

  private TLRPC.FileLocation getFileLocation(int paramInt, int[] paramArrayOfInt)
  {
    if ((paramInt < 0) || (paramInt >= this.imagesArr.size()))
      return null;
    Object localObject = getMedia(paramInt);
    if ((localObject instanceof TLRPC.Photo))
    {
      localObject = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo)localObject).sizes, AndroidUtilities.getPhotoSize());
      if (localObject != null)
      {
        paramArrayOfInt[0] = ((TLRPC.PhotoSize)localObject).size;
        if (paramArrayOfInt[0] == 0)
          paramArrayOfInt[0] = -1;
        return ((TLRPC.PhotoSize)localObject).location;
      }
      paramArrayOfInt[0] = -1;
    }
    do
    {
      do
        return null;
      while (!(localObject instanceof TLRPC.Document));
      localObject = (TLRPC.Document)localObject;
    }
    while (((TLRPC.Document)localObject).thumb == null);
    paramArrayOfInt[0] = ((TLRPC.Document)localObject).thumb.size;
    if (paramArrayOfInt[0] == 0)
      paramArrayOfInt[0] = -1;
    return (TLRPC.FileLocation)((TLRPC.Document)localObject).thumb.location;
  }

  private String getFileName(int paramInt)
  {
    TLObject localTLObject = getMedia(paramInt);
    Object localObject = localTLObject;
    if ((localTLObject instanceof TLRPC.Photo))
      localObject = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo)localTLObject).sizes, AndroidUtilities.getPhotoSize());
    return (String)FileLoader.getAttachFileName((TLObject)localObject);
  }

  private ImageReceiver getImageReceiverFromListView(ViewGroup paramViewGroup, TLRPC.PageBlock paramPageBlock, int[] paramArrayOfInt)
  {
    int j = paramViewGroup.getChildCount();
    int i = 0;
    if (i < j)
    {
      Object localObject1 = paramViewGroup.getChildAt(i);
      Object localObject2;
      if ((localObject1 instanceof BlockPhotoCell))
      {
        localObject2 = (BlockPhotoCell)localObject1;
        if (((BlockPhotoCell)localObject2).currentBlock == paramPageBlock)
        {
          ((View)localObject1).getLocationInWindow(paramArrayOfInt);
          localObject1 = ((BlockPhotoCell)localObject2).imageView;
        }
      }
      else
      {
        do
        {
          return localObject1;
          if ((localObject1 instanceof BlockVideoCell))
          {
            localObject2 = (BlockVideoCell)localObject1;
            if (((BlockVideoCell)localObject2).currentBlock != paramPageBlock)
              break;
            ((View)localObject1).getLocationInWindow(paramArrayOfInt);
            return ((BlockVideoCell)localObject2).imageView;
          }
          if (!(localObject1 instanceof BlockCollageCell))
            break label142;
          localObject2 = getImageReceiverFromListView(((BlockCollageCell)localObject1).innerListView, paramPageBlock, paramArrayOfInt);
          localObject1 = localObject2;
        }
        while (localObject2 != null);
      }
      label142: 
      do
      {
        do
        {
          i += 1;
          break;
        }
        while (!(localObject1 instanceof BlockSlideshowCell));
        localObject1 = getImageReceiverFromListView(((BlockSlideshowCell)localObject1).innerListView, paramPageBlock, paramArrayOfInt);
      }
      while (localObject1 == null);
      return localObject1;
    }
    return (ImageReceiver)(ImageReceiver)null;
  }

  public static ArticleViewer getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        ArticleViewer localArticleViewer = Instance;
        localObject1 = localArticleViewer;
        if (localArticleViewer == null)
        {
          localObject1 = new ArticleViewer();
          Instance = (ArticleViewer)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (ArticleViewer)localObject2;
  }

  private TLObject getMedia(int paramInt)
  {
    if ((this.imagesArr.isEmpty()) || (paramInt >= this.imagesArr.size()) || (paramInt < 0))
      return null;
    TLRPC.PageBlock localPageBlock = (TLRPC.PageBlock)this.imagesArr.get(paramInt);
    if (localPageBlock.photo_id != 0L)
      return getPhotoWithId(localPageBlock.photo_id);
    if (localPageBlock.video_id != 0L)
      return getDocumentWithId(localPageBlock.video_id);
    return null;
  }

  private File getMediaFile(int paramInt)
  {
    if ((this.imagesArr.isEmpty()) || (paramInt >= this.imagesArr.size()) || (paramInt < 0))
      return null;
    Object localObject = (TLRPC.PageBlock)this.imagesArr.get(paramInt);
    if (((TLRPC.PageBlock)localObject).photo_id != 0L)
    {
      localObject = getPhotoWithId(((TLRPC.PageBlock)localObject).photo_id);
      if (localObject != null)
      {
        localObject = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo)localObject).sizes, AndroidUtilities.getPhotoSize());
        if (localObject != null)
          return FileLoader.getPathToAttach((TLObject)localObject, true);
      }
    }
    else if (((TLRPC.PageBlock)localObject).video_id != 0L)
    {
      localObject = getDocumentWithId(((TLRPC.PageBlock)localObject).video_id);
      if (localObject != null)
        return FileLoader.getPathToAttach((TLObject)localObject, true);
    }
    return (File)null;
  }

  private TLRPC.Photo getPhotoWithId(long paramLong)
  {
    Object localObject;
    if ((this.currentPage == null) || (this.currentPage.cached_page == null))
    {
      localObject = null;
      return localObject;
    }
    if ((this.currentPage.photo != null) && (this.currentPage.photo.id == paramLong))
      return this.currentPage.photo;
    int i = 0;
    while (true)
    {
      if (i >= this.currentPage.cached_page.photos.size())
        break label115;
      TLRPC.Photo localPhoto = (TLRPC.Photo)this.currentPage.cached_page.photos.get(i);
      localObject = localPhoto;
      if (localPhoto.id == paramLong)
        break;
      i += 1;
    }
    label115: return null;
  }

  private PlaceProviderObject getPlaceForPhoto(TLRPC.PageBlock paramPageBlock)
  {
    paramPageBlock = getImageReceiverFromListView(this.listView, paramPageBlock, this.coords);
    if (paramPageBlock == null)
      return null;
    PlaceProviderObject localPlaceProviderObject = new PlaceProviderObject();
    localPlaceProviderObject.viewX = this.coords[0];
    localPlaceProviderObject.viewY = this.coords[1];
    localPlaceProviderObject.parentView = this.listView;
    localPlaceProviderObject.imageReceiver = paramPageBlock;
    localPlaceProviderObject.thumb = paramPageBlock.getBitmap();
    localPlaceProviderObject.radius = paramPageBlock.getRoundRadius();
    localPlaceProviderObject.clipTopAddition = this.currentHeaderHeight;
    return localPlaceProviderObject;
  }

  private CharSequence getText(TLRPC.RichText paramRichText1, TLRPC.RichText paramRichText2, TLRPC.PageBlock paramPageBlock)
  {
    Object localObject1 = null;
    int i = 0;
    if ((paramRichText2 instanceof TLRPC.TL_textFixed))
      return getText(paramRichText1, ((TLRPC.TL_textFixed)paramRichText2).text, paramPageBlock);
    if ((paramRichText2 instanceof TLRPC.TL_textItalic))
      return getText(paramRichText1, ((TLRPC.TL_textItalic)paramRichText2).text, paramPageBlock);
    if ((paramRichText2 instanceof TLRPC.TL_textBold))
      return getText(paramRichText1, ((TLRPC.TL_textBold)paramRichText2).text, paramPageBlock);
    if ((paramRichText2 instanceof TLRPC.TL_textUnderline))
      return getText(paramRichText1, ((TLRPC.TL_textUnderline)paramRichText2).text, paramPageBlock);
    if ((paramRichText2 instanceof TLRPC.TL_textStrike))
      return getText(paramRichText1, ((TLRPC.TL_textStrike)paramRichText2).text, paramPageBlock);
    Object localObject2;
    if ((paramRichText2 instanceof TLRPC.TL_textEmail))
    {
      localObject1 = new SpannableStringBuilder(getText(paramRichText1, ((TLRPC.TL_textEmail)paramRichText2).text, paramPageBlock));
      localObject2 = (MetricAffectingSpan[])((SpannableStringBuilder)localObject1).getSpans(0, ((SpannableStringBuilder)localObject1).length(), MetricAffectingSpan.class);
      if ((localObject2 == null) || (localObject2.length == 0));
      for (paramRichText1 = getTextPaint(paramRichText1, paramRichText2, paramPageBlock); ; paramRichText1 = null)
      {
        ((SpannableStringBuilder)localObject1).setSpan(new TextPaintUrlSpan(paramRichText1, getUrl(paramRichText2)), 0, ((SpannableStringBuilder)localObject1).length(), 33);
        return localObject1;
      }
    }
    Object localObject3;
    if ((paramRichText2 instanceof TLRPC.TL_textUrl))
    {
      localObject2 = new SpannableStringBuilder(getText(paramRichText1, ((TLRPC.TL_textUrl)paramRichText2).text, paramPageBlock));
      localObject3 = (MetricAffectingSpan[])((SpannableStringBuilder)localObject2).getSpans(0, ((SpannableStringBuilder)localObject2).length(), MetricAffectingSpan.class);
      if ((localObject3 == null) || (localObject3.length == 0))
        localObject1 = getTextPaint(paramRichText1, paramRichText2, paramPageBlock);
      ((SpannableStringBuilder)localObject2).setSpan(new TextPaintUrlSpan((TextPaint)localObject1, getUrl(paramRichText2)), 0, ((SpannableStringBuilder)localObject2).length(), 33);
      return localObject2;
    }
    if ((paramRichText2 instanceof TLRPC.TL_textPlain))
      return ((TLRPC.TL_textPlain)paramRichText2).text;
    if ((paramRichText2 instanceof TLRPC.TL_textEmpty))
      return "";
    if ((paramRichText2 instanceof TLRPC.TL_textConcat))
    {
      localObject3 = new SpannableStringBuilder();
      int j = paramRichText2.texts.size();
      if (i < j)
      {
        TLRPC.RichText localRichText = (TLRPC.RichText)paramRichText2.texts.get(i);
        localObject1 = getText(paramRichText1, localRichText, paramPageBlock);
        int k = getTextFlags(localRichText);
        int m = ((SpannableStringBuilder)localObject3).length();
        ((SpannableStringBuilder)localObject3).append((CharSequence)localObject1);
        if ((k != 0) && (!(localObject1 instanceof SpannableStringBuilder)))
        {
          if ((k & 0x8) == 0)
            break label500;
          localObject2 = getUrl(localRichText);
          localObject1 = localObject2;
          if (localObject2 == null)
            localObject1 = getUrl(paramRichText1);
          ((SpannableStringBuilder)localObject3).setSpan(new TextPaintUrlSpan(getTextPaint(paramRichText1, localRichText, paramPageBlock), (String)localObject1), m, ((SpannableStringBuilder)localObject3).length(), 33);
        }
        while (true)
        {
          i += 1;
          break;
          label500: ((SpannableStringBuilder)localObject3).setSpan(new TextPaintSpan(getTextPaint(paramRichText1, localRichText, paramPageBlock)), m, ((SpannableStringBuilder)localObject3).length(), 33);
        }
      }
      return localObject3;
    }
    return (CharSequence)(CharSequence)(CharSequence)("not supported " + paramRichText2);
  }

  private int getTextFlags(TLRPC.RichText paramRichText)
  {
    if ((paramRichText instanceof TLRPC.TL_textFixed))
      return getTextFlags(paramRichText.parentRichText) | 0x4;
    if ((paramRichText instanceof TLRPC.TL_textItalic))
      return getTextFlags(paramRichText.parentRichText) | 0x2;
    if ((paramRichText instanceof TLRPC.TL_textBold))
      return getTextFlags(paramRichText.parentRichText) | 0x1;
    if ((paramRichText instanceof TLRPC.TL_textUnderline))
      return getTextFlags(paramRichText.parentRichText) | 0x10;
    if ((paramRichText instanceof TLRPC.TL_textStrike))
      return getTextFlags(paramRichText.parentRichText) | 0x20;
    if ((paramRichText instanceof TLRPC.TL_textEmail))
      return getTextFlags(paramRichText.parentRichText) | 0x8;
    if ((paramRichText instanceof TLRPC.TL_textUrl))
      return getTextFlags(paramRichText.parentRichText) | 0x8;
    if (paramRichText != null)
      return getTextFlags(paramRichText.parentRichText);
    return 0;
  }

  private TextPaint getTextPaint(TLRPC.RichText paramRichText1, TLRPC.RichText paramRichText2, TLRPC.PageBlock paramPageBlock)
  {
    int j = -8156010;
    int k = getTextFlags(paramRichText2);
    TextPaint localTextPaint = null;
    int i = AndroidUtilities.dp(14.0F);
    if ((paramPageBlock instanceof TLRPC.TL_pageBlockPhoto))
    {
      paramRichText1 = captionTextPaints;
      i = AndroidUtilities.dp(14.0F);
    }
    while (true)
    {
      if (paramRichText1 == null)
      {
        if (errorTextPaint == null)
        {
          errorTextPaint = new TextPaint(1);
          errorTextPaint.setColor(-65536);
        }
        errorTextPaint.setTextSize(AndroidUtilities.dp(14.0F));
        return errorTextPaint;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockTitle))
        {
          paramRichText1 = titleTextPaints;
          i = AndroidUtilities.dp(24.0F);
          j = -16777216;
          continue;
        }
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockAuthorDate))
        {
          paramRichText1 = authorTextPaints;
          i = AndroidUtilities.dp(14.0F);
          continue;
        }
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockFooter))
        {
          paramRichText1 = footerTextPaints;
          i = AndroidUtilities.dp(14.0F);
          continue;
        }
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockSubtitle))
        {
          paramRichText1 = subtitleTextPaints;
          i = AndroidUtilities.dp(21.0F);
          j = -16777216;
          continue;
        }
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockHeader))
        {
          paramRichText1 = headerTextPaints;
          i = AndroidUtilities.dp(21.0F);
          j = -16777216;
          continue;
        }
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockSubheader))
        {
          paramRichText1 = subheaderTextPaints;
          i = AndroidUtilities.dp(18.0F);
          j = -16777216;
          continue;
        }
        if (((paramPageBlock instanceof TLRPC.TL_pageBlockBlockquote)) || ((paramPageBlock instanceof TLRPC.TL_pageBlockPullquote)))
        {
          if (paramPageBlock.text == paramRichText1)
          {
            paramRichText1 = quoteTextPaints;
            i = AndroidUtilities.dp(15.0F);
            j = -16777216;
            continue;
          }
          if (paramPageBlock.caption == paramRichText1)
          {
            paramRichText1 = subquoteTextPaints;
            i = AndroidUtilities.dp(14.0F);
            continue;
          }
        }
        else
        {
          if ((paramPageBlock instanceof TLRPC.TL_pageBlockPreformatted))
          {
            paramRichText1 = preformattedTextPaints;
            i = AndroidUtilities.dp(14.0F);
            j = -16777216;
            continue;
          }
          if ((paramPageBlock instanceof TLRPC.TL_pageBlockParagraph))
          {
            if (paramPageBlock.caption == paramRichText1)
            {
              paramRichText1 = embedPostCaptionTextPaints;
              i = AndroidUtilities.dp(14.0F);
              continue;
            }
            paramRichText1 = paragraphTextPaints;
            i = AndroidUtilities.dp(16.0F);
            j = -16777216;
            continue;
          }
          if ((paramPageBlock instanceof TLRPC.TL_pageBlockList))
          {
            paramRichText1 = listTextPaints;
            i = AndroidUtilities.dp(15.0F);
            j = -16777216;
            continue;
          }
          if ((paramPageBlock instanceof TLRPC.TL_pageBlockEmbed))
          {
            paramRichText1 = embedTextPaints;
            i = AndroidUtilities.dp(14.0F);
            continue;
          }
          if ((paramPageBlock instanceof TLRPC.TL_pageBlockSlideshow))
          {
            paramRichText1 = slideshowTextPaints;
            i = AndroidUtilities.dp(14.0F);
            continue;
          }
          if ((paramPageBlock instanceof TLRPC.TL_pageBlockEmbedPost))
          {
            if (paramRichText2 != null)
            {
              paramRichText1 = embedPostTextPaints;
              i = AndroidUtilities.dp(14.0F);
              j = -16777216;
              continue;
            }
          }
          else if ((paramPageBlock instanceof TLRPC.TL_pageBlockVideo))
          {
            paramRichText1 = videoTextPaints;
            i = AndroidUtilities.dp(14.0F);
            j = -16777216;
            continue;
          }
        }
      }
      else
      {
        localTextPaint = (TextPaint)paramRichText1.get(Integer.valueOf(k));
        paramRichText2 = localTextPaint;
        if (localTextPaint == null)
        {
          paramRichText2 = new TextPaint(1);
          if ((k & 0x4) == 0)
            break label638;
          paramRichText2.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
          if ((k & 0x20) != 0)
            paramRichText2.setFlags(paramRichText2.getFlags() | 0x10);
          if ((k & 0x10) != 0)
            paramRichText2.setFlags(paramRichText2.getFlags() | 0x8);
          if ((k & 0x8) == 0)
            break label824;
          j = -11697229;
        }
        label824: 
        while (true)
        {
          paramRichText2.setColor(j);
          paramRichText1.put(Integer.valueOf(k), paramRichText2);
          paramRichText2.setTextSize(i);
          return paramRichText2;
          label638: if (((paramPageBlock instanceof TLRPC.TL_pageBlockTitle)) || ((paramPageBlock instanceof TLRPC.TL_pageBlockHeader)) || ((paramPageBlock instanceof TLRPC.TL_pageBlockSubtitle)) || ((paramPageBlock instanceof TLRPC.TL_pageBlockSubheader)))
          {
            if (((k & 0x1) != 0) && ((k & 0x2) != 0))
            {
              paramRichText2.setTypeface(Typeface.create("serif", 3));
              break;
            }
            if ((k & 0x1) != 0)
            {
              paramRichText2.setTypeface(Typeface.create("serif", 1));
              break;
            }
            if ((k & 0x2) != 0)
            {
              paramRichText2.setTypeface(Typeface.create("serif", 2));
              break;
            }
            paramRichText2.setTypeface(Typeface.create("serif", 0));
            break;
          }
          if (((k & 0x1) != 0) && ((k & 0x2) != 0))
          {
            paramRichText2.setTypeface(AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf"));
            break;
          }
          if ((k & 0x1) != 0)
          {
            paramRichText2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            break;
          }
          if ((k & 0x2) == 0)
            break;
          paramRichText2.setTypeface(AndroidUtilities.getTypeface("fonts/ritalic.ttf"));
          break;
        }
      }
      j = -65536;
      paramRichText1 = localTextPaint;
    }
  }

  private String getUrl(TLRPC.RichText paramRichText)
  {
    if ((paramRichText instanceof TLRPC.TL_textFixed))
      return getUrl(((TLRPC.TL_textFixed)paramRichText).text);
    if ((paramRichText instanceof TLRPC.TL_textItalic))
      return getUrl(((TLRPC.TL_textItalic)paramRichText).text);
    if ((paramRichText instanceof TLRPC.TL_textBold))
      return getUrl(((TLRPC.TL_textBold)paramRichText).text);
    if ((paramRichText instanceof TLRPC.TL_textUnderline))
      return getUrl(((TLRPC.TL_textUnderline)paramRichText).text);
    if ((paramRichText instanceof TLRPC.TL_textStrike))
      return getUrl(((TLRPC.TL_textStrike)paramRichText).text);
    if ((paramRichText instanceof TLRPC.TL_textEmail))
      return ((TLRPC.TL_textEmail)paramRichText).email;
    if ((paramRichText instanceof TLRPC.TL_textUrl))
      return ((TLRPC.TL_textUrl)paramRichText).url;
    return null;
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

  private void hideActionBar()
  {
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.backButton, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.shareContainer, "alpha", new float[] { 0.0F }) });
    localAnimatorSet.setDuration(250L);
    localAnimatorSet.setInterpolator(new DecelerateInterpolator());
    localAnimatorSet.start();
  }

  private boolean isMediaVideo(int paramInt)
  {
    return (!this.imagesArr.isEmpty()) && (paramInt < this.imagesArr.size()) && (paramInt >= 0) && (isVideoBlock((TLRPC.PageBlock)this.imagesArr.get(paramInt)));
  }

  private boolean isVideoBlock(TLRPC.PageBlock paramPageBlock)
  {
    if ((paramPageBlock != null) && (paramPageBlock.video_id != 0L))
    {
      paramPageBlock = getDocumentWithId(paramPageBlock.video_id);
      if (paramPageBlock != null)
        return MessageObject.isVideoDocument(paramPageBlock);
    }
    return false;
  }

  private void onActionClick(boolean paramBoolean)
  {
    Object localObject2 = null;
    Object localObject1 = getMedia(this.currentIndex);
    if ((!(localObject1 instanceof TLRPC.Document)) || (this.currentFileNames[0] == null))
      return;
    TLRPC.Document localDocument = (TLRPC.Document)localObject1;
    localObject1 = localObject2;
    if (this.currentMedia != null)
    {
      localObject1 = getMediaFile(this.currentIndex);
      if ((localObject1 == null) || (((File)localObject1).exists()))
        break label115;
      localObject1 = localObject2;
    }
    label115: 
    while (true)
    {
      if (localObject1 == null)
      {
        if (!paramBoolean)
          break;
        if (!FileLoader.getInstance().isLoadingFile(this.currentFileNames[0]))
        {
          FileLoader.getInstance().loadFile(localDocument, true, true);
          return;
        }
        FileLoader.getInstance().cancelLoadFile(localDocument);
        return;
      }
      preparePlayer((File)localObject1, true);
      return;
    }
  }

  private void onClosed()
  {
    this.isVisible = false;
    this.currentPage = null;
    this.blocks.clear();
    this.photoBlocks.clear();
    this.adapter.notifyDataSetChanged();
    try
    {
      this.parentActivity.getWindow().clearFlags(128);
      int i = 0;
      while (i < this.createdWebViews.size())
      {
        ((BlockEmbedCell)this.createdWebViews.get(i)).destroyWebView(false);
        i += 1;
      }
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
      this.containerView.post(new Runnable()
      {
        public void run()
        {
          try
          {
            if (ArticleViewer.this.windowView.getParent() != null)
              ((WindowManager)ArticleViewer.this.parentActivity.getSystemService("window")).removeView(ArticleViewer.this.windowView);
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      });
    }
  }

  private void onPhotoClosed(PlaceProviderObject paramPlaceProviderObject)
  {
    this.isPhotoVisible = false;
    this.disableShowCheck = true;
    this.currentMedia = null;
    this.currentThumb = null;
    if (this.currentAnimation != null)
    {
      this.currentAnimation.setSecondParentView(null);
      this.currentAnimation = null;
    }
    int i = 0;
    while (i < 3)
    {
      if (this.radialProgressViews[i] != null)
        this.radialProgressViews[i].setBackgroundState(-1, false);
      i += 1;
    }
    this.centerImage.setImageBitmap((Bitmap)null);
    this.leftImage.setImageBitmap((Bitmap)null);
    this.rightImage.setImageBitmap((Bitmap)null);
    this.photoContainerView.post(new Runnable()
    {
      public void run()
      {
        ArticleViewer.this.animatingImageView.setImageBitmap(null);
      }
    });
    this.disableShowCheck = false;
    if (paramPlaceProviderObject != null)
      paramPlaceProviderObject.imageReceiver.setVisible(true, true);
  }

  private void onPhotoShow(int paramInt, PlaceProviderObject paramPlaceProviderObject)
  {
    this.currentIndex = -1;
    this.currentFileNames[0] = null;
    this.currentFileNames[1] = null;
    this.currentFileNames[2] = null;
    if (paramPlaceProviderObject != null);
    for (paramPlaceProviderObject = paramPlaceProviderObject.thumb; ; paramPlaceProviderObject = null)
    {
      this.currentThumb = paramPlaceProviderObject;
      this.menuItem.setVisibility(0);
      this.menuItem.hideSubItem(3);
      this.actionBar.setTranslationY(0.0F);
      this.captionTextView.setTag(null);
      this.captionTextView.setVisibility(4);
      int i = 0;
      while (i < 3)
      {
        if (this.radialProgressViews[i] != null)
          this.radialProgressViews[i].setBackgroundState(-1, false);
        i += 1;
      }
    }
    setImageIndex(paramInt, true);
    if ((this.currentMedia != null) && (isMediaVideo(this.currentIndex)))
      onActionClick(false);
  }

  private void onSharePressed()
  {
    if ((this.parentActivity == null) || (this.currentMedia == null))
      return;
    while (true)
    {
      Intent localIntent;
      try
      {
        File localFile = getMediaFile(this.currentIndex);
        if ((localFile == null) || (!localFile.exists()))
          break;
        localIntent = new Intent("android.intent.action.SEND");
        if (isMediaVideo(this.currentIndex))
        {
          localIntent.setType("video/mp4");
          localIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(localFile));
          this.parentActivity.startActivityForResult(Intent.createChooser(localIntent, LocaleController.getString("ShareFile", 2131166451)), 500);
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        return;
      }
      localIntent.setType("image/jpeg");
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.parentActivity);
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
    localBuilder.setMessage(LocaleController.getString("PleaseDownload", 2131166288));
    showDialog(localBuilder.create());
  }

  // ERROR //
  private boolean open(MessageObject paramMessageObject, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 714	org/vidogram/ui/ArticleViewer:parentActivity	Landroid/app/Activity;
    //   4: ifnull +21 -> 25
    //   7: aload_0
    //   8: getfield 717	org/vidogram/ui/ArticleViewer:isVisible	Z
    //   11: ifeq +10 -> 21
    //   14: aload_0
    //   15: getfield 710	org/vidogram/ui/ArticleViewer:collapsed	Z
    //   18: ifeq +7 -> 25
    //   21: aload_1
    //   22: ifnonnull +5 -> 27
    //   25: iconst_0
    //   26: ireturn
    //   27: iload_2
    //   28: ifeq +73 -> 101
    //   31: new 1268	org/vidogram/tgnet/TLRPC$TL_messages_getWebPage
    //   34: dup
    //   35: invokespecial 1269	org/vidogram/tgnet/TLRPC$TL_messages_getWebPage:<init>	()V
    //   38: astore 6
    //   40: aload 6
    //   42: aload_1
    //   43: getfield 2068	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   46: getfield 2074	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   49: getfield 2079	org/vidogram/tgnet/TLRPC$MessageMedia:webpage	Lorg/vidogram/tgnet/TLRPC$WebPage;
    //   52: getfield 1262	org/vidogram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   55: putfield 1270	org/vidogram/tgnet/TLRPC$TL_messages_getWebPage:url	Ljava/lang/String;
    //   58: aload_1
    //   59: getfield 2068	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   62: getfield 2074	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   65: getfield 2079	org/vidogram/tgnet/TLRPC$MessageMedia:webpage	Lorg/vidogram/tgnet/TLRPC$WebPage;
    //   68: getfield 1578	org/vidogram/tgnet/TLRPC$WebPage:cached_page	Lorg/vidogram/tgnet/TLRPC$Page;
    //   71: instanceof 2081
    //   74: ifeq +637 -> 711
    //   77: aload 6
    //   79: iconst_0
    //   80: putfield 1273	org/vidogram/tgnet/TLRPC$TL_messages_getWebPage:hash	I
    //   83: invokestatic 1279	org/vidogram/tgnet/ConnectionsManager:getInstance	()Lorg/vidogram/tgnet/ConnectionsManager;
    //   86: aload 6
    //   88: new 26	org/vidogram/ui/ArticleViewer$15
    //   91: dup
    //   92: aload_0
    //   93: aload_1
    //   94: invokespecial 2084	org/vidogram/ui/ArticleViewer$15:<init>	(Lorg/vidogram/ui/ArticleViewer;Lorg/vidogram/messenger/MessageObject;)V
    //   97: invokevirtual 1286	org/vidogram/tgnet/ConnectionsManager:sendRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/tgnet/RequestDelegate;)I
    //   100: pop
    //   101: aload_0
    //   102: getfield 519	org/vidogram/ui/ArticleViewer:pagesStack	Ljava/util/ArrayList;
    //   105: invokevirtual 1916	java/util/ArrayList:clear	()V
    //   108: aload_0
    //   109: iconst_0
    //   110: putfield 710	org/vidogram/ui/ArticleViewer:collapsed	Z
    //   113: aload_0
    //   114: getfield 2086	org/vidogram/ui/ArticleViewer:backDrawable	Lorg/vidogram/ui/ActionBar/BackDrawable;
    //   117: fconst_0
    //   118: iconst_0
    //   119: invokevirtual 2091	org/vidogram/ui/ActionBar/BackDrawable:setRotation	(FZ)V
    //   122: aload_0
    //   123: getfield 979	org/vidogram/ui/ArticleViewer:containerView	Landroid/widget/FrameLayout;
    //   126: fconst_0
    //   127: invokevirtual 2094	android/widget/FrameLayout:setTranslationX	(F)V
    //   130: aload_0
    //   131: getfield 979	org/vidogram/ui/ArticleViewer:containerView	Landroid/widget/FrameLayout;
    //   134: fconst_0
    //   135: invokevirtual 1383	android/widget/FrameLayout:setTranslationY	(F)V
    //   138: aload_0
    //   139: getfield 808	org/vidogram/ui/ArticleViewer:listView	Lorg/vidogram/ui/Components/RecyclerListView;
    //   142: fconst_0
    //   143: invokevirtual 2095	org/vidogram/ui/Components/RecyclerListView:setTranslationY	(F)V
    //   146: aload_0
    //   147: getfield 808	org/vidogram/ui/ArticleViewer:listView	Lorg/vidogram/ui/Components/RecyclerListView;
    //   150: fconst_1
    //   151: invokevirtual 2096	org/vidogram/ui/Components/RecyclerListView:setAlpha	(F)V
    //   154: aload_0
    //   155: getfield 858	org/vidogram/ui/ArticleViewer:windowView	Lorg/vidogram/ui/ArticleViewer$WindowView;
    //   158: fconst_0
    //   159: invokevirtual 2099	org/vidogram/ui/ArticleViewer$WindowView:setInnerTranslationX	(F)V
    //   162: aload_0
    //   163: getfield 652	org/vidogram/ui/ArticleViewer:actionBar	Lorg/vidogram/ui/ActionBar/ActionBar;
    //   166: bipush 8
    //   168: invokevirtual 2100	org/vidogram/ui/ActionBar/ActionBar:setVisibility	(I)V
    //   171: aload_0
    //   172: getfield 788	org/vidogram/ui/ArticleViewer:bottomLayout	Landroid/widget/FrameLayout;
    //   175: bipush 8
    //   177: invokevirtual 2101	android/widget/FrameLayout:setVisibility	(I)V
    //   180: aload_0
    //   181: getfield 659	org/vidogram/ui/ArticleViewer:captionTextViewNew	Landroid/widget/TextView;
    //   184: bipush 8
    //   186: invokevirtual 1984	android/widget/TextView:setVisibility	(I)V
    //   189: aload_0
    //   190: getfield 656	org/vidogram/ui/ArticleViewer:captionTextViewOld	Landroid/widget/TextView;
    //   193: bipush 8
    //   195: invokevirtual 1984	android/widget/TextView:setVisibility	(I)V
    //   198: aload_0
    //   199: getfield 1378	org/vidogram/ui/ArticleViewer:shareContainer	Landroid/widget/FrameLayout;
    //   202: fconst_0
    //   203: invokevirtual 2102	android/widget/FrameLayout:setAlpha	(F)V
    //   206: aload_0
    //   207: getfield 1365	org/vidogram/ui/ArticleViewer:backButton	Landroid/widget/ImageView;
    //   210: fconst_0
    //   211: invokevirtual 2103	android/widget/ImageView:setAlpha	(F)V
    //   214: aload_0
    //   215: getfield 1078	org/vidogram/ui/ArticleViewer:layoutManager	Lorg/vidogram/messenger/support/widget/LinearLayoutManager;
    //   218: iconst_0
    //   219: iconst_0
    //   220: invokevirtual 1087	org/vidogram/messenger/support/widget/LinearLayoutManager:scrollToPositionWithOffset	(II)V
    //   223: aload_0
    //   224: ldc_w 1345
    //   227: invokestatic 1351	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   230: ineg
    //   231: invokespecial 813	org/vidogram/ui/ArticleViewer:checkScroll	(I)V
    //   234: aload_1
    //   235: getfield 2068	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   238: getfield 2074	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   241: getfield 2079	org/vidogram/tgnet/TLRPC$MessageMedia:webpage	Lorg/vidogram/tgnet/TLRPC$WebPage;
    //   244: astore 7
    //   246: aload 7
    //   248: getfield 1262	org/vidogram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   251: invokevirtual 1256	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   254: astore 8
    //   256: iconst_0
    //   257: istore_3
    //   258: iload_3
    //   259: aload_1
    //   260: getfield 2068	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   263: getfield 2106	org/vidogram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   266: invokevirtual 1057	java/util/ArrayList:size	()I
    //   269: if_icmpge +534 -> 803
    //   272: aload_1
    //   273: getfield 2068	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   276: getfield 2106	org/vidogram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   279: iload_3
    //   280: invokevirtual 1061	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   283: checkcast 2108	org/vidogram/tgnet/TLRPC$MessageEntity
    //   286: astore 6
    //   288: aload 6
    //   290: instanceof 2110
    //   293: ifeq +446 -> 739
    //   296: aload_1
    //   297: getfield 2068	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   300: getfield 2113	org/vidogram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   303: astore 9
    //   305: aload 6
    //   307: getfield 2116	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   310: istore 4
    //   312: aload 6
    //   314: getfield 2116	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   317: istore 5
    //   319: aload 9
    //   321: iload 4
    //   323: aload 6
    //   325: getfield 2118	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   328: iload 5
    //   330: iadd
    //   331: invokevirtual 2121	java/lang/String:substring	(II)Ljava/lang/String;
    //   334: invokevirtual 1256	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   337: astore 6
    //   339: aload 6
    //   341: aload 8
    //   343: invokevirtual 1266	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   346: ifne +13 -> 359
    //   349: aload 8
    //   351: aload 6
    //   353: invokevirtual 1266	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   356: ifeq +383 -> 739
    //   359: aload 6
    //   361: bipush 35
    //   363: invokevirtual 1249	java/lang/String:lastIndexOf	(I)I
    //   366: istore 4
    //   368: iload 4
    //   370: iconst_m1
    //   371: if_icmpeq +432 -> 803
    //   374: aload 6
    //   376: iload 4
    //   378: iconst_1
    //   379: iadd
    //   380: invokevirtual 1253	java/lang/String:substring	(I)Ljava/lang/String;
    //   383: astore 6
    //   385: aload 6
    //   387: astore_1
    //   388: aload_0
    //   389: aload 7
    //   391: aload_1
    //   392: invokespecial 782	org/vidogram/ui/ArticleViewer:addPageToStack	(Lorg/vidogram/tgnet/TLRPC$WebPage;Ljava/lang/String;)V
    //   395: aload_0
    //   396: aconst_null
    //   397: putfield 896	org/vidogram/ui/ArticleViewer:lastInsets	Ljava/lang/Object;
    //   400: aload_0
    //   401: getfield 717	org/vidogram/ui/ArticleViewer:isVisible	Z
    //   404: ifne +349 -> 753
    //   407: aload_0
    //   408: getfield 714	org/vidogram/ui/ArticleViewer:parentActivity	Landroid/app/Activity;
    //   411: ldc_w 2123
    //   414: invokevirtual 2127	android/app/Activity:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   417: checkcast 2129	android/view/WindowManager
    //   420: astore_1
    //   421: aload_0
    //   422: getfield 707	org/vidogram/ui/ArticleViewer:attachedToWindow	Z
    //   425: ifeq +13 -> 438
    //   428: aload_1
    //   429: aload_0
    //   430: getfield 858	org/vidogram/ui/ArticleViewer:windowView	Lorg/vidogram/ui/ArticleViewer$WindowView;
    //   433: invokeinterface 2132 2 0
    //   438: getstatic 2137	android/os/Build$VERSION:SDK_INT	I
    //   441: bipush 21
    //   443: if_icmplt +13 -> 456
    //   446: aload_0
    //   447: getfield 909	org/vidogram/ui/ArticleViewer:windowLayoutParams	Landroid/view/WindowManager$LayoutParams;
    //   450: ldc_w 2138
    //   453: putfield 2143	android/view/WindowManager$LayoutParams:flags	I
    //   456: aload_0
    //   457: getfield 909	org/vidogram/ui/ArticleViewer:windowLayoutParams	Landroid/view/WindowManager$LayoutParams;
    //   460: astore 6
    //   462: aload 6
    //   464: aload 6
    //   466: getfield 2143	android/view/WindowManager$LayoutParams:flags	I
    //   469: sipush 1032
    //   472: ior
    //   473: putfield 2143	android/view/WindowManager$LayoutParams:flags	I
    //   476: aload_0
    //   477: getfield 858	org/vidogram/ui/ArticleViewer:windowView	Lorg/vidogram/ui/ArticleViewer$WindowView;
    //   480: iconst_0
    //   481: invokevirtual 2146	org/vidogram/ui/ArticleViewer$WindowView:setFocusable	(Z)V
    //   484: aload_0
    //   485: getfield 979	org/vidogram/ui/ArticleViewer:containerView	Landroid/widget/FrameLayout;
    //   488: iconst_0
    //   489: invokevirtual 2147	android/widget/FrameLayout:setFocusable	(Z)V
    //   492: aload_1
    //   493: aload_0
    //   494: getfield 858	org/vidogram/ui/ArticleViewer:windowView	Lorg/vidogram/ui/ArticleViewer$WindowView;
    //   497: aload_0
    //   498: getfield 909	org/vidogram/ui/ArticleViewer:windowLayoutParams	Landroid/view/WindowManager$LayoutParams;
    //   501: invokeinterface 2151 3 0
    //   506: aload_0
    //   507: iconst_1
    //   508: putfield 717	org/vidogram/ui/ArticleViewer:isVisible	Z
    //   511: aload_0
    //   512: iconst_1
    //   513: putfield 884	org/vidogram/ui/ArticleViewer:animationInProgress	I
    //   516: aload_0
    //   517: getfield 858	org/vidogram/ui/ArticleViewer:windowView	Lorg/vidogram/ui/ArticleViewer$WindowView;
    //   520: fconst_0
    //   521: invokevirtual 2152	org/vidogram/ui/ArticleViewer$WindowView:setAlpha	(F)V
    //   524: aload_0
    //   525: getfield 979	org/vidogram/ui/ArticleViewer:containerView	Landroid/widget/FrameLayout;
    //   528: fconst_0
    //   529: invokevirtual 2102	android/widget/FrameLayout:setAlpha	(F)V
    //   532: new 1114	android/animation/AnimatorSet
    //   535: dup
    //   536: invokespecial 1115	android/animation/AnimatorSet:<init>	()V
    //   539: astore_1
    //   540: aload_1
    //   541: iconst_3
    //   542: anewarray 1117	android/animation/Animator
    //   545: dup
    //   546: iconst_0
    //   547: aload_0
    //   548: getfield 858	org/vidogram/ui/ArticleViewer:windowView	Lorg/vidogram/ui/ArticleViewer$WindowView;
    //   551: ldc_w 1889
    //   554: iconst_2
    //   555: newarray float
    //   557: dup
    //   558: iconst_0
    //   559: fconst_0
    //   560: fastore
    //   561: dup
    //   562: iconst_1
    //   563: fconst_1
    //   564: fastore
    //   565: invokestatic 1124	android/animation/ObjectAnimator:ofFloat	(Ljava/lang/Object;Ljava/lang/String;[F)Landroid/animation/ObjectAnimator;
    //   568: aastore
    //   569: dup
    //   570: iconst_1
    //   571: aload_0
    //   572: getfield 979	org/vidogram/ui/ArticleViewer:containerView	Landroid/widget/FrameLayout;
    //   575: ldc_w 1889
    //   578: iconst_2
    //   579: newarray float
    //   581: dup
    //   582: iconst_0
    //   583: fconst_0
    //   584: fastore
    //   585: dup
    //   586: iconst_1
    //   587: fconst_1
    //   588: fastore
    //   589: invokestatic 1124	android/animation/ObjectAnimator:ofFloat	(Ljava/lang/Object;Ljava/lang/String;[F)Landroid/animation/ObjectAnimator;
    //   592: aastore
    //   593: dup
    //   594: iconst_2
    //   595: aload_0
    //   596: getfield 858	org/vidogram/ui/ArticleViewer:windowView	Lorg/vidogram/ui/ArticleViewer$WindowView;
    //   599: ldc_w 2153
    //   602: iconst_2
    //   603: newarray float
    //   605: dup
    //   606: iconst_0
    //   607: ldc_w 1345
    //   610: invokestatic 1351	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   613: i2f
    //   614: fastore
    //   615: dup
    //   616: iconst_1
    //   617: fconst_0
    //   618: fastore
    //   619: invokestatic 1124	android/animation/ObjectAnimator:ofFloat	(Ljava/lang/Object;Ljava/lang/String;[F)Landroid/animation/ObjectAnimator;
    //   622: aastore
    //   623: invokevirtual 1128	android/animation/AnimatorSet:playTogether	([Landroid/animation/Animator;)V
    //   626: aload_0
    //   627: new 30	org/vidogram/ui/ArticleViewer$16
    //   630: dup
    //   631: aload_0
    //   632: invokespecial 2154	org/vidogram/ui/ArticleViewer$16:<init>	(Lorg/vidogram/ui/ArticleViewer;)V
    //   635: putfield 887	org/vidogram/ui/ArticleViewer:animationEndRunnable	Ljava/lang/Runnable;
    //   638: aload_1
    //   639: ldc2_w 2155
    //   642: invokevirtual 1136	android/animation/AnimatorSet:setDuration	(J)Landroid/animation/AnimatorSet;
    //   645: pop
    //   646: aload_1
    //   647: aload_0
    //   648: getfield 598	org/vidogram/ui/ArticleViewer:interpolator	Landroid/view/animation/DecelerateInterpolator;
    //   651: invokevirtual 1132	android/animation/AnimatorSet:setInterpolator	(Landroid/animation/TimeInterpolator;)V
    //   654: aload_1
    //   655: new 32	org/vidogram/ui/ArticleViewer$17
    //   658: dup
    //   659: aload_0
    //   660: invokespecial 2157	org/vidogram/ui/ArticleViewer$17:<init>	(Lorg/vidogram/ui/ArticleViewer;)V
    //   663: invokevirtual 1141	android/animation/AnimatorSet:addListener	(Landroid/animation/Animator$AnimatorListener;)V
    //   666: aload_0
    //   667: invokestatic 1110	java/lang/System:currentTimeMillis	()J
    //   670: putfield 1147	org/vidogram/ui/ArticleViewer:transitionAnimationStartTime	J
    //   673: new 36	org/vidogram/ui/ArticleViewer$18
    //   676: dup
    //   677: aload_0
    //   678: aload_1
    //   679: invokespecial 2160	org/vidogram/ui/ArticleViewer$18:<init>	(Lorg/vidogram/ui/ArticleViewer;Landroid/animation/AnimatorSet;)V
    //   682: invokestatic 2164	org/vidogram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
    //   685: getstatic 2137	android/os/Build$VERSION:SDK_INT	I
    //   688: bipush 18
    //   690: if_icmplt +12 -> 702
    //   693: aload_0
    //   694: getfield 979	org/vidogram/ui/ArticleViewer:containerView	Landroid/widget/FrameLayout;
    //   697: iconst_2
    //   698: aconst_null
    //   699: invokevirtual 2168	android/widget/FrameLayout:setLayerType	(ILandroid/graphics/Paint;)V
    //   702: aload_0
    //   703: sipush 200
    //   706: invokespecial 913	org/vidogram/ui/ArticleViewer:showActionBar	(I)V
    //   709: iconst_1
    //   710: ireturn
    //   711: aload 6
    //   713: aload_1
    //   714: getfield 2068	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   717: getfield 2074	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   720: getfield 2079	org/vidogram/tgnet/TLRPC$MessageMedia:webpage	Lorg/vidogram/tgnet/TLRPC$WebPage;
    //   723: getfield 2169	org/vidogram/tgnet/TLRPC$WebPage:hash	I
    //   726: putfield 1273	org/vidogram/tgnet/TLRPC$TL_messages_getWebPage:hash	I
    //   729: goto -646 -> 83
    //   732: astore 6
    //   734: aload 6
    //   736: invokestatic 1242	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   739: iload_3
    //   740: iconst_1
    //   741: iadd
    //   742: istore_3
    //   743: goto -485 -> 258
    //   746: astore_1
    //   747: aload_1
    //   748: invokestatic 1242	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   751: iconst_0
    //   752: ireturn
    //   753: aload_0
    //   754: getfield 909	org/vidogram/ui/ArticleViewer:windowLayoutParams	Landroid/view/WindowManager$LayoutParams;
    //   757: astore_1
    //   758: aload_1
    //   759: aload_1
    //   760: getfield 2143	android/view/WindowManager$LayoutParams:flags	I
    //   763: bipush 239
    //   765: iand
    //   766: putfield 2143	android/view/WindowManager$LayoutParams:flags	I
    //   769: aload_0
    //   770: getfield 714	org/vidogram/ui/ArticleViewer:parentActivity	Landroid/app/Activity;
    //   773: ldc_w 2123
    //   776: invokevirtual 2127	android/app/Activity:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   779: checkcast 2129	android/view/WindowManager
    //   782: aload_0
    //   783: getfield 858	org/vidogram/ui/ArticleViewer:windowView	Lorg/vidogram/ui/ArticleViewer$WindowView;
    //   786: aload_0
    //   787: getfield 909	org/vidogram/ui/ArticleViewer:windowLayoutParams	Landroid/view/WindowManager$LayoutParams;
    //   790: invokeinterface 2172 3 0
    //   795: goto -289 -> 506
    //   798: astore 6
    //   800: goto -362 -> 438
    //   803: aconst_null
    //   804: astore_1
    //   805: goto -417 -> 388
    //
    // Exception table:
    //   from	to	target	type
    //   296	359	732	java/lang/Exception
    //   359	368	732	java/lang/Exception
    //   374	385	732	java/lang/Exception
    //   438	456	746	java/lang/Exception
    //   456	506	746	java/lang/Exception
    //   428	438	798	java/lang/Exception
  }

  private void openPreviewsChat(TLRPC.User paramUser, long paramLong)
  {
    if ((paramUser == null) || (this.parentActivity == null))
      return;
    Bundle localBundle = new Bundle();
    localBundle.putInt("user_id", paramUser.id);
    localBundle.putString("botUser", "webpage" + paramLong);
    ((LaunchActivity)this.parentActivity).presentFragment(new ChatActivity(localBundle), false, true);
    close(false, true);
  }

  @SuppressLint({"NewApi"})
  private void preparePlayer(File paramFile, boolean paramBoolean)
  {
    if (this.parentActivity == null)
      return;
    releasePlayer();
    if (this.videoTextureView == null)
    {
      this.aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity);
      this.aspectRatioFrameLayout.setVisibility(4);
      this.photoContainerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
      this.videoTextureView = new TextureView(this.parentActivity);
      this.videoTextureView.setOpaque(false);
      this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
    }
    this.textureUploaded = false;
    this.videoCrossfadeStarted = false;
    TextureView localTextureView = this.videoTextureView;
    this.videoCrossfadeAlpha = 0.0F;
    localTextureView.setAlpha(0.0F);
    this.videoPlayButton.setImageResource(2130837872);
    long l1;
    if (this.videoPlayer == null)
    {
      this.videoPlayer = new VideoPlayer();
      this.videoPlayer.setTextureView(this.videoTextureView);
      this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate()
      {
        public void onError(Exception paramException)
        {
          FileLog.e(paramException);
        }

        public void onRenderedFirstFrame()
        {
          if (!ArticleViewer.this.textureUploaded)
          {
            ArticleViewer.access$10302(ArticleViewer.this, true);
            ArticleViewer.this.containerView.invalidate();
          }
        }

        public void onStateChanged(boolean paramBoolean, int paramInt)
        {
          if (ArticleViewer.this.videoPlayer == null)
            return;
          if ((paramInt != 4) && (paramInt != 1));
          while (true)
          {
            try
            {
              ArticleViewer.this.parentActivity.getWindow().addFlags(128);
              if ((paramInt != 3) || (ArticleViewer.this.aspectRatioFrameLayout.getVisibility() == 0))
                continue;
              ArticleViewer.this.aspectRatioFrameLayout.setVisibility(0);
              if ((!ArticleViewer.this.videoPlayer.isPlaying()) || (paramInt == 4))
                break label168;
              if (ArticleViewer.this.isPlaying)
                continue;
              ArticleViewer.access$4902(ArticleViewer.this, true);
              ArticleViewer.this.videoPlayButton.setImageResource(2130837871);
              AndroidUtilities.runOnUIThread(ArticleViewer.this.updateProgressRunnable);
              ArticleViewer.this.updateVideoPlayerTime();
              return;
            }
            catch (Exception localException1)
            {
              FileLog.e(localException1);
              continue;
            }
            try
            {
              ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
            }
            catch (Exception localException2)
            {
              FileLog.e(localException2);
            }
            continue;
            label168: if (!ArticleViewer.this.isPlaying)
              continue;
            ArticleViewer.access$4902(ArticleViewer.this, false);
            ArticleViewer.this.videoPlayButton.setImageResource(2130837872);
            AndroidUtilities.cancelRunOnUIThread(ArticleViewer.this.updateProgressRunnable);
            if ((paramInt != 4) || (ArticleViewer.this.videoPlayerSeekbar.isDragging()))
              continue;
            ArticleViewer.this.videoPlayerSeekbar.setProgress(0.0F);
            ArticleViewer.this.videoPlayerControlFrameLayout.invalidate();
            ArticleViewer.this.videoPlayer.seekTo(0L);
            ArticleViewer.this.videoPlayer.pause();
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
          if (ArticleViewer.this.aspectRatioFrameLayout != null)
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
            localAspectRatioFrameLayout = ArticleViewer.this.aspectRatioFrameLayout;
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
        break label328;
      long l2 = this.videoPlayer.getDuration();
      l1 = l2;
      if (l2 == -9223372036854775807L)
        l1 = 0L;
    }
    while (true)
    {
      l1 /= 1000L;
      int i = (int)Math.ceil(this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[] { Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L), Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L) })));
      this.videoPlayer.preparePlayer(Uri.fromFile(paramFile), "other");
      this.bottomLayout.setVisibility(0);
      this.videoPlayer.setPlayWhenReady(paramBoolean);
      return;
      label328: l1 = 0L;
    }
  }

  private boolean processTouchEvent(MotionEvent paramMotionEvent)
  {
    float f5 = 0.0F;
    float f4 = 0.0F;
    if ((this.photoAnimationInProgress != 0) || (this.animationStartTime != 0L))
      return false;
    if ((paramMotionEvent.getPointerCount() == 1) && (this.gestureDetector.onTouchEvent(paramMotionEvent)) && (this.doubleTap))
    {
      this.doubleTap = false;
      this.moving = false;
      this.zooming = false;
      checkMinMax(false);
      return true;
    }
    if ((paramMotionEvent.getActionMasked() == 0) || (paramMotionEvent.getActionMasked() == 5))
    {
      this.discardTap = false;
      if (!this.scroller.isFinished())
        this.scroller.abortAnimation();
      if ((!this.draggingDown) && (!this.changingPage))
      {
        if ((!this.canZoom) || (paramMotionEvent.getPointerCount() != 2))
          break label254;
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
    label254: float f1;
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
        break label1044;
      if ((this.canZoom) && (paramMotionEvent.getPointerCount() == 2) && (!this.draggingDown) && (this.zooming) && (!this.changingPage))
      {
        this.discardTap = true;
        this.scale = ((float)Math.hypot(paramMotionEvent.getX(1) - paramMotionEvent.getX(0), paramMotionEvent.getY(1) - paramMotionEvent.getY(0)) / this.pinchStartDistance * this.pinchStartScale);
        this.translationX = (this.pinchCenterX - getContainerViewWidth() / 2 - (this.pinchCenterX - getContainerViewWidth() / 2 - this.pinchStartX) * (this.scale / this.pinchStartScale));
        this.translationY = (this.pinchCenterY - getContainerViewHeight() / 2 - (this.pinchCenterY - getContainerViewHeight() / 2 - this.pinchStartY) * (this.scale / this.pinchStartScale));
        updateMinMax(this.scale);
        this.photoContainerView.invalidate();
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
      if ((this.canDragDown) && (!this.draggingDown) && (this.scale == 1.0F) && (f2 >= AndroidUtilities.dp(30.0F)) && (f2 / 2.0F > f1))
      {
        this.draggingDown = true;
        this.moving = false;
        this.dragY = paramMotionEvent.getY();
        if (this.isActionBarVisible)
          toggleActionBar(false, true);
        return true;
      }
      if (this.draggingDown)
      {
        this.translationY = (paramMotionEvent.getY() - this.dragY);
        this.photoContainerView.invalidate();
        continue;
      }
      if ((!this.invalidCoords) && (this.animationStartTime == 0L))
      {
        f2 = this.moveStartX - paramMotionEvent.getX();
        f1 = this.moveStartY - paramMotionEvent.getY();
        if ((!this.moving) && ((this.scale != 1.0F) || (Math.abs(f1) + AndroidUtilities.dp(12.0F) >= Math.abs(f2))) && (this.scale == 1.0F))
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
        if ((this.translationX >= this.minX) || (this.rightImage.hasImage()))
        {
          f3 = f2;
          if (this.translationX > this.maxX)
          {
            f3 = f2;
            if (this.leftImage.hasImage());
          }
        }
        else
        {
          f3 = f2 / 3.0F;
        }
        if ((this.maxY == 0.0F) && (this.minY == 0.0F))
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
      if (this.scale != 1.0F)
        this.translationY -= f1;
      this.photoContainerView.invalidate();
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
          label1044: if ((paramMotionEvent.getActionMasked() != 3) && (paramMotionEvent.getActionMasked() != 1) && (paramMotionEvent.getActionMasked() != 6))
            break;
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
                  label1224: if (f3 >= this.minY)
                    break label1271;
                  f2 = this.minY;
                }
                while (true)
                {
                  animateTo(3.0F, f1, f2, true);
                  break;
                  f1 = f2;
                  if (f2 <= this.maxX)
                    break label1224;
                  f1 = this.maxX;
                  break label1224;
                  label1271: f2 = f3;
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
              closePhoto(true);
            while (true)
            {
              this.draggingDown = false;
              break;
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
          if (this.translationX < this.minX)
          {
            f1 = this.minX;
            label1546: if (this.translationY >= this.minY)
              break label1600;
            f2 = this.minY;
          }
          while (true)
          {
            animateTo(this.scale, f1, f2, false);
            break;
            f1 = f3;
            if (this.translationX <= this.maxX)
              break label1546;
            f1 = this.maxX;
            break label1546;
            label1600: if (this.translationY > this.maxY)
            {
              f2 = this.maxY;
              continue;
            }
          }
        }
      }
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
        this.photoContainerView.removeView(this.aspectRatioFrameLayout);
        this.aspectRatioFrameLayout = null;
      }
      if (this.videoTextureView != null)
        this.videoTextureView = null;
      if (this.isPlaying)
      {
        this.isPlaying = false;
        this.videoPlayButton.setImageResource(2130837872);
        AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
      }
      this.bottomLayout.setVisibility(8);
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  private boolean removeLastPageFromStack()
  {
    if (this.pagesStack.size() < 2)
      return false;
    this.pagesStack.remove(this.pagesStack.size() - 1);
    this.currentPage = ((TLRPC.WebPage)this.pagesStack.get(this.pagesStack.size() - 1));
    updateInterfaceForCurrentPage(true);
    return true;
  }

  private void saveCurrentPagePosition()
  {
    boolean bool = false;
    if (this.currentPage == null);
    int j;
    do
    {
      return;
      j = this.layoutManager.findFirstVisibleItemPosition();
    }
    while (j == -1);
    Object localObject = this.layoutManager.findViewByPosition(j);
    if (localObject != null);
    for (int i = ((View)localObject).getTop(); ; i = 0)
    {
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit();
      localObject = "article" + this.currentPage.id;
      localEditor = localEditor.putInt((String)localObject, j).putInt((String)localObject + "o", i);
      localObject = (String)localObject + "r";
      if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y)
        bool = true;
      localEditor.putBoolean((String)localObject, bool).commit();
      return;
    }
  }

  private boolean scaleToFill()
  {
    return false;
  }

  private void setCurrentCaption(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence))
    {
      this.captionTextView = this.captionTextViewOld;
      this.captionTextViewOld = this.captionTextViewNew;
      this.captionTextViewNew = this.captionTextView;
      Theme.createChatResources(null, true);
      paramCharSequence = Emoji.replaceEmoji(new SpannableStringBuilder(paramCharSequence.toString()), this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
      this.captionTextView.setTag(paramCharSequence);
      this.captionTextView.setText(paramCharSequence);
      this.captionTextView.setTextColor(-1);
      paramCharSequence = this.captionTextView;
      float f;
      if (this.actionBar.getVisibility() == 0)
        f = 1.0F;
      while (true)
      {
        paramCharSequence.setAlpha(f);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            int i = 4;
            ArticleViewer.this.captionTextViewOld.setTag(null);
            ArticleViewer.this.captionTextViewOld.setVisibility(4);
            TextView localTextView = ArticleViewer.this.captionTextViewNew;
            if (ArticleViewer.this.actionBar.getVisibility() == 0)
              i = 0;
            localTextView.setVisibility(i);
          }
        });
        return;
        f = 0.0F;
      }
    }
    this.captionTextView.setTextColor(-1);
    this.captionTextView.setTag(null);
    this.captionTextView.setVisibility(4);
  }

  private void setImageIndex(int paramInt, boolean paramBoolean)
  {
    if (this.currentIndex == paramInt)
      return;
    if (!paramBoolean)
      this.currentThumb = null;
    this.currentFileNames[0] = getFileName(paramInt);
    this.currentFileNames[1] = getFileName(paramInt + 1);
    this.currentFileNames[2] = getFileName(paramInt - 1);
    int j = this.currentIndex;
    this.currentIndex = paramInt;
    Object localObject;
    if (!this.imagesArr.isEmpty())
    {
      if ((this.currentIndex < 0) || (this.currentIndex >= this.imagesArr.size()))
      {
        closePhoto(false);
        return;
      }
      localObject = (TLRPC.PageBlock)this.imagesArr.get(this.currentIndex);
      if ((this.currentMedia != null) && (this.currentMedia == localObject))
      {
        paramInt = 1;
        label137: this.currentMedia = ((TLRPC.PageBlock)localObject);
        paramBoolean = isMediaVideo(this.currentIndex);
        if (paramBoolean)
          this.menuItem.showSubItem(3);
        setCurrentCaption(getText(this.currentMedia.caption, this.currentMedia.caption, this.currentMedia));
        if (this.currentAnimation == null)
          break label588;
        this.menuItem.setVisibility(8);
        this.menuItem.hideSubItem(1);
        this.actionBar.setTitle(LocaleController.getString("AttachGif", 2131165364));
      }
    }
    while (true)
    {
      int k = this.listView.getChildCount();
      int i = 0;
      label241: if (i < k)
      {
        localObject = this.listView.getChildAt(i);
        if ((localObject instanceof BlockSlideshowCell))
        {
          localObject = (BlockSlideshowCell)localObject;
          int m = ((BlockSlideshowCell)localObject).currentBlock.items.indexOf(this.currentMedia);
          if (m != -1)
            ((BlockSlideshowCell)localObject).innerListView.setCurrentItem(m, false);
        }
      }
      else
      {
        if (this.currentPlaceObject != null)
        {
          if (this.photoAnimationInProgress != 0)
            break label712;
          this.currentPlaceObject.imageReceiver.setVisible(true, true);
        }
        this.currentPlaceObject = getPlaceForPhoto(this.currentMedia);
        if (this.currentPlaceObject != null)
        {
          if (this.photoAnimationInProgress != 0)
            break label723;
          this.currentPlaceObject.imageReceiver.setVisible(false, true);
        }
        label370: if (paramInt == 0)
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
          if ((this.currentFileNames[0] == null) || (paramBoolean) || (this.radialProgressViews[0].backgroundState == 0))
            break label734;
        }
      }
      label332: label723: label734: for (paramBoolean = true; ; paramBoolean = false)
      {
        this.canZoom = paramBoolean;
        updateMinMax(this.scale);
        if (j != -1)
          break label739;
        setImages();
        paramInt = 0;
        while (paramInt < 3)
        {
          checkProgress(paramInt, false);
          paramInt += 1;
        }
        break;
        paramInt = 0;
        break label137;
        this.menuItem.setVisibility(0);
        if (this.imagesArr.size() == 1)
          if (paramBoolean)
            this.actionBar.setTitle(LocaleController.getString("AttachVideo", 2131165369));
        while (true)
        {
          this.menuItem.showSubItem(1);
          break;
          this.actionBar.setTitle(LocaleController.getString("AttachPhoto", 2131165367));
          continue;
          this.actionBar.setTitle(LocaleController.formatString("Of", 2131166154, new Object[] { Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.imagesArr.size()) }));
        }
        i += 1;
        break label241;
        this.showAfterAnimation = this.currentPlaceObject;
        break label332;
        this.hideAfterAnimation = this.currentPlaceObject;
        break label370;
      }
      label588: label739: checkProgress(0, false);
      label712: if (j > this.currentIndex)
      {
        localObject = this.rightImage;
        this.rightImage = this.centerImage;
        this.centerImage = this.leftImage;
        this.leftImage = ((ImageReceiver)localObject);
        localObject = this.radialProgressViews[0];
        this.radialProgressViews[0] = this.radialProgressViews[2];
        this.radialProgressViews[2] = localObject;
        setIndexToImage(this.leftImage, this.currentIndex - 1);
        checkProgress(1, false);
        checkProgress(2, false);
        return;
      }
      if (j >= this.currentIndex)
        break;
      localObject = this.leftImage;
      this.leftImage = this.centerImage;
      this.centerImage = this.rightImage;
      this.rightImage = ((ImageReceiver)localObject);
      localObject = this.radialProgressViews[0];
      this.radialProgressViews[0] = this.radialProgressViews[1];
      this.radialProgressViews[1] = localObject;
      setIndexToImage(this.rightImage, this.currentIndex + 1);
      checkProgress(1, false);
      checkProgress(2, false);
      return;
      paramInt = 0;
      paramBoolean = false;
    }
  }

  private void setImages()
  {
    if (this.photoAnimationInProgress == 0)
    {
      setIndexToImage(this.centerImage, this.currentIndex);
      setIndexToImage(this.rightImage, this.currentIndex + 1);
      setIndexToImage(this.leftImage, this.currentIndex - 1);
    }
  }

  private void setIndexToImage(ImageReceiver paramImageReceiver, int paramInt)
  {
    paramImageReceiver.setOrientation(0, false);
    int[] arrayOfInt = new int[1];
    TLRPC.FileLocation localFileLocation = getFileLocation(paramInt, arrayOfInt);
    Object localObject2;
    if (localFileLocation != null)
    {
      localObject1 = getMedia(paramInt);
      if ((localObject1 instanceof TLRPC.Photo))
      {
        localObject2 = (TLRPC.Photo)localObject1;
        if ((this.currentThumb == null) || (paramImageReceiver != this.centerImage))
          break label306;
      }
    }
    label140: label306: for (Object localObject1 = this.currentThumb; ; localObject1 = null)
    {
      if (arrayOfInt[0] == 0)
        arrayOfInt[0] = -1;
      localObject2 = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo)localObject2).sizes, 80);
      if (localObject1 != null)
      {
        localObject1 = new BitmapDrawable(null, (Bitmap)localObject1);
        if (localObject2 == null)
          break label140;
        localObject2 = ((TLRPC.PhotoSize)localObject2).location;
        label114: paramImageReceiver.setImage(localFileLocation, null, null, (Drawable)localObject1, (TLRPC.FileLocation)localObject2, "b", arrayOfInt[0], null, true);
      }
      do
      {
        return;
        localObject1 = null;
        break;
        localObject2 = null;
        break label114;
        if (!isMediaVideo(paramInt))
          continue;
        if (!(localFileLocation instanceof TLRPC.TL_fileLocationUnavailable))
        {
          localObject2 = null;
          localObject1 = localObject2;
          if (this.currentThumb != null)
          {
            localObject1 = localObject2;
            if (paramImageReceiver == this.centerImage)
              localObject1 = this.currentThumb;
          }
          if (localObject1 != null);
          for (localObject1 = new BitmapDrawable(null, (Bitmap)localObject1); ; localObject1 = null)
          {
            paramImageReceiver.setImage(null, null, null, (Drawable)localObject1, localFileLocation, "b", 0, null, true);
            return;
          }
        }
        paramImageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(2130838018));
        return;
      }
      while (this.currentAnimation == null);
      paramImageReceiver.setImageBitmap(this.currentAnimation);
      this.currentAnimation.setSecondParentView(this.photoContainerView);
      return;
      if (arrayOfInt[0] == 0)
      {
        paramImageReceiver.setImageBitmap((Bitmap)null);
        return;
      }
      paramImageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(2130838018));
      return;
    }
  }

  private void setRichTextParents(TLRPC.RichText paramRichText1, TLRPC.RichText paramRichText2)
  {
    if (paramRichText2 == null);
    while (true)
    {
      return;
      paramRichText2.parentRichText = paramRichText1;
      if ((paramRichText2 instanceof TLRPC.TL_textFixed))
      {
        setRichTextParents(paramRichText2, ((TLRPC.TL_textFixed)paramRichText2).text);
        return;
      }
      if ((paramRichText2 instanceof TLRPC.TL_textItalic))
      {
        setRichTextParents(paramRichText2, ((TLRPC.TL_textItalic)paramRichText2).text);
        return;
      }
      if ((paramRichText2 instanceof TLRPC.TL_textBold))
      {
        setRichTextParents(paramRichText2, ((TLRPC.TL_textBold)paramRichText2).text);
        return;
      }
      if ((paramRichText2 instanceof TLRPC.TL_textUnderline))
      {
        setRichTextParents(paramRichText2, ((TLRPC.TL_textUnderline)paramRichText2).text);
        return;
      }
      if ((paramRichText2 instanceof TLRPC.TL_textStrike))
      {
        setRichTextParents(paramRichText1, ((TLRPC.TL_textStrike)paramRichText2).text);
        return;
      }
      if ((paramRichText2 instanceof TLRPC.TL_textEmail))
      {
        setRichTextParents(paramRichText2, ((TLRPC.TL_textEmail)paramRichText2).text);
        return;
      }
      if ((paramRichText2 instanceof TLRPC.TL_textUrl))
      {
        setRichTextParents(paramRichText2, ((TLRPC.TL_textUrl)paramRichText2).text);
        return;
      }
      if (!(paramRichText2 instanceof TLRPC.TL_textConcat))
        continue;
      int j = paramRichText2.texts.size();
      int i = 0;
      while (i < j)
      {
        setRichTextParents(paramRichText2, (TLRPC.RichText)paramRichText2.texts.get(i));
        i += 1;
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

  private void showActionBar(int paramInt)
  {
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.backButton, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.shareContainer, "alpha", new float[] { 1.0F }) });
    localAnimatorSet.setDuration(150L);
    localAnimatorSet.setStartDelay(paramInt);
    localAnimatorSet.start();
  }

  private void showProgressView(boolean paramBoolean)
  {
    if (this.progressViewAnimation != null)
      this.progressViewAnimation.cancel();
    this.progressViewAnimation = new AnimatorSet();
    if (paramBoolean)
    {
      this.progressView.setVisibility(0);
      this.shareContainer.setEnabled(false);
      this.progressViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.shareButton, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.shareButton, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.shareButton, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 1.0F }) });
    }
    while (true)
    {
      this.progressViewAnimation.addListener(new AnimatorListenerAdapter(paramBoolean)
      {
        public void onAnimationCancel(Animator paramAnimator)
        {
          if ((ArticleViewer.this.progressViewAnimation != null) && (ArticleViewer.this.progressViewAnimation.equals(paramAnimator)))
            ArticleViewer.access$5902(ArticleViewer.this, null);
        }

        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((ArticleViewer.this.progressViewAnimation != null) && (ArticleViewer.this.progressViewAnimation.equals(paramAnimator)))
          {
            if (!this.val$show)
              ArticleViewer.this.progressView.setVisibility(4);
          }
          else
            return;
          ArticleViewer.this.shareButton.setVisibility(4);
        }
      });
      this.progressViewAnimation.setDuration(150L);
      this.progressViewAnimation.start();
      return;
      this.shareButton.setVisibility(0);
      this.shareContainer.setEnabled(true);
      this.progressViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.shareButton, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.shareButton, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.shareButton, "alpha", new float[] { 1.0F }) });
    }
  }

  private void toggleActionBar(boolean paramBoolean1, boolean paramBoolean2)
  {
    float f1 = 1.0F;
    if (paramBoolean1)
    {
      this.actionBar.setVisibility(0);
      if (this.videoPlayer != null)
        this.bottomLayout.setVisibility(0);
      if (this.captionTextView.getTag() != null)
        this.captionTextView.setVisibility(0);
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
          break label256;
        f2 = 1.0F;
        label129: ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localObject2, "alpha", new float[] { f2 }));
        if (this.captionTextView.getTag() != null)
        {
          localObject2 = this.captionTextView;
          if (!paramBoolean1)
            break label262;
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
              if ((ArticleViewer.this.currentActionBarAnimation != null) && (ArticleViewer.this.currentActionBarAnimation.equals(paramAnimator)))
              {
                ArticleViewer.this.actionBar.setVisibility(8);
                if (ArticleViewer.this.videoPlayer != null)
                  ArticleViewer.this.bottomLayout.setVisibility(8);
                if (ArticleViewer.this.captionTextView.getTag() != null)
                  ArticleViewer.this.captionTextView.setVisibility(4);
                ArticleViewer.access$10402(ArticleViewer.this, null);
              }
            }
          });
        this.currentActionBarAnimation.setDuration(200L);
        this.currentActionBarAnimation.start();
        return;
        f2 = 0.0F;
        break;
        label256: f2 = 0.0F;
        break label129;
        label262: f1 = 0.0F;
      }
    }
    Object localObject1 = this.actionBar;
    if (paramBoolean1)
    {
      f2 = 1.0F;
      label280: ((ActionBar)localObject1).setAlpha(f2);
      localObject1 = this.bottomLayout;
      if (!paramBoolean1)
        break label387;
      f2 = 1.0F;
      label300: ((FrameLayout)localObject1).setAlpha(f2);
      if (this.captionTextView.getTag() != null)
      {
        localObject1 = this.captionTextView;
        if (!paramBoolean1)
          break label393;
      }
    }
    while (true)
    {
      ((TextView)localObject1).setAlpha(f1);
      if (paramBoolean1)
        break;
      this.actionBar.setVisibility(8);
      if (this.videoPlayer != null)
        this.bottomLayout.setVisibility(8);
      if (this.captionTextView.getTag() == null)
        break;
      this.captionTextView.setVisibility(4);
      return;
      f2 = 0.0F;
      break label280;
      label387: f2 = 0.0F;
      break label300;
      label393: f1 = 0.0F;
    }
  }

  private void updateInterfaceForCurrentPage(boolean paramBoolean)
  {
    if ((this.currentPage == null) || (this.currentPage.cached_page == null));
    label774: 
    while (true)
    {
      return;
      this.blocks.clear();
      this.photoBlocks.clear();
      int i = 0;
      if (i < this.currentPage.cached_page.blocks.size())
      {
        localObject1 = (TLRPC.PageBlock)this.currentPage.cached_page.blocks.get(i);
        if ((localObject1 instanceof TLRPC.TL_pageBlockUnsupported));
        while (true)
        {
          i += 1;
          break;
          if ((localObject1 instanceof TLRPC.TL_pageBlockAnchor))
          {
            this.anchors.put(((TLRPC.PageBlock)localObject1).name, Integer.valueOf(this.blocks.size()));
            continue;
          }
          setRichTextParents(null, ((TLRPC.PageBlock)localObject1).text);
          setRichTextParents(null, ((TLRPC.PageBlock)localObject1).caption);
          if ((localObject1 instanceof TLRPC.TL_pageBlockAuthorDate))
          {
            setRichTextParents(null, ((TLRPC.TL_pageBlockAuthorDate)localObject1).author);
            if (i == 0)
              ((TLRPC.PageBlock)localObject1).first = true;
            addAllMediaFromBlock((TLRPC.PageBlock)localObject1);
            this.blocks.add(localObject1);
            if (!(localObject1 instanceof TLRPC.TL_pageBlockEmbedPost))
              continue;
            if (((TLRPC.PageBlock)localObject1).blocks.isEmpty())
              break label538;
            ((TLRPC.PageBlock)localObject1).level = -1;
            j = 0;
            label213: if (j >= ((TLRPC.PageBlock)localObject1).blocks.size())
              break label538;
            localObject2 = (TLRPC.PageBlock)((TLRPC.PageBlock)localObject1).blocks.get(j);
            if (!(localObject2 instanceof TLRPC.TL_pageBlockUnsupported))
              break label459;
          }
          while (true)
          {
            j += 1;
            break label213;
            if ((localObject1 instanceof TLRPC.TL_pageBlockCollage))
            {
              localObject2 = (TLRPC.TL_pageBlockCollage)localObject1;
              j = 0;
              while (j < ((TLRPC.TL_pageBlockCollage)localObject2).items.size())
              {
                setRichTextParents(null, ((TLRPC.PageBlock)((TLRPC.TL_pageBlockCollage)localObject2).items.get(j)).text);
                setRichTextParents(null, ((TLRPC.PageBlock)((TLRPC.TL_pageBlockCollage)localObject2).items.get(j)).caption);
                j += 1;
              }
              break;
            }
            if ((localObject1 instanceof TLRPC.TL_pageBlockList))
            {
              localObject2 = (TLRPC.TL_pageBlockList)localObject1;
              j = 0;
              while (j < ((TLRPC.TL_pageBlockList)localObject2).items.size())
              {
                setRichTextParents(null, (TLRPC.RichText)((TLRPC.TL_pageBlockList)localObject2).items.get(j));
                j += 1;
              }
              break;
            }
            if (!(localObject1 instanceof TLRPC.TL_pageBlockSlideshow))
              break;
            localObject2 = (TLRPC.TL_pageBlockSlideshow)localObject1;
            j = 0;
            while (j < ((TLRPC.TL_pageBlockSlideshow)localObject2).items.size())
            {
              setRichTextParents(null, ((TLRPC.PageBlock)((TLRPC.TL_pageBlockSlideshow)localObject2).items.get(j)).text);
              setRichTextParents(null, ((TLRPC.PageBlock)((TLRPC.TL_pageBlockSlideshow)localObject2).items.get(j)).caption);
              j += 1;
            }
            break;
            label459: if ((localObject2 instanceof TLRPC.TL_pageBlockAnchor))
            {
              this.anchors.put(((TLRPC.PageBlock)localObject2).name, Integer.valueOf(this.blocks.size()));
              continue;
            }
            ((TLRPC.PageBlock)localObject2).level = 1;
            if (j == ((TLRPC.PageBlock)localObject1).blocks.size() - 1)
              ((TLRPC.PageBlock)localObject2).bottom = true;
            this.blocks.add(localObject2);
            addAllMediaFromBlock((TLRPC.PageBlock)localObject2);
          }
          label538: if ((((TLRPC.PageBlock)localObject1).caption instanceof TLRPC.TL_textEmpty))
            continue;
          localObject2 = new TLRPC.TL_pageBlockParagraph();
          ((TLRPC.TL_pageBlockParagraph)localObject2).caption = ((TLRPC.PageBlock)localObject1).caption;
          this.blocks.add(localObject2);
        }
      }
      this.adapter.notifyDataSetChanged();
      if ((this.pagesStack.size() != 1) && (!paramBoolean))
        break;
      Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0);
      Object localObject2 = "article" + this.currentPage.id;
      int j = ((SharedPreferences)localObject1).getInt((String)localObject2, -1);
      boolean bool = ((SharedPreferences)localObject1).getBoolean((String)localObject2 + "r", true);
      if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y)
      {
        paramBoolean = true;
        if (bool != paramBoolean)
          break label766;
      }
      label766: for (i = ((SharedPreferences)localObject1).getInt((String)localObject2 + "o", 0) - this.listView.getPaddingTop(); ; i = AndroidUtilities.dp(10.0F))
      {
        if (j == -1)
          break label774;
        this.layoutManager.scrollToPositionWithOffset(j, i);
        return;
        paramBoolean = false;
        break;
      }
    }
    this.layoutManager.scrollToPositionWithOffset(0, 0);
  }

  private void updateMinMax(float paramFloat)
  {
    int i = (int)(this.centerImage.getImageWidth() * paramFloat - getContainerViewWidth()) / 2;
    int j = (int)(this.centerImage.getImageHeight() * paramFloat - getContainerViewHeight()) / 2;
    if (i > 0)
    {
      this.minX = (-i);
      this.maxX = i;
    }
    while (j > 0)
    {
      this.minY = (-j);
      this.maxY = j;
      return;
      this.maxX = 0.0F;
      this.minX = 0.0F;
    }
    this.maxY = 0.0F;
    this.minY = 0.0F;
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
      long l1 = this.videoPlayer.getCurrentPosition() / 1000L;
      long l2 = this.videoPlayer.getDuration() / 1000L;
      if ((l2 != -9223372036854775807L) && (l1 != -9223372036854775807L))
      {
        str = String.format("%02d:%02d / %02d:%02d", new Object[] { Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L), Long.valueOf(l2 / 60L), Long.valueOf(l2 % 60L) });
        continue;
      }
      str = "00:00 / 00:00";
    }
  }

  protected void cancelCheckLongPress()
  {
    this.checkingForLongPress = false;
    if (this.pendingCheckForLongPress != null)
      this.windowView.removeCallbacks(this.pendingCheckForLongPress);
    if (this.pendingCheckForTap != null)
      this.windowView.removeCallbacks(this.pendingCheckForTap);
  }

  public void close(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((this.parentActivity == null) || (!this.isVisible) || (checkAnimation()))
      return;
    if (this.fullscreenVideoContainer.getVisibility() == 0)
    {
      if (this.customView == null)
        break label358;
      this.fullscreenVideoContainer.setVisibility(4);
      this.customViewCallback.onCustomViewHidden();
      this.fullscreenVideoContainer.removeView(this.customView);
      this.customView = null;
    }
    while (paramBoolean2)
    {
      boolean bool;
      if (this.isPhotoVisible)
      {
        if (!paramBoolean2)
        {
          bool = true;
          closePhoto(bool);
          if (!paramBoolean2)
            break;
        }
      }
      else
      {
        if (this.openUrlReqId != 0)
        {
          ConnectionsManager.getInstance().cancelRequest(this.openUrlReqId, true);
          this.openUrlReqId = 0;
          showProgressView(false);
        }
        if (this.previewsReqId != 0)
        {
          ConnectionsManager.getInstance().cancelRequest(this.previewsReqId, true);
          this.previewsReqId = 0;
          showProgressView(false);
        }
        saveCurrentPagePosition();
        if ((paramBoolean1) && (!paramBoolean2) && (removeLastPageFromStack()))
          break;
      }
      try
      {
        if (this.visibleDialog != null)
        {
          this.visibleDialog.dismiss();
          this.visibleDialog = null;
        }
        AnimatorSet localAnimatorSet = new AnimatorSet();
        localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.windowView, "translationX", new float[] { 0.0F, AndroidUtilities.dp(56.0F) }) });
        this.animationInProgress = 2;
        this.animationEndRunnable = new Runnable()
        {
          public void run()
          {
            if (ArticleViewer.this.containerView == null)
              return;
            if (Build.VERSION.SDK_INT >= 18)
              ArticleViewer.this.containerView.setLayerType(0, null);
            ArticleViewer.access$5702(ArticleViewer.this, 0);
            ArticleViewer.this.onClosed();
          }
        };
        localAnimatorSet.setDuration(150L);
        localAnimatorSet.setInterpolator(this.interpolator);
        localAnimatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            if (ArticleViewer.this.animationEndRunnable != null)
            {
              ArticleViewer.this.animationEndRunnable.run();
              ArticleViewer.access$5802(ArticleViewer.this, null);
            }
          }
        });
        this.transitionAnimationStartTime = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= 18)
          this.containerView.setLayerType(2, null);
        localAnimatorSet.start();
        return;
        label358: if (this.fullscreenedVideo == null)
          continue;
        this.fullscreenedVideo.exitFullscreen();
        continue;
        bool = false;
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
      }
    }
  }

  public void closePhoto(boolean paramBoolean)
  {
    if ((this.parentActivity == null) || (!this.isPhotoVisible) || (checkPhotoAnimation()))
      return;
    releasePlayer();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileLoadProgressChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    this.isActionBarVisible = false;
    if (this.velocityTracker != null)
    {
      this.velocityTracker.recycle();
      this.velocityTracker = null;
    }
    PlaceProviderObject localPlaceProviderObject = getPlaceForPhoto(this.currentMedia);
    AnimatorSet localAnimatorSet;
    Object localObject2;
    int j;
    int k;
    int i;
    if (paramBoolean)
    {
      this.photoAnimationInProgress = 1;
      this.animatingImageView.setVisibility(0);
      this.photoContainerView.invalidate();
      localAnimatorSet = new AnimatorSet();
      localObject2 = this.animatingImageView.getLayoutParams();
      j = this.centerImage.getOrientation();
      k = 0;
      i = k;
      if (localPlaceProviderObject != null)
      {
        i = k;
        if (localPlaceProviderObject.imageReceiver != null)
          i = localPlaceProviderObject.imageReceiver.getAnimatedOrientation();
      }
      if (i == 0)
        break label1579;
    }
    while (true)
    {
      this.animatingImageView.setOrientation(i);
      Object localObject1;
      label280: float f2;
      float f3;
      float f1;
      float f4;
      if (localPlaceProviderObject != null)
      {
        localObject1 = this.animatingImageView;
        if (localPlaceProviderObject.radius != 0)
        {
          paramBoolean = true;
          ((ClippingImageView)localObject1).setNeedRadius(paramBoolean);
          localObject1 = localPlaceProviderObject.imageReceiver.getDrawRegion();
          ((ViewGroup.LayoutParams)localObject2).width = (((Rect)localObject1).right - ((Rect)localObject1).left);
          ((ViewGroup.LayoutParams)localObject2).height = (((Rect)localObject1).bottom - ((Rect)localObject1).top);
          this.animatingImageView.setImageBitmap(localPlaceProviderObject.thumb);
          this.animatingImageView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
          f2 = AndroidUtilities.displaySize.x / ((ViewGroup.LayoutParams)localObject2).width;
          f3 = (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight) / ((ViewGroup.LayoutParams)localObject2).height;
          f1 = f2;
          if (f2 > f3)
            f1 = f3;
          f2 = ((ViewGroup.LayoutParams)localObject2).width;
          float f5 = this.scale;
          f3 = ((ViewGroup.LayoutParams)localObject2).height;
          f4 = this.scale;
          f2 = (AndroidUtilities.displaySize.x - f2 * f5 * f1) / 2.0F;
          if ((Build.VERSION.SDK_INT < 21) || (this.lastInsets == null))
            break label1576;
          f2 = ((WindowInsets)this.lastInsets).getSystemWindowInsetLeft() + f2;
        }
      }
      label1574: label1576: 
      while (true)
      {
        f3 = (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight - f3 * f4 * f1) / 2.0F;
        this.animatingImageView.setTranslationX(f2 + this.translationX);
        this.animatingImageView.setTranslationY(f3 + this.translationY);
        this.animatingImageView.setScaleX(this.scale * f1);
        this.animatingImageView.setScaleY(f1 * this.scale);
        if (localPlaceProviderObject != null)
        {
          localPlaceProviderObject.imageReceiver.setVisible(false, true);
          int m = Math.abs(((Rect)localObject1).left - localPlaceProviderObject.imageReceiver.getImageX());
          int n = Math.abs(((Rect)localObject1).top - localPlaceProviderObject.imageReceiver.getImageY());
          localObject2 = new int[2];
          localPlaceProviderObject.parentView.getLocationInWindow(localObject2);
          j = localObject2[1] - (localPlaceProviderObject.viewY + ((Rect)localObject1).top) + localPlaceProviderObject.clipTopAddition;
          i = j;
          if (j < 0)
            i = 0;
          k = localPlaceProviderObject.viewY + ((Rect)localObject1).top + (((Rect)localObject1).bottom - ((Rect)localObject1).top) - (localObject2[1] + localPlaceProviderObject.parentView.getHeight()) + localPlaceProviderObject.clipBottomAddition;
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
          localObject2 = this.animationValues[1];
          f1 = localPlaceProviderObject.viewY;
          localObject2[3] = (((Rect)localObject1).top * localPlaceProviderObject.scale + f1);
          this.animationValues[1][4] = (m * localPlaceProviderObject.scale);
          this.animationValues[1][5] = (i * localPlaceProviderObject.scale);
          this.animationValues[1][6] = (j * localPlaceProviderObject.scale);
          this.animationValues[1][7] = localPlaceProviderObject.radius;
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofInt(this.photoBackgroundDrawable, "alpha", new int[] { 0 }), ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[] { 0.0F }) });
          this.photoAnimationEndRunnable = new Runnable(localPlaceProviderObject)
          {
            public void run()
            {
              if (Build.VERSION.SDK_INT >= 18)
                ArticleViewer.this.photoContainerView.setLayerType(0, null);
              ArticleViewer.this.photoContainerView.setVisibility(4);
              ArticleViewer.this.photoContainerBackground.setVisibility(4);
              ArticleViewer.access$10902(ArticleViewer.this, 0);
              ArticleViewer.this.onPhotoClosed(this.val$object);
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
                  if (ArticleViewer.this.photoAnimationEndRunnable != null)
                  {
                    ArticleViewer.this.photoAnimationEndRunnable.run();
                    ArticleViewer.access$11402(ArticleViewer.this, null);
                  }
                }
              });
            }
          });
          this.photoTransitionAnimationStartTime = System.currentTimeMillis();
          if (Build.VERSION.SDK_INT >= 18)
            this.photoContainerView.setLayerType(2, null);
          localAnimatorSet.start();
        }
        while (true)
        {
          if (this.currentAnimation == null)
            break label1574;
          this.currentAnimation.setSecondParentView(null);
          this.currentAnimation = null;
          this.centerImage.setImageBitmap((Drawable)null);
          return;
          paramBoolean = false;
          break;
          this.animatingImageView.setNeedRadius(false);
          ((ViewGroup.LayoutParams)localObject2).width = this.centerImage.getImageWidth();
          ((ViewGroup.LayoutParams)localObject2).height = this.centerImage.getImageHeight();
          this.animatingImageView.setImageBitmap(this.centerImage.getBitmap());
          localObject1 = null;
          break label280;
          i = AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight;
          localObject1 = ObjectAnimator.ofInt(this.photoBackgroundDrawable, "alpha", new int[] { 0 });
          localObject2 = ObjectAnimator.ofFloat(this.animatingImageView, "alpha", new float[] { 0.0F });
          ClippingImageView localClippingImageView = this.animatingImageView;
          if (this.translationY >= 0.0F)
            f1 = i;
          while (true)
          {
            localAnimatorSet.playTogether(new Animator[] { localObject1, localObject2, ObjectAnimator.ofFloat(localClippingImageView, "translationY", new float[] { f1 }), ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[] { 0.0F }) });
            break;
            f1 = -i;
          }
          localObject1 = new AnimatorSet();
          ((AnimatorSet)localObject1).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.photoContainerView, "scaleX", new float[] { 0.9F }), ObjectAnimator.ofFloat(this.photoContainerView, "scaleY", new float[] { 0.9F }), ObjectAnimator.ofInt(this.photoBackgroundDrawable, "alpha", new int[] { 0 }), ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[] { 0.0F }) });
          this.photoAnimationInProgress = 2;
          this.photoAnimationEndRunnable = new Runnable(localPlaceProviderObject)
          {
            public void run()
            {
              if (ArticleViewer.this.photoContainerView == null)
                return;
              if (Build.VERSION.SDK_INT >= 18)
                ArticleViewer.this.photoContainerView.setLayerType(0, null);
              ArticleViewer.this.photoContainerView.setVisibility(4);
              ArticleViewer.this.photoContainerBackground.setVisibility(4);
              ArticleViewer.access$10902(ArticleViewer.this, 0);
              ArticleViewer.this.onPhotoClosed(this.val$object);
              ArticleViewer.this.photoContainerView.setScaleX(1.0F);
              ArticleViewer.this.photoContainerView.setScaleY(1.0F);
            }
          };
          ((AnimatorSet)localObject1).setDuration(200L);
          ((AnimatorSet)localObject1).addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              if (ArticleViewer.this.photoAnimationEndRunnable != null)
              {
                ArticleViewer.this.photoAnimationEndRunnable.run();
                ArticleViewer.access$11402(ArticleViewer.this, null);
              }
            }
          });
          this.photoTransitionAnimationStartTime = System.currentTimeMillis();
          if (Build.VERSION.SDK_INT >= 18)
            this.photoContainerView.setLayerType(2, null);
          ((AnimatorSet)localObject1).start();
        }
        break;
      }
      label1579: i = j;
    }
  }

  public void collapse()
  {
    if ((this.parentActivity == null) || (!this.isVisible) || (checkAnimation()))
      return;
    if (this.fullscreenVideoContainer.getVisibility() == 0)
    {
      if (this.customView == null)
        break label505;
      this.fullscreenVideoContainer.setVisibility(4);
      this.customViewCallback.onCustomViewHidden();
      this.fullscreenVideoContainer.removeView(this.customView);
      this.customView = null;
    }
    while (true)
    {
      if (this.isPhotoVisible)
        closePhoto(false);
      try
      {
        if (this.visibleDialog != null)
        {
          this.visibleDialog.dismiss();
          this.visibleDialog = null;
        }
        AnimatorSet localAnimatorSet = new AnimatorSet();
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this.containerView, "translationX", new float[] { this.containerView.getMeasuredWidth() - AndroidUtilities.dp(56.0F) });
        FrameLayout localFrameLayout = this.containerView;
        int j = ActionBar.getCurrentActionBarHeight();
        if (Build.VERSION.SDK_INT >= 21)
        {
          i = AndroidUtilities.statusBarHeight;
          localAnimatorSet.playTogether(new Animator[] { localObjectAnimator, ObjectAnimator.ofFloat(localFrameLayout, "translationY", new float[] { i + j }), ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.listView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.listView, "translationY", new float[] { -AndroidUtilities.dp(56.0F) }), ObjectAnimator.ofFloat(this.headerView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.backButton, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.backButton, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.backButton, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.shareContainer, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.shareContainer, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.shareContainer, "scaleY", new float[] { 1.0F }) });
          this.collapsed = true;
          this.animationInProgress = 2;
          this.animationEndRunnable = new Runnable()
          {
            public void run()
            {
              if (ArticleViewer.this.containerView == null)
                return;
              if (Build.VERSION.SDK_INT >= 18)
                ArticleViewer.this.containerView.setLayerType(0, null);
              ArticleViewer.access$5702(ArticleViewer.this, 0);
              ((WindowManager)ArticleViewer.this.parentActivity.getSystemService("window")).updateViewLayout(ArticleViewer.this.windowView, ArticleViewer.this.windowLayoutParams);
            }
          };
          localAnimatorSet.setInterpolator(new DecelerateInterpolator());
          localAnimatorSet.setDuration(250L);
          localAnimatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              if (ArticleViewer.this.animationEndRunnable != null)
              {
                ArticleViewer.this.animationEndRunnable.run();
                ArticleViewer.access$5802(ArticleViewer.this, null);
              }
            }
          });
          this.transitionAnimationStartTime = System.currentTimeMillis();
          if (Build.VERSION.SDK_INT >= 18)
            this.containerView.setLayerType(2, null);
          this.backDrawable.setRotation(1.0F, true);
          localAnimatorSet.start();
          return;
          label505: if (this.fullscreenedVideo == null)
            continue;
          this.fullscreenedVideo.exitFullscreen();
        }
      }
      catch (Exception localException)
      {
        while (true)
        {
          FileLog.e(localException);
          continue;
          int i = 0;
        }
      }
    }
  }

  public void destroyArticleViewer()
  {
    if ((this.parentActivity == null) || (this.windowView == null))
      return;
    releasePlayer();
    try
    {
      if (this.windowView.getParent() != null)
        ((WindowManager)this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
      this.windowView = null;
      int i = 0;
      while (i < this.createdWebViews.size())
      {
        ((BlockEmbedCell)this.createdWebViews.get(i)).destroyWebView(true);
        i += 1;
      }
    }
    catch (Exception localException1)
    {
      while (true)
        FileLog.e(localException1);
      this.createdWebViews.clear();
    }
    try
    {
      this.parentActivity.getWindow().clearFlags(128);
      Instance = null;
      return;
    }
    catch (Exception localException2)
    {
      while (true)
        FileLog.e(localException2);
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    int i = 0;
    if (paramInt == NotificationCenter.FileDidFailedLoad)
    {
      paramArrayOfObject = (String)paramArrayOfObject[0];
      paramInt = i;
      if (paramInt < 3)
      {
        if ((this.currentFileNames[paramInt] == null) || (!this.currentFileNames[paramInt].equals(paramArrayOfObject)))
          break label63;
        this.radialProgressViews[paramInt].setProgress(1.0F, true);
        checkProgress(paramInt, true);
      }
    }
    label63: label233: 
    do
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
              break label156;
            if ((this.currentFileNames[paramInt] != null) && (this.currentFileNames[paramInt].equals(paramArrayOfObject)))
            {
              this.radialProgressViews[paramInt].setProgress(1.0F, true);
              checkProgress(paramInt, true);
              if ((paramInt != 0) || (!isMediaVideo(this.currentIndex)))
                break;
              onActionClick(false);
              return;
            }
            paramInt += 1;
          }
          continue;
        }
        if (paramInt != NotificationCenter.FileLoadProgressChanged)
          break label233;
        String str = (String)paramArrayOfObject[0];
        paramInt = 0;
        while (paramInt < 3)
        {
          if ((this.currentFileNames[paramInt] != null) && (this.currentFileNames[paramInt].equals(str)))
          {
            Float localFloat = (Float)paramArrayOfObject[1];
            this.radialProgressViews[paramInt].setProgress(localFloat.floatValue(), true);
          }
          paramInt += 1;
        }
      }
    while ((paramInt != NotificationCenter.emojiDidLoaded) || (this.captionTextView == null));
    label156: this.captionTextView.invalidate();
  }

  public float getAnimationValue()
  {
    return this.animationValue;
  }

  public boolean isShowingImage(TLRPC.PageBlock paramPageBlock)
  {
    return (this.isPhotoVisible) && (!this.disableShowCheck) && (paramPageBlock != null) && (this.currentMedia == paramPageBlock);
  }

  public boolean isVisible()
  {
    return this.isVisible;
  }

  public boolean onDoubleTap(MotionEvent paramMotionEvent)
  {
    if ((!this.canZoom) || ((this.scale == 1.0F) && ((this.translationY != 0.0F) || (this.translationX != 0.0F))));
    do
      return false;
    while ((this.animationStartTime != 0L) || (this.photoAnimationInProgress != 0));
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
      this.photoContainerView.postInvalidate();
    }
    return false;
  }

  public void onLongPress(MotionEvent paramMotionEvent)
  {
  }

  public void onPause()
  {
    if (this.currentAnimation != null)
      closePhoto(false);
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
    boolean bool = false;
    if (this.discardTap)
      return false;
    if ((this.aspectRatioFrameLayout != null) && (this.aspectRatioFrameLayout.getVisibility() == 0));
    for (int i = 1; (this.radialProgressViews[0] != null) && (this.photoContainerView != null) && (i == 0); i = 0)
    {
      i = this.radialProgressViews[0].backgroundState;
      if ((i <= 0) || (i > 3))
        break;
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      if ((f1 < (getContainerViewWidth() - AndroidUtilities.dp(100.0F)) / 2.0F) || (f1 > (getContainerViewWidth() + AndroidUtilities.dp(100.0F)) / 2.0F) || (f2 < (getContainerViewHeight() - AndroidUtilities.dp(100.0F)) / 2.0F) || (f2 > (getContainerViewHeight() + AndroidUtilities.dp(100.0F)) / 2.0F))
        break;
      onActionClick(true);
      checkProgress(0, true);
      return true;
    }
    if (!this.isActionBarVisible)
      bool = true;
    toggleActionBar(bool, true);
    return true;
  }

  public boolean onSingleTapUp(MotionEvent paramMotionEvent)
  {
    return false;
  }

  public boolean open(MessageObject paramMessageObject)
  {
    return open(paramMessageObject, true);
  }

  public boolean openPhoto(TLRPC.PageBlock paramPageBlock)
  {
    if ((this.parentActivity == null) || (this.isPhotoVisible) || (checkPhotoAnimation()) || (paramPageBlock == null))
      return false;
    PlaceProviderObject localPlaceProviderObject = getPlaceForPhoto(paramPageBlock);
    if (localPlaceProviderObject == null)
      return false;
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileLoadProgressChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    if (this.velocityTracker == null)
      this.velocityTracker = VelocityTracker.obtain();
    this.isPhotoVisible = true;
    toggleActionBar(true, false);
    this.actionBar.setAlpha(0.0F);
    this.bottomLayout.setAlpha(0.0F);
    this.captionTextView.setAlpha(0.0F);
    this.photoBackgroundDrawable.setAlpha(0);
    this.disableShowCheck = true;
    this.photoAnimationInProgress = 1;
    if (paramPageBlock != null)
      this.currentAnimation = localPlaceProviderObject.imageReceiver.getAnimation();
    int i = this.photoBlocks.indexOf(paramPageBlock);
    this.imagesArr.clear();
    int j;
    if ((!(paramPageBlock instanceof TLRPC.TL_pageBlockVideo)) || (isVideoBlock(paramPageBlock)))
    {
      this.imagesArr.addAll(this.photoBlocks);
      onPhotoShow(i, localPlaceProviderObject);
      paramPageBlock = localPlaceProviderObject.imageReceiver.getDrawRegion();
      i = localPlaceProviderObject.imageReceiver.getOrientation();
      j = localPlaceProviderObject.imageReceiver.getAnimatedOrientation();
      if (j == 0)
        break label1224;
      i = j;
    }
    label300: label1221: label1224: 
    while (true)
    {
      this.animatingImageView.setVisibility(0);
      this.animatingImageView.setRadius(localPlaceProviderObject.radius);
      this.animatingImageView.setOrientation(i);
      Object localObject = this.animatingImageView;
      boolean bool;
      float f2;
      float f3;
      float f1;
      if (localPlaceProviderObject.radius != 0)
      {
        bool = true;
        ((ClippingImageView)localObject).setNeedRadius(bool);
        this.animatingImageView.setImageBitmap(localPlaceProviderObject.thumb);
        this.animatingImageView.setAlpha(1.0F);
        this.animatingImageView.setPivotX(0.0F);
        this.animatingImageView.setPivotY(0.0F);
        this.animatingImageView.setScaleX(localPlaceProviderObject.scale);
        this.animatingImageView.setScaleY(localPlaceProviderObject.scale);
        this.animatingImageView.setTranslationX(localPlaceProviderObject.viewX + paramPageBlock.left * localPlaceProviderObject.scale);
        this.animatingImageView.setTranslationY(localPlaceProviderObject.viewY + paramPageBlock.top * localPlaceProviderObject.scale);
        localObject = this.animatingImageView.getLayoutParams();
        ((ViewGroup.LayoutParams)localObject).width = (paramPageBlock.right - paramPageBlock.left);
        ((ViewGroup.LayoutParams)localObject).height = (paramPageBlock.bottom - paramPageBlock.top);
        this.animatingImageView.setLayoutParams((ViewGroup.LayoutParams)localObject);
        f2 = AndroidUtilities.displaySize.x / ((ViewGroup.LayoutParams)localObject).width;
        f3 = (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight) / ((ViewGroup.LayoutParams)localObject).height;
        f1 = f2;
        if (f2 > f3)
          f1 = f3;
        f2 = ((ViewGroup.LayoutParams)localObject).width;
        f3 = ((ViewGroup.LayoutParams)localObject).height;
        f2 = (AndroidUtilities.displaySize.x - f2 * f1) / 2.0F;
        if ((Build.VERSION.SDK_INT < 21) || (this.lastInsets == null))
          break label1221;
        f2 = ((WindowInsets)this.lastInsets).getSystemWindowInsetLeft() + f2;
      }
      while (true)
      {
        f3 = (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight - f3 * f1) / 2.0F;
        int m = Math.abs(paramPageBlock.left - localPlaceProviderObject.imageReceiver.getImageX());
        int n = Math.abs(paramPageBlock.top - localPlaceProviderObject.imageReceiver.getImageY());
        int[] arrayOfInt = new int[2];
        localPlaceProviderObject.parentView.getLocationInWindow(arrayOfInt);
        j = arrayOfInt[1] - (localPlaceProviderObject.viewY + paramPageBlock.top) + localPlaceProviderObject.clipTopAddition;
        i = j;
        if (j < 0)
          i = 0;
        j = localPlaceProviderObject.viewY;
        int k = paramPageBlock.top + j + ((ViewGroup.LayoutParams)localObject).height - (arrayOfInt[1] + localPlaceProviderObject.parentView.getHeight()) + localPlaceProviderObject.clipBottomAddition;
        j = k;
        if (k < 0)
          j = 0;
        i = Math.max(i, n);
        j = Math.max(j, n);
        this.animationValues[0][0] = this.animatingImageView.getScaleX();
        this.animationValues[0][1] = this.animatingImageView.getScaleY();
        this.animationValues[0][2] = this.animatingImageView.getTranslationX();
        this.animationValues[0][3] = this.animatingImageView.getTranslationY();
        this.animationValues[0][4] = (m * localPlaceProviderObject.scale);
        this.animationValues[0][5] = (i * localPlaceProviderObject.scale);
        this.animationValues[0][6] = (j * localPlaceProviderObject.scale);
        this.animationValues[0][7] = this.animatingImageView.getRadius();
        this.animationValues[1][0] = f1;
        this.animationValues[1][1] = f1;
        this.animationValues[1][2] = f2;
        this.animationValues[1][3] = f3;
        this.animationValues[1][4] = 0;
        this.animationValues[1][5] = 0;
        this.animationValues[1][6] = 0;
        this.animationValues[1][7] = 0;
        this.photoContainerView.setVisibility(0);
        this.photoContainerBackground.setVisibility(0);
        this.animatingImageView.setAnimationProgress(0.0F);
        paramPageBlock = new AnimatorSet();
        paramPageBlock.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofInt(this.photoBackgroundDrawable, "alpha", new int[] { 0, 255 }), ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[] { 0.0F, 1.0F }) });
        this.photoAnimationEndRunnable = new Runnable()
        {
          public void run()
          {
            if (ArticleViewer.this.photoContainerView == null);
            do
            {
              return;
              if (Build.VERSION.SDK_INT >= 18)
                ArticleViewer.this.photoContainerView.setLayerType(0, null);
              ArticleViewer.access$10902(ArticleViewer.this, 0);
              ArticleViewer.access$11002(ArticleViewer.this, 0L);
              ArticleViewer.this.setImages();
              ArticleViewer.this.photoContainerView.invalidate();
              ArticleViewer.this.animatingImageView.setVisibility(8);
              if (ArticleViewer.this.showAfterAnimation == null)
                continue;
              ArticleViewer.this.showAfterAnimation.imageReceiver.setVisible(true, true);
            }
            while (ArticleViewer.this.hideAfterAnimation == null);
            ArticleViewer.this.hideAfterAnimation.imageReceiver.setVisible(false, true);
          }
        };
        paramPageBlock.setDuration(200L);
        paramPageBlock.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance().setAnimationInProgress(false);
                if (ArticleViewer.this.photoAnimationEndRunnable != null)
                {
                  ArticleViewer.this.photoAnimationEndRunnable.run();
                  ArticleViewer.access$11402(ArticleViewer.this, null);
                }
              }
            });
          }
        });
        this.photoTransitionAnimationStartTime = System.currentTimeMillis();
        AndroidUtilities.runOnUIThread(new Runnable(paramPageBlock)
        {
          public void run()
          {
            NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[] { NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded, NotificationCenter.mediaDidLoaded, NotificationCenter.dialogPhotosLoaded });
            NotificationCenter.getInstance().setAnimationInProgress(true);
            this.val$animatorSet.start();
          }
        });
        if (Build.VERSION.SDK_INT >= 18)
          this.photoContainerView.setLayerType(2, null);
        PhotoBackgroundDrawable.access$11502(this.photoBackgroundDrawable, new Runnable(localPlaceProviderObject)
        {
          public void run()
          {
            ArticleViewer.access$11602(ArticleViewer.this, false);
            this.val$object.imageReceiver.setVisible(false, true);
          }
        });
        return true;
        this.imagesArr.add(paramPageBlock);
        i = 0;
        break;
        bool = false;
        break label300;
      }
    }
  }

  public void setAnimationValue(float paramFloat)
  {
    this.animationValue = paramFloat;
    this.photoContainerView.invalidate();
  }

  public void setParentActivity(Activity paramActivity)
  {
    if (this.parentActivity == paramActivity)
      return;
    this.parentActivity = paramActivity;
    this.backgroundPaint = new Paint();
    this.backgroundPaint.setColor(-1);
    this.layerShadowDrawable = paramActivity.getResources().getDrawable(2130837884);
    this.slideDotDrawable = paramActivity.getResources().getDrawable(2130838066);
    this.slideDotBigDrawable = paramActivity.getResources().getDrawable(2130838065);
    this.scrimPaint = new Paint();
    this.windowView = new WindowView(paramActivity);
    this.windowView.setWillNotDraw(false);
    this.windowView.setClipChildren(true);
    this.windowView.setFocusable(false);
    this.containerView = new FrameLayout(paramActivity);
    this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
    this.containerView.setFitsSystemWindows(true);
    if (Build.VERSION.SDK_INT >= 21)
      this.containerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
      {
        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View paramView, WindowInsets paramWindowInsets)
        {
          paramView = (WindowInsets)ArticleViewer.this.lastInsets;
          ArticleViewer.access$602(ArticleViewer.this, paramWindowInsets);
          if ((paramView == null) || (!paramView.toString().equals(paramWindowInsets.toString())))
            ArticleViewer.this.windowView.requestLayout();
          return paramWindowInsets.consumeSystemWindowInsets();
        }
      });
    this.containerView.setSystemUiVisibility(1028);
    this.photoContainerBackground = new View(paramActivity);
    this.photoContainerBackground.setVisibility(4);
    this.photoContainerBackground.setBackgroundDrawable(this.photoBackgroundDrawable);
    this.windowView.addView(this.photoContainerBackground, LayoutHelper.createFrame(-1, -1, 51));
    this.animatingImageView = new ClippingImageView(paramActivity);
    this.animatingImageView.setAnimationValues(this.animationValues);
    this.animatingImageView.setVisibility(8);
    this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0F));
    this.photoContainerView = new FrameLayoutDrawer(paramActivity)
    {
      protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        paramInt2 = paramInt4 - paramInt2 - ArticleViewer.this.captionTextView.getMeasuredHeight();
        paramInt1 = paramInt2;
        if (ArticleViewer.this.bottomLayout.getVisibility() == 0)
          paramInt1 = paramInt2 - ArticleViewer.this.bottomLayout.getMeasuredHeight();
        ArticleViewer.this.captionTextView.layout(0, paramInt1, ArticleViewer.this.captionTextView.getMeasuredWidth(), ArticleViewer.this.captionTextView.getMeasuredHeight() + paramInt1);
      }
    };
    this.photoContainerView.setVisibility(4);
    this.photoContainerView.setWillNotDraw(false);
    this.windowView.addView(this.photoContainerView, LayoutHelper.createFrame(-1, -1, 51));
    this.fullscreenVideoContainer = new FrameLayout(paramActivity);
    this.fullscreenVideoContainer.setBackgroundColor(-16777216);
    this.fullscreenVideoContainer.setVisibility(4);
    this.windowView.addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0F));
    this.fullscreenAspectRatioView = new AspectRatioFrameLayout(paramActivity);
    this.fullscreenAspectRatioView.setVisibility(8);
    this.fullscreenVideoContainer.addView(this.fullscreenAspectRatioView, LayoutHelper.createFrame(-1, -1, 17));
    this.fullscreenTextureView = new TextureView(paramActivity);
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.barBackground = new View(paramActivity);
      this.barBackground.setBackgroundColor(-16777216);
      this.windowView.addView(this.barBackground);
    }
    this.listView = new RecyclerListView(paramActivity);
    Object localObject1 = this.listView;
    Object localObject2 = new LinearLayoutManager(this.parentActivity, 1, false);
    this.layoutManager = ((LinearLayoutManager)localObject2);
    ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
    localObject1 = this.listView;
    localObject2 = new WebpageAdapter(this.parentActivity);
    this.adapter = ((WebpageAdapter)localObject2);
    ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
    this.listView.setClipToPadding(false);
    this.listView.setPadding(0, AndroidUtilities.dp(56.0F), 0, 0);
    this.listView.setTopGlowOffset(AndroidUtilities.dp(56.0F));
    this.listView.setGlowColor(-657673);
    this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
    {
      public boolean onItemClick(View paramView, int paramInt)
      {
        return false;
      }
    });
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        if ((paramInt != ArticleViewer.this.blocks.size()) || (ArticleViewer.this.currentPage == null) || (ArticleViewer.this.previewsReqId != 0))
          return;
        paramView = MessagesController.getInstance().getUser("previews");
        if (paramView != null)
        {
          ArticleViewer.this.openPreviewsChat(paramView, ArticleViewer.this.currentPage.id);
          return;
        }
        long l = ArticleViewer.this.currentPage.id;
        ArticleViewer.this.showProgressView(true);
        paramView = new TLRPC.TL_contacts_resolveUsername();
        paramView.username = "previews";
        ArticleViewer.access$3702(ArticleViewer.this, ConnectionsManager.getInstance().sendRequest(paramView, new RequestDelegate(l)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
            {
              public void run()
              {
                if (ArticleViewer.this.previewsReqId == 0);
                TLRPC.TL_contacts_resolvedPeer localTL_contacts_resolvedPeer;
                do
                {
                  do
                  {
                    return;
                    ArticleViewer.access$3702(ArticleViewer.this, 0);
                    ArticleViewer.this.showProgressView(false);
                  }
                  while (this.val$response == null);
                  localTL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer)this.val$response;
                  MessagesController.getInstance().putUsers(localTL_contacts_resolvedPeer.users, false);
                  MessagesStorage.getInstance().putUsersAndChats(localTL_contacts_resolvedPeer.users, localTL_contacts_resolvedPeer.chats, false, true);
                }
                while (localTL_contacts_resolvedPeer.users.isEmpty());
                ArticleViewer.this.openPreviewsChat((TLRPC.User)localTL_contacts_resolvedPeer.users.get(0), ArticleViewer.5.1.this.val$pageId);
              }
            });
          }
        }));
      }
    });
    this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
      {
        if (ArticleViewer.this.listView.getChildCount() == 0)
          return;
        ArticleViewer.this.checkScroll(paramInt2);
      }
    });
    this.headerView = new FrameLayout(paramActivity);
    this.headerView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        return true;
      }
    });
    this.headerView.setBackgroundColor(-16777216);
    this.containerView.addView(this.headerView, LayoutHelper.createFrame(-1, 56.0F));
    this.backButton = new ImageView(paramActivity);
    this.backButton.setScaleType(ImageView.ScaleType.CENTER);
    this.backDrawable = new BackDrawable(false);
    this.backDrawable.setAnimationTime(200.0F);
    this.backDrawable.setColor(-5000269);
    this.backDrawable.setRotated(false);
    this.backButton.setImageDrawable(this.backDrawable);
    this.backButton.setBackgroundDrawable(Theme.createSelectorDrawable(1090519039));
    this.headerView.addView(this.backButton, LayoutHelper.createFrame(54, 56.0F));
    this.backButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        ArticleViewer.this.close(true, true);
      }
    });
    this.shareContainer = new FrameLayout(paramActivity);
    this.headerView.addView(this.shareContainer, LayoutHelper.createFrame(48, 56, 53));
    this.shareContainer.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        if ((ArticleViewer.this.currentPage == null) || (ArticleViewer.this.parentActivity == null))
          return;
        ArticleViewer.this.showDialog(new ShareAlert(ArticleViewer.this.parentActivity, null, ArticleViewer.this.currentPage.url, false, ArticleViewer.this.currentPage.url, true));
        ArticleViewer.this.hideActionBar();
      }
    });
    this.shareButton = new ImageView(paramActivity);
    this.shareButton.setScaleType(ImageView.ScaleType.CENTER);
    this.shareButton.setImageResource(2130837836);
    this.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(1090519039));
    this.shareContainer.addView(this.shareButton, LayoutHelper.createFrame(48, 56.0F));
    this.progressView = new ContextProgressView(paramActivity, 2);
    this.progressView.setVisibility(8);
    this.shareContainer.addView(this.progressView, LayoutHelper.createFrame(48, 56.0F));
    this.windowLayoutParams = new WindowManager.LayoutParams();
    this.windowLayoutParams.height = -1;
    this.windowLayoutParams.format = -3;
    this.windowLayoutParams.width = -1;
    this.windowLayoutParams.gravity = 51;
    this.windowLayoutParams.type = 99;
    if (Build.VERSION.SDK_INT >= 21);
    for (this.windowLayoutParams.flags = -2147417848; ; this.windowLayoutParams.flags = 8)
    {
      if (progressDrawables == null)
      {
        progressDrawables = new Drawable[4];
        progressDrawables[0] = this.parentActivity.getResources().getDrawable(2130837672);
        progressDrawables[1] = this.parentActivity.getResources().getDrawable(2130837661);
        progressDrawables[2] = this.parentActivity.getResources().getDrawable(2130837897);
        progressDrawables[3] = this.parentActivity.getResources().getDrawable(2130838029);
      }
      this.scroller = new Scroller(paramActivity);
      this.blackPaint.setColor(-16777216);
      this.actionBar = new ActionBar(paramActivity);
      this.actionBar.setBackgroundColor(2130706432);
      this.actionBar.setOccupyStatusBar(false);
      this.actionBar.setTitleColor(-1);
      this.actionBar.setItemsBackgroundColor(1090519039, false);
      this.actionBar.setBackButtonImage(2130837732);
      this.actionBar.setTitle(LocaleController.formatString("Of", 2131166154, new Object[] { Integer.valueOf(1), Integer.valueOf(1) }));
      this.photoContainerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0F));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public boolean canOpenMenu()
        {
          File localFile = ArticleViewer.this.getMediaFile(ArticleViewer.this.currentIndex);
          return (localFile != null) && (localFile.exists());
        }

        public void onItemClick(int paramInt)
        {
          int i = 1;
          if (paramInt == -1)
            ArticleViewer.this.closePhoto(true);
          do
          {
            return;
            if (paramInt == 1)
            {
              if ((Build.VERSION.SDK_INT >= 23) && (ArticleViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
              {
                ArticleViewer.this.parentActivity.requestPermissions(new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 4);
                return;
              }
              Object localObject = ArticleViewer.this.getMediaFile(ArticleViewer.this.currentIndex);
              if ((localObject != null) && (((File)localObject).exists()))
              {
                localObject = ((File)localObject).toString();
                Activity localActivity = ArticleViewer.this.parentActivity;
                if (ArticleViewer.this.isMediaVideo(ArticleViewer.this.currentIndex));
                for (paramInt = i; ; paramInt = 0)
                {
                  MediaController.saveFile((String)localObject, localActivity, paramInt, null, null);
                  return;
                }
              }
              localObject = new AlertDialog.Builder(ArticleViewer.this.parentActivity);
              ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165319));
              ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166153), null);
              ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("PleaseDownload", 2131166288));
              ArticleViewer.this.showDialog(((AlertDialog.Builder)localObject).create());
              return;
            }
            if (paramInt != 2)
              continue;
            ArticleViewer.this.onSharePressed();
            return;
          }
          while (paramInt != 3);
          try
          {
            AndroidUtilities.openForView(ArticleViewer.this.getMedia(ArticleViewer.this.currentIndex), ArticleViewer.this.parentActivity);
            ArticleViewer.this.closePhoto(false);
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      });
      localObject1 = this.actionBar.createMenu();
      ((ActionBarMenu)localObject1).addItem(2, 2130838058);
      this.menuItem = ((ActionBarMenu)localObject1).addItem(0, 2130837738);
      this.menuItem.setLayoutInScreen(true);
      this.menuItem.addSubItem(3, LocaleController.getString("OpenInExternalApp", 2131166167));
      this.menuItem.addSubItem(1, LocaleController.getString("SaveToGallery", 2131166375));
      this.bottomLayout = new FrameLayout(this.parentActivity);
      this.bottomLayout.setBackgroundColor(2130706432);
      this.photoContainerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
      this.captionTextViewOld = new TextView(paramActivity);
      this.captionTextViewOld.setMaxLines(10);
      this.captionTextViewOld.setBackgroundColor(2130706432);
      this.captionTextViewOld.setPadding(AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F));
      this.captionTextViewOld.setLinkTextColor(-1);
      this.captionTextViewOld.setTextColor(-1);
      this.captionTextViewOld.setGravity(19);
      this.captionTextViewOld.setTextSize(1, 16.0F);
      this.captionTextViewOld.setVisibility(4);
      this.photoContainerView.addView(this.captionTextViewOld, LayoutHelper.createFrame(-1, -2, 83));
      localObject1 = new TextView(paramActivity);
      this.captionTextViewNew = ((TextView)localObject1);
      this.captionTextView = ((TextView)localObject1);
      this.captionTextViewNew.setMaxLines(10);
      this.captionTextViewNew.setBackgroundColor(2130706432);
      this.captionTextViewNew.setPadding(AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(20.0F), AndroidUtilities.dp(8.0F));
      this.captionTextViewNew.setLinkTextColor(-1);
      this.captionTextViewNew.setTextColor(-1);
      this.captionTextViewNew.setGravity(19);
      this.captionTextViewNew.setTextSize(1, 16.0F);
      this.captionTextViewNew.setVisibility(4);
      this.photoContainerView.addView(this.captionTextViewNew, LayoutHelper.createFrame(-1, -2, 83));
      this.radialProgressViews[0] = new RadialProgressView(paramActivity, this.photoContainerView);
      this.radialProgressViews[0].setBackgroundState(0, false);
      this.radialProgressViews[1] = new RadialProgressView(paramActivity, this.photoContainerView);
      this.radialProgressViews[1].setBackgroundState(0, false);
      this.radialProgressViews[2] = new RadialProgressView(paramActivity, this.photoContainerView);
      this.radialProgressViews[2].setBackgroundState(0, false);
      this.videoPlayerSeekbar = new SeekBar(paramActivity);
      this.videoPlayerSeekbar.setColors(1728053247, -1, -1);
      this.videoPlayerSeekbar.setDelegate(new SeekBar.SeekBarDelegate()
      {
        public void onSeekBarDrag(float paramFloat)
        {
          if (ArticleViewer.this.videoPlayer != null)
            ArticleViewer.this.videoPlayer.seekTo((int)((float)ArticleViewer.this.videoPlayer.getDuration() * paramFloat));
        }
      });
      this.videoPlayerControlFrameLayout = new FrameLayout(paramActivity)
      {
        protected void onDraw(Canvas paramCanvas)
        {
          paramCanvas.save();
          paramCanvas.translate(AndroidUtilities.dp(48.0F), 0.0F);
          ArticleViewer.this.videoPlayerSeekbar.draw(paramCanvas);
          paramCanvas.restore();
        }

        protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
        {
          super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
          float f = 0.0F;
          if (ArticleViewer.this.videoPlayer != null)
            f = (float)ArticleViewer.this.videoPlayer.getCurrentPosition() / (float)ArticleViewer.this.videoPlayer.getDuration();
          ArticleViewer.this.videoPlayerSeekbar.setProgress(f);
        }

        protected void onMeasure(int paramInt1, int paramInt2)
        {
          long l2 = 0L;
          super.onMeasure(paramInt1, paramInt2);
          long l1 = l2;
          if (ArticleViewer.this.videoPlayer != null)
          {
            l1 = ArticleViewer.this.videoPlayer.getDuration();
            if (l1 != -9223372036854775807L)
              break label149;
            l1 = l2;
          }
          label149: 
          while (true)
          {
            l1 /= 1000L;
            paramInt1 = (int)Math.ceil(ArticleViewer.this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[] { Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L), Long.valueOf(l1 / 60L), Long.valueOf(l1 % 60L) })));
            ArticleViewer.this.videoPlayerSeekbar.setSize(getMeasuredWidth() - AndroidUtilities.dp(64.0F) - paramInt1, getMeasuredHeight());
            return;
          }
        }

        public boolean onTouchEvent(MotionEvent paramMotionEvent)
        {
          int i = (int)paramMotionEvent.getX();
          i = (int)paramMotionEvent.getY();
          if (ArticleViewer.this.videoPlayerSeekbar.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX() - AndroidUtilities.dp(48.0F), paramMotionEvent.getY()))
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
      this.videoPlayButton = new ImageView(paramActivity);
      this.videoPlayButton.setScaleType(ImageView.ScaleType.CENTER);
      this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48, 48, 51));
      this.videoPlayButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (ArticleViewer.this.videoPlayer != null)
          {
            if (ArticleViewer.this.isPlaying)
              ArticleViewer.this.videoPlayer.pause();
          }
          else
            return;
          ArticleViewer.this.videoPlayer.play();
        }
      });
      this.videoPlayerTime = new TextView(paramActivity);
      this.videoPlayerTime.setTextColor(-1);
      this.videoPlayerTime.setGravity(16);
      this.videoPlayerTime.setTextSize(1, 13.0F);
      this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -1.0F, 53, 0.0F, 0.0F, 8.0F, 0.0F));
      this.gestureDetector = new GestureDetector(paramActivity, this);
      this.gestureDetector.setOnDoubleTapListener(this);
      paramActivity = new ImageReceiver.ImageReceiverDelegate()
      {
        public void didSetImage(ImageReceiver paramImageReceiver, boolean paramBoolean1, boolean paramBoolean2)
        {
          if ((paramImageReceiver == ArticleViewer.this.centerImage) && (paramBoolean1) && (ArticleViewer.this.scaleToFill()))
          {
            if (!ArticleViewer.this.wasLayout)
              ArticleViewer.access$5302(ArticleViewer.this, true);
          }
          else
            return;
          ArticleViewer.this.setScaleToFill();
        }
      };
      this.centerImage.setParentView(this.photoContainerView);
      this.centerImage.setCrossfadeAlpha(2);
      this.centerImage.setInvalidateAll(true);
      this.centerImage.setDelegate(paramActivity);
      this.leftImage.setParentView(this.photoContainerView);
      this.leftImage.setCrossfadeAlpha(2);
      this.leftImage.setInvalidateAll(true);
      this.leftImage.setDelegate(paramActivity);
      this.rightImage.setParentView(this.photoContainerView);
      this.rightImage.setCrossfadeAlpha(2);
      this.rightImage.setInvalidateAll(true);
      this.rightImage.setDelegate(paramActivity);
      return;
    }
  }

  public void showDialog(Dialog paramDialog)
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
        this.visibleDialog = paramDialog;
        this.visibleDialog.setCanceledOnTouchOutside(true);
        this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
          public void onDismiss(DialogInterface paramDialogInterface)
          {
            ArticleViewer.this.showActionBar(120);
            ArticleViewer.access$6402(ArticleViewer.this, null);
          }
        });
        paramDialog.show();
        return;
      }
      catch (Exception paramDialog)
      {
        FileLog.e(paramDialog);
        return;
      }
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  protected void startCheckLongPress()
  {
    if (this.checkingForLongPress)
      return;
    this.checkingForLongPress = true;
    if (this.pendingCheckForTap == null)
      this.pendingCheckForTap = new CheckForTap(null);
    this.windowView.postDelayed(this.pendingCheckForTap, ViewConfiguration.getTapTimeout());
  }

  public void uncollapse()
  {
    if ((this.parentActivity == null) || (!this.isVisible) || (checkAnimation()))
      return;
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.containerView, "translationX", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.containerView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.listView, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.listView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.headerView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.backButton, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.backButton, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.backButton, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.shareContainer, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.shareContainer, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.shareContainer, "scaleY", new float[] { 1.0F }) });
    this.collapsed = false;
    this.animationInProgress = 2;
    this.animationEndRunnable = new Runnable()
    {
      public void run()
      {
        if (ArticleViewer.this.containerView == null)
          return;
        if (Build.VERSION.SDK_INT >= 18)
          ArticleViewer.this.containerView.setLayerType(0, null);
        ArticleViewer.access$5702(ArticleViewer.this, 0);
      }
    };
    localAnimatorSet.setDuration(250L);
    localAnimatorSet.setInterpolator(new DecelerateInterpolator());
    localAnimatorSet.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        if (ArticleViewer.this.animationEndRunnable != null)
        {
          ArticleViewer.this.animationEndRunnable.run();
          ArticleViewer.access$5802(ArticleViewer.this, null);
        }
      }
    });
    this.transitionAnimationStartTime = System.currentTimeMillis();
    if (Build.VERSION.SDK_INT >= 18)
      this.containerView.setLayerType(2, null);
    this.backDrawable.setRotation(0.0F, true);
    localAnimatorSet.start();
  }

  private class BlockAuthorDateCell extends View
  {
    private TLRPC.TL_pageBlockAuthorDate currentBlock;
    private int lastCreatedWidth;
    private StaticLayout textLayout;
    private int textX = AndroidUtilities.dp(18.0F);
    private int textY = AndroidUtilities.dp(8.0F);

    public BlockAuthorDateCell(Context arg2)
    {
      super();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
        return;
      while (this.textLayout == null);
      paramCanvas.save();
      paramCanvas.translate(this.textX, this.textY);
      ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
      this.textLayout.draw(paramCanvas);
      paramCanvas.restore();
    }

    // ERROR //
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      // Byte code:
      //   0: iload_1
      //   1: invokestatic 76	android/view/View$MeasureSpec:getSize	(I)I
      //   4: istore_2
      //   5: aload_0
      //   6: getfield 41	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:currentBlock	Lorg/vidogram/tgnet/TLRPC$TL_pageBlockAuthorDate;
      //   9: ifnull +350 -> 359
      //   12: aload_0
      //   13: getfield 78	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:lastCreatedWidth	I
      //   16: iload_2
      //   17: if_icmpeq +356 -> 373
      //   20: aload_0
      //   21: getfield 21	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:this$0	Lorg/vidogram/ui/ArticleViewer;
      //   24: aload_0
      //   25: getfield 41	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:currentBlock	Lorg/vidogram/tgnet/TLRPC$TL_pageBlockAuthorDate;
      //   28: getfield 84	org/vidogram/tgnet/TLRPC$TL_pageBlockAuthorDate:author	Lorg/vidogram/tgnet/TLRPC$RichText;
      //   31: aload_0
      //   32: getfield 41	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:currentBlock	Lorg/vidogram/tgnet/TLRPC$TL_pageBlockAuthorDate;
      //   35: getfield 84	org/vidogram/tgnet/TLRPC$TL_pageBlockAuthorDate:author	Lorg/vidogram/tgnet/TLRPC$RichText;
      //   38: aload_0
      //   39: getfield 41	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:currentBlock	Lorg/vidogram/tgnet/TLRPC$TL_pageBlockAuthorDate;
      //   42: invokestatic 88	org/vidogram/ui/ArticleViewer:access$9200	(Lorg/vidogram/ui/ArticleViewer;Lorg/vidogram/tgnet/TLRPC$RichText;Lorg/vidogram/tgnet/TLRPC$RichText;Lorg/vidogram/tgnet/TLRPC$PageBlock;)Ljava/lang/CharSequence;
      //   45: astore 7
      //   47: aload 7
      //   49: instanceof 90
      //   52: ifeq +181 -> 233
      //   55: aload 7
      //   57: checkcast 90	android/text/Spannable
      //   60: astore 5
      //   62: aload 5
      //   64: iconst_0
      //   65: aload 7
      //   67: invokeinterface 95 1 0
      //   72: ldc 97
      //   74: invokeinterface 101 4 0
      //   79: checkcast 103	[Landroid/text/style/MetricAffectingSpan;
      //   82: astore 6
      //   84: aload_0
      //   85: getfield 41	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:currentBlock	Lorg/vidogram/tgnet/TLRPC$TL_pageBlockAuthorDate;
      //   88: getfield 106	org/vidogram/tgnet/TLRPC$TL_pageBlockAuthorDate:published_date	I
      //   91: ifeq +151 -> 242
      //   94: aload 7
      //   96: invokestatic 112	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
      //   99: ifne +143 -> 242
      //   102: ldc 114
      //   104: ldc 115
      //   106: iconst_2
      //   107: anewarray 117	java/lang/Object
      //   110: dup
      //   111: iconst_0
      //   112: invokestatic 123	org/vidogram/messenger/LocaleController:getInstance	()Lorg/vidogram/messenger/LocaleController;
      //   115: getfield 127	org/vidogram/messenger/LocaleController:chatFullDate	Lorg/vidogram/messenger/time/FastDateFormat;
      //   118: aload_0
      //   119: getfield 41	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:currentBlock	Lorg/vidogram/tgnet/TLRPC$TL_pageBlockAuthorDate;
      //   122: getfield 106	org/vidogram/tgnet/TLRPC$TL_pageBlockAuthorDate:published_date	I
      //   125: i2l
      //   126: ldc2_w 128
      //   129: lmul
      //   130: invokevirtual 135	org/vidogram/messenger/time/FastDateFormat:format	(J)Ljava/lang/String;
      //   133: aastore
      //   134: dup
      //   135: iconst_1
      //   136: aload 7
      //   138: aastore
      //   139: invokestatic 139	org/vidogram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
      //   142: astore 4
      //   144: aload 6
      //   146: ifnull +239 -> 385
      //   149: aload 6
      //   151: arraylength
      //   152: ifle +233 -> 385
      //   155: aload 4
      //   157: aload 7
      //   159: invokestatic 143	android/text/TextUtils:indexOf	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)I
      //   162: istore_3
      //   163: iload_3
      //   164: iconst_m1
      //   165: if_icmpeq +220 -> 385
      //   168: invokestatic 148	android/text/Spannable$Factory:getInstance	()Landroid/text/Spannable$Factory;
      //   171: aload 7
      //   173: invokevirtual 152	android/text/Spannable$Factory:newSpannable	(Ljava/lang/CharSequence;)Landroid/text/Spannable;
      //   176: astore 7
      //   178: iconst_0
      //   179: istore_1
      //   180: iload_1
      //   181: aload 6
      //   183: arraylength
      //   184: if_icmpge +194 -> 378
      //   187: aload 7
      //   189: aload 6
      //   191: iload_1
      //   192: aaload
      //   193: aload 5
      //   195: aload 6
      //   197: iload_1
      //   198: aaload
      //   199: invokeinterface 156 2 0
      //   204: iload_3
      //   205: iadd
      //   206: aload 5
      //   208: aload 6
      //   210: iload_1
      //   211: aaload
      //   212: invokeinterface 159 2 0
      //   217: iload_3
      //   218: iadd
      //   219: bipush 33
      //   221: invokeinterface 163 5 0
      //   226: iload_1
      //   227: iconst_1
      //   228: iadd
      //   229: istore_1
      //   230: goto -50 -> 180
      //   233: aconst_null
      //   234: astore 5
      //   236: aconst_null
      //   237: astore 6
      //   239: goto -155 -> 84
      //   242: aload 7
      //   244: invokestatic 112	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
      //   247: ifne +24 -> 271
      //   250: ldc 165
      //   252: ldc 166
      //   254: iconst_1
      //   255: anewarray 117	java/lang/Object
      //   258: dup
      //   259: iconst_0
      //   260: aload 7
      //   262: aastore
      //   263: invokestatic 139	org/vidogram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
      //   266: astore 4
      //   268: goto -124 -> 144
      //   271: invokestatic 123	org/vidogram/messenger/LocaleController:getInstance	()Lorg/vidogram/messenger/LocaleController;
      //   274: getfield 127	org/vidogram/messenger/LocaleController:chatFullDate	Lorg/vidogram/messenger/time/FastDateFormat;
      //   277: aload_0
      //   278: getfield 41	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:currentBlock	Lorg/vidogram/tgnet/TLRPC$TL_pageBlockAuthorDate;
      //   281: getfield 106	org/vidogram/tgnet/TLRPC$TL_pageBlockAuthorDate:published_date	I
      //   284: i2l
      //   285: ldc2_w 128
      //   288: lmul
      //   289: invokevirtual 135	org/vidogram/messenger/time/FastDateFormat:format	(J)Ljava/lang/String;
      //   292: astore 4
      //   294: goto -150 -> 144
      //   297: astore 5
      //   299: aload 5
      //   301: invokestatic 172	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   304: aload_0
      //   305: aload_0
      //   306: getfield 21	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:this$0	Lorg/vidogram/ui/ArticleViewer;
      //   309: aload 4
      //   311: aconst_null
      //   312: iload_2
      //   313: ldc 173
      //   315: invokestatic 31	org/vidogram/messenger/AndroidUtilities:dp	(F)I
      //   318: isub
      //   319: aload_0
      //   320: getfield 41	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:currentBlock	Lorg/vidogram/tgnet/TLRPC$TL_pageBlockAuthorDate;
      //   323: invokestatic 177	org/vidogram/ui/ArticleViewer:access$6700	(Lorg/vidogram/ui/ArticleViewer;Ljava/lang/CharSequence;Lorg/vidogram/tgnet/TLRPC$RichText;ILorg/vidogram/tgnet/TLRPC$PageBlock;)Landroid/text/StaticLayout;
      //   326: putfield 43	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:textLayout	Landroid/text/StaticLayout;
      //   329: aload_0
      //   330: getfield 43	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:textLayout	Landroid/text/StaticLayout;
      //   333: ifnull +40 -> 373
      //   336: ldc 178
      //   338: invokestatic 31	org/vidogram/messenger/AndroidUtilities:dp	(F)I
      //   341: aload_0
      //   342: getfield 43	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:textLayout	Landroid/text/StaticLayout;
      //   345: invokevirtual 181	android/text/StaticLayout:getHeight	()I
      //   348: iadd
      //   349: iconst_0
      //   350: iadd
      //   351: istore_1
      //   352: aload_0
      //   353: iload_2
      //   354: iload_1
      //   355: invokevirtual 184	org/vidogram/ui/ArticleViewer$BlockAuthorDateCell:setMeasuredDimension	(II)V
      //   358: return
      //   359: iconst_1
      //   360: istore_1
      //   361: goto -9 -> 352
      //   364: astore 5
      //   366: aload 7
      //   368: astore 4
      //   370: goto -71 -> 299
      //   373: iconst_0
      //   374: istore_1
      //   375: goto -23 -> 352
      //   378: aload 7
      //   380: astore 4
      //   382: goto -78 -> 304
      //   385: goto -81 -> 304
      //
      // Exception table:
      //   from	to	target	type
      //   149	163	297	java/lang/Exception
      //   168	178	297	java/lang/Exception
      //   180	226	364	java/lang/Exception
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setBlock(TLRPC.TL_pageBlockAuthorDate paramTL_pageBlockAuthorDate)
    {
      this.currentBlock = paramTL_pageBlockAuthorDate;
      this.lastCreatedWidth = 0;
      requestLayout();
    }
  }

  private class BlockBlockquoteCell extends View
  {
    private TLRPC.TL_pageBlockBlockquote currentBlock;
    private int lastCreatedWidth;
    private StaticLayout textLayout;
    private StaticLayout textLayout2;
    private int textX = AndroidUtilities.dp(32.0F);
    private int textY = AndroidUtilities.dp(8.0F);
    private int textY2;

    public BlockBlockquoteCell(Context arg2)
    {
      super();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null)
        return;
      if (this.textLayout != null)
      {
        paramCanvas.save();
        paramCanvas.translate(this.textX, this.textY);
        ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
        this.textLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.textLayout2 != null)
      {
        paramCanvas.save();
        paramCanvas.translate(this.textX, this.textY2);
        ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout2);
        this.textLayout2.draw(paramCanvas);
        paramCanvas.restore();
      }
      paramCanvas.drawRect(AndroidUtilities.dp(18.0F), AndroidUtilities.dp(6.0F), AndroidUtilities.dp(20.0F), getMeasuredHeight() - AndroidUtilities.dp(6.0F), ArticleViewer.quoteLinePaint);
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      paramInt1 = 0;
      paramInt2 = 0;
      if (this.currentBlock != null)
        if (this.lastCreatedWidth != i)
        {
          this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(50.0F), this.currentBlock);
          paramInt1 = paramInt2;
          if (this.textLayout != null)
            paramInt1 = 0 + (AndroidUtilities.dp(8.0F) + this.textLayout.getHeight());
          this.textLayout2 = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, i - AndroidUtilities.dp(50.0F), this.currentBlock);
          paramInt2 = paramInt1;
          if (this.textLayout2 != null)
          {
            this.textY2 = (AndroidUtilities.dp(2.0F) + paramInt1);
            paramInt2 = paramInt1 + (AndroidUtilities.dp(8.0F) + this.textLayout2.getHeight());
          }
          paramInt1 = paramInt2;
          if (paramInt2 == 0);
        }
      for (paramInt1 = paramInt2 + AndroidUtilities.dp(8.0F); ; paramInt1 = 1)
      {
        setMeasuredDimension(i, paramInt1);
        return;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout2, this.textX, this.textY2)) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setBlock(TLRPC.TL_pageBlockBlockquote paramTL_pageBlockBlockquote)
    {
      this.currentBlock = paramTL_pageBlockBlockquote;
      this.lastCreatedWidth = 0;
      requestLayout();
    }
  }

  private class BlockCollageCell extends FrameLayout
  {
    private RecyclerView.Adapter adapter;
    private TLRPC.TL_pageBlockCollage currentBlock;
    private GridLayoutManager gridLayoutManager;
    private boolean inLayout;
    private RecyclerListView innerListView;
    private int lastCreatedWidth;
    private int listX;
    private StaticLayout textLayout;
    private int textX;
    private int textY;

    public BlockCollageCell(Context arg2)
    {
      super();
      this.innerListView = new RecyclerListView((Context)localObject, ArticleViewer.this)
      {
        public void requestLayout()
        {
          if (ArticleViewer.BlockCollageCell.this.inLayout)
            return;
          super.requestLayout();
        }
      };
      this.innerListView.setGlowColor(-657673);
      this.innerListView.addItemDecoration(new RecyclerView.ItemDecoration(ArticleViewer.this)
      {
        public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
        {
          paramRect.left = 0;
          paramRect.top = 0;
          int i = AndroidUtilities.dp(2.0F);
          paramRect.right = i;
          paramRect.bottom = i;
        }
      });
      RecyclerListView localRecyclerListView = this.innerListView;
      Object localObject = new GridLayoutManager((Context)localObject, 3);
      this.gridLayoutManager = ((GridLayoutManager)localObject);
      localRecyclerListView.setLayoutManager((RecyclerView.LayoutManager)localObject);
      localObject = this.innerListView;
      this$1 = new RecyclerView.Adapter(ArticleViewer.this)
      {
        public int getItemCount()
        {
          if (ArticleViewer.BlockCollageCell.this.currentBlock == null)
            return 0;
          return ArticleViewer.BlockCollageCell.this.currentBlock.items.size();
        }

        public int getItemViewType(int paramInt)
        {
          if (((TLRPC.PageBlock)ArticleViewer.BlockCollageCell.this.currentBlock.items.get(paramInt) instanceof TLRPC.TL_pageBlockPhoto))
            return 0;
          return 1;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
        {
          switch (paramViewHolder.getItemViewType())
          {
          default:
            ((ArticleViewer.BlockVideoCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockVideo)ArticleViewer.BlockCollageCell.this.currentBlock.items.get(paramInt), true, true);
            return;
          case 0:
          }
          ((ArticleViewer.BlockPhotoCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockPhoto)ArticleViewer.BlockCollageCell.this.currentBlock.items.get(paramInt), true, true);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
        {
          switch (paramInt)
          {
          default:
          case 0:
          }
          for (paramViewGroup = new ArticleViewer.BlockVideoCell(ArticleViewer.this, ArticleViewer.BlockCollageCell.this.getContext(), 2); ; paramViewGroup = new ArticleViewer.BlockPhotoCell(ArticleViewer.this, ArticleViewer.BlockCollageCell.this.getContext(), 2))
            return new RecyclerListView.Holder(paramViewGroup);
        }
      };
      this.adapter = ArticleViewer.this;
      ((RecyclerListView)localObject).setAdapter(ArticleViewer.this);
      addView(this.innerListView, LayoutHelper.createFrame(-1, -2.0F));
      setWillNotDraw(false);
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
      {
        return;
        if (this.textLayout == null)
          continue;
        paramCanvas.save();
        paramCanvas.translate(this.textX, this.textY);
        ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
        this.textLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      while (this.currentBlock.level <= 0);
      float f1 = AndroidUtilities.dp(18.0F);
      float f2 = AndroidUtilities.dp(20.0F);
      int j = getMeasuredHeight();
      if (this.currentBlock.bottom);
      for (int i = AndroidUtilities.dp(6.0F); ; i = 0)
      {
        paramCanvas.drawRect(f1, 0.0F, f2, j - i, ArticleViewer.quoteLinePaint);
        return;
      }
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.innerListView.layout(this.listX, 0, this.listX + this.innerListView.getMeasuredWidth(), this.innerListView.getMeasuredHeight());
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      paramInt2 = 1;
      this.inLayout = true;
      int i = View.MeasureSpec.getSize(paramInt1);
      if (this.currentBlock != null)
      {
        if (this.currentBlock.level <= 0)
          break label270;
        paramInt1 = AndroidUtilities.dp(this.currentBlock.level * 14) + AndroidUtilities.dp(18.0F);
        this.listX = paramInt1;
        this.textX = paramInt1;
        paramInt1 = i - (this.listX + AndroidUtilities.dp(18.0F));
        paramInt2 = paramInt1;
        int j = paramInt2 / AndroidUtilities.dp(100.0F);
        int k = (int)Math.ceil(this.currentBlock.items.size() / j);
        paramInt2 /= j;
        this.gridLayoutManager.setSpanCount(j);
        this.innerListView.measure(View.MeasureSpec.makeMeasureSpec(j * paramInt2 + AndroidUtilities.dp(2.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2 * k, 1073741824));
        paramInt2 = paramInt2 * k - AndroidUtilities.dp(2.0F);
        if (this.lastCreatedWidth == i)
          break label297;
        this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, paramInt1, this.currentBlock);
        if (this.textLayout == null)
          break label297;
        this.textY = (AndroidUtilities.dp(8.0F) + paramInt2);
      }
      label270: label297: for (paramInt1 = AndroidUtilities.dp(8.0F) + this.textLayout.getHeight() + paramInt2; ; paramInt1 = paramInt2)
      {
        paramInt2 = paramInt1;
        if (this.currentBlock.level > 0)
        {
          paramInt2 = paramInt1;
          if (!this.currentBlock.bottom)
            paramInt2 = paramInt1 + AndroidUtilities.dp(8.0F);
        }
        setMeasuredDimension(i, paramInt2);
        this.inLayout = false;
        return;
        this.listX = 0;
        this.textX = AndroidUtilities.dp(18.0F);
        paramInt1 = i - AndroidUtilities.dp(36.0F);
        paramInt2 = i;
        break;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setBlock(TLRPC.TL_pageBlockCollage paramTL_pageBlockCollage)
    {
      this.currentBlock = paramTL_pageBlockCollage;
      this.lastCreatedWidth = 0;
      this.adapter.notifyDataSetChanged();
      requestLayout();
    }
  }

  private class BlockDividerCell extends View
  {
    private RectF rect = new RectF();

    public BlockDividerCell(Context arg2)
    {
      super();
      if (ArticleViewer.dividerPaint == null)
      {
        ArticleViewer.access$9102(new Paint());
        ArticleViewer.dividerPaint.setColor(-3288619);
      }
    }

    protected void onDraw(Canvas paramCanvas)
    {
      int i = getMeasuredWidth() / 3;
      this.rect.set(i, AndroidUtilities.dp(8.0F), i * 2, AndroidUtilities.dp(10.0F));
      paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0F), AndroidUtilities.dp(1.0F), ArticleViewer.dividerPaint);
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(18.0F));
    }
  }

  private class BlockEmbedCell extends FrameLayout
  {
    private TLRPC.TL_pageBlockEmbed currentBlock;
    private int lastCreatedWidth;
    private int listX;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    private WebPlayerView videoView;
    private TouchyWebView webView;

    @SuppressLint({"SetJavaScriptEnabled"})
    public BlockEmbedCell(Context arg2)
    {
      super();
      setWillNotDraw(false);
      this.videoView = new WebPlayerView(localContext, false, false, new WebPlayerView.WebPlayerViewDelegate(ArticleViewer.this)
      {
        public boolean checkInlinePermissons()
        {
          return false;
        }

        public ViewGroup getTextureViewContainer()
        {
          return null;
        }

        public void onInitFailed()
        {
          ArticleViewer.BlockEmbedCell.this.webView.setVisibility(0);
          ArticleViewer.BlockEmbedCell.this.videoView.setVisibility(4);
          ArticleViewer.BlockEmbedCell.this.videoView.loadVideo(null, null, null, false);
          HashMap localHashMap = new HashMap();
          localHashMap.put("Referer", "http://youtube.com");
          ArticleViewer.BlockEmbedCell.this.webView.loadUrl(ArticleViewer.BlockEmbedCell.this.currentBlock.url, localHashMap);
        }

        public void onInlineSurfaceTextureReady()
        {
        }

        public void onPlayStateChanged(WebPlayerView paramWebPlayerView, boolean paramBoolean)
        {
          if (paramBoolean)
          {
            if ((ArticleViewer.this.currentPlayingVideo != null) && (ArticleViewer.this.currentPlayingVideo != paramWebPlayerView))
              ArticleViewer.this.currentPlayingVideo.pause();
            ArticleViewer.access$7702(ArticleViewer.this, paramWebPlayerView);
            try
            {
              ArticleViewer.this.parentActivity.getWindow().addFlags(128);
              return;
            }
            catch (Exception paramWebPlayerView)
            {
              FileLog.e(paramWebPlayerView);
              return;
            }
          }
          if (ArticleViewer.this.currentPlayingVideo == paramWebPlayerView)
            ArticleViewer.access$7702(ArticleViewer.this, null);
          try
          {
            ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
            return;
          }
          catch (Exception paramWebPlayerView)
          {
            FileLog.e(paramWebPlayerView);
          }
        }

        public void onSharePressed()
        {
          if (ArticleViewer.this.parentActivity == null)
            return;
          ArticleViewer.this.showDialog(new ShareAlert(ArticleViewer.this.parentActivity, null, ArticleViewer.BlockEmbedCell.this.currentBlock.url, false, ArticleViewer.BlockEmbedCell.this.currentBlock.url, true));
        }

        public TextureView onSwitchInlineMode(View paramView, boolean paramBoolean1, float paramFloat, int paramInt, boolean paramBoolean2)
        {
          return null;
        }

        public TextureView onSwitchToFullscreen(View paramView, boolean paramBoolean1, float paramFloat, int paramInt, boolean paramBoolean2)
        {
          if (paramBoolean1)
          {
            ArticleViewer.this.fullscreenAspectRatioView.addView(ArticleViewer.this.fullscreenTextureView, LayoutHelper.createFrame(-1, -1.0F));
            ArticleViewer.this.fullscreenAspectRatioView.setVisibility(0);
            ArticleViewer.this.fullscreenAspectRatioView.setAspectRatio(paramFloat, paramInt);
            ArticleViewer.access$7602(ArticleViewer.this, ArticleViewer.BlockEmbedCell.this.videoView);
            ArticleViewer.this.fullscreenVideoContainer.addView(paramView, LayoutHelper.createFrame(-1, -1.0F));
            ArticleViewer.this.fullscreenVideoContainer.setVisibility(0);
          }
          while (true)
          {
            return ArticleViewer.this.fullscreenTextureView;
            ArticleViewer.this.fullscreenAspectRatioView.removeView(ArticleViewer.this.fullscreenTextureView);
            ArticleViewer.access$7602(ArticleViewer.this, null);
            ArticleViewer.this.fullscreenAspectRatioView.setVisibility(8);
            ArticleViewer.this.fullscreenVideoContainer.setVisibility(4);
          }
        }

        public void onVideoSizeChanged(float paramFloat, int paramInt)
        {
          ArticleViewer.this.fullscreenAspectRatioView.setAspectRatio(paramFloat, paramInt);
        }

        public void prepareToSwitchInlineMode(boolean paramBoolean1, Runnable paramRunnable, float paramFloat, boolean paramBoolean2)
        {
        }
      });
      addView(this.videoView);
      ArticleViewer.this.createdWebViews.add(this);
      this.webView = new TouchyWebView(localContext);
      this.webView.getSettings().setJavaScriptEnabled(true);
      this.webView.getSettings().setDomStorageEnabled(true);
      this.webView.getSettings().setAllowContentAccess(true);
      if (Build.VERSION.SDK_INT >= 17)
        this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
      if (Build.VERSION.SDK_INT >= 21)
      {
        this.webView.getSettings().setMixedContentMode(0);
        CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
      }
      this.webView.setWebChromeClient(new WebChromeClient(ArticleViewer.this)
      {
        public void onHideCustomView()
        {
          super.onHideCustomView();
          if (ArticleViewer.this.customView == null)
            return;
          ArticleViewer.this.fullscreenVideoContainer.setVisibility(4);
          ArticleViewer.this.fullscreenVideoContainer.removeView(ArticleViewer.this.customView);
          if ((ArticleViewer.this.customViewCallback != null) && (!ArticleViewer.this.customViewCallback.getClass().getName().contains(".chromium.")))
            ArticleViewer.this.customViewCallback.onCustomViewHidden();
          ArticleViewer.access$7902(ArticleViewer.this, null);
        }

        public void onShowCustomView(View paramView, int paramInt, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
          onShowCustomView(paramView, paramCustomViewCallback);
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
          if (ArticleViewer.this.customView != null)
          {
            paramCustomViewCallback.onCustomViewHidden();
            return;
          }
          ArticleViewer.access$7902(ArticleViewer.this, paramView);
          ArticleViewer.this.fullscreenVideoContainer.setVisibility(0);
          ArticleViewer.this.fullscreenVideoContainer.addView(paramView, LayoutHelper.createFrame(-1, -1.0F));
          ArticleViewer.access$8002(ArticleViewer.this, paramCustomViewCallback);
        }
      });
      this.webView.setWebViewClient(new WebViewClient(ArticleViewer.this)
      {
        public void onLoadResource(WebView paramWebView, String paramString)
        {
          super.onLoadResource(paramWebView, paramString);
        }

        public void onPageFinished(WebView paramWebView, String paramString)
        {
          super.onPageFinished(paramWebView, paramString);
        }
      });
      addView(this.webView);
    }

    public void destroyWebView(boolean paramBoolean)
    {
      try
      {
        this.webView.stopLoading();
        this.webView.loadUrl("about:blank");
        if (paramBoolean)
          this.webView.destroy();
        this.currentBlock = null;
        this.videoView.destroy();
        return;
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
      }
    }

    protected void onAttachedToWindow()
    {
      super.onAttachedToWindow();
    }

    protected void onDetachedFromWindow()
    {
      super.onDetachedFromWindow();
      if (!ArticleViewer.this.isVisible)
        this.currentBlock = null;
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
      {
        return;
        if (this.textLayout == null)
          continue;
        paramCanvas.save();
        paramCanvas.translate(this.textX, this.textY);
        ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
        this.textLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      while (this.currentBlock.level <= 0);
      float f1 = AndroidUtilities.dp(18.0F);
      float f2 = AndroidUtilities.dp(20.0F);
      int j = getMeasuredHeight();
      if (this.currentBlock.bottom);
      for (int i = AndroidUtilities.dp(6.0F); ; i = 0)
      {
        paramCanvas.drawRect(f1, 0.0F, f2, j - i, ArticleViewer.quoteLinePaint);
        return;
      }
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.webView.layout(this.listX, 0, this.listX + this.webView.getMeasuredWidth(), this.webView.getMeasuredHeight());
      if (this.videoView.getParent() == this)
        this.videoView.layout(this.listX, 0, this.listX + this.videoView.getMeasuredWidth(), this.videoView.getMeasuredHeight());
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      float f;
      label82: label107: int j;
      if (this.currentBlock != null)
        if (this.currentBlock.level > 0)
        {
          paramInt1 = AndroidUtilities.dp(this.currentBlock.level * 14) + AndroidUtilities.dp(18.0F);
          this.listX = paramInt1;
          this.textX = paramInt1;
          paramInt1 = i - (this.listX + AndroidUtilities.dp(18.0F));
          paramInt2 = paramInt1;
          if (this.currentBlock.w != 0)
            break label320;
          f = 1.0F;
          if (this.currentBlock.w != 0)
            break label336;
          f *= AndroidUtilities.dp(this.currentBlock.h);
          j = (int)f;
          this.webView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824), View.MeasureSpec.makeMeasureSpec(j, 1073741824));
          if (this.videoView.getParent() == this)
            this.videoView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0F) + j, 1073741824));
          if (this.lastCreatedWidth == i)
            break label388;
          this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, paramInt1, this.currentBlock);
          if (this.textLayout == null)
            break label388;
          this.textY = (AndroidUtilities.dp(8.0F) + j);
        }
      label388: for (paramInt1 = AndroidUtilities.dp(8.0F) + this.textLayout.getHeight() + j; ; paramInt1 = j)
      {
        paramInt2 = paramInt1 + AndroidUtilities.dp(5.0F);
        if ((this.currentBlock.level > 0) && (!this.currentBlock.bottom))
          paramInt1 = paramInt2 + AndroidUtilities.dp(8.0F);
        while (true)
        {
          setMeasuredDimension(i, paramInt1);
          return;
          this.listX = 0;
          this.textX = AndroidUtilities.dp(18.0F);
          paramInt1 = i - AndroidUtilities.dp(36.0F);
          paramInt2 = i;
          break;
          label320: f = i / this.currentBlock.w;
          break label82;
          label336: f *= this.currentBlock.h;
          break label107;
          paramInt1 = paramInt2;
          if (this.currentBlock.level != 0)
            continue;
          paramInt1 = paramInt2;
          if (this.textLayout == null)
            continue;
          paramInt1 = paramInt2 + AndroidUtilities.dp(8.0F);
          continue;
          paramInt1 = 1;
        }
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setBlock(TLRPC.TL_pageBlockEmbed paramTL_pageBlockEmbed)
    {
      TLRPC.TL_pageBlockEmbed localTL_pageBlockEmbed = this.currentBlock;
      this.currentBlock = paramTL_pageBlockEmbed;
      this.lastCreatedWidth = 0;
      if (localTL_pageBlockEmbed != this.currentBlock);
      try
      {
        this.webView.loadUrl("about:blank");
      }
      catch (Exception localPhoto)
      {
        try
        {
          if (this.currentBlock.html != null)
            this.webView.loadData(this.currentBlock.html, "text/html", "UTF-8");
          while (true)
          {
            requestLayout();
            return;
            localException = localException;
            FileLog.e(localException);
            break;
            if (this.currentBlock.poster_photo_id == 0L)
              break label152;
            localPhoto = ArticleViewer.this.getPhotoWithId(this.currentBlock.poster_photo_id);
            if (!this.videoView.loadVideo(paramTL_pageBlockEmbed.url, localPhoto, null, this.currentBlock.autoplay))
              break label157;
            this.webView.setVisibility(4);
            this.videoView.setVisibility(0);
          }
        }
        catch (Exception paramTL_pageBlockEmbed)
        {
          while (true)
          {
            FileLog.e(paramTL_pageBlockEmbed);
            continue;
            label152: TLRPC.Photo localPhoto = null;
            continue;
            label157: this.webView.setVisibility(0);
            this.videoView.setVisibility(4);
            this.videoView.loadVideo(null, null, null, false);
            paramTL_pageBlockEmbed = new HashMap();
            paramTL_pageBlockEmbed.put("Referer", "http://youtube.com");
            this.webView.loadUrl(this.currentBlock.url, paramTL_pageBlockEmbed);
          }
        }
      }
    }

    public class TouchyWebView extends WebView
    {
      public TouchyWebView(Context arg2)
      {
        super();
        setFocusable(false);
      }

      public boolean onTouchEvent(MotionEvent paramMotionEvent)
      {
        if (ArticleViewer.BlockEmbedCell.this.currentBlock.allow_scrolling)
          requestDisallowInterceptTouchEvent(true);
        while (true)
        {
          return super.onTouchEvent(paramMotionEvent);
          ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
        }
      }
    }
  }

  private class BlockEmbedPostCell extends View
  {
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImageView = new ImageReceiver(this);
    private boolean avatarVisible;
    private int captionX = AndroidUtilities.dp(18.0F);
    private int captionY;
    private TLRPC.TL_pageBlockEmbedPost currentBlock;
    private StaticLayout dateLayout;
    private int dateX;
    private int lastCreatedWidth;
    private int lineHeight;
    private StaticLayout nameLayout;
    private int nameX;
    private StaticLayout textLayout;
    private int textX = AndroidUtilities.dp(32.0F);
    private int textY = AndroidUtilities.dp(56.0F);

    public BlockEmbedPostCell(Context arg2)
    {
      super();
      this.avatarImageView.setRoundRadius(AndroidUtilities.dp(20.0F));
      this.avatarImageView.setImageCoords(AndroidUtilities.dp(32.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(40.0F), AndroidUtilities.dp(40.0F));
      this.avatarDrawable = new AvatarDrawable();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      int k = 54;
      int j = 0;
      if (this.currentBlock == null)
        return;
      if (this.avatarVisible)
        this.avatarImageView.draw(paramCanvas);
      float f2;
      float f1;
      label75: label120: float f3;
      if (this.nameLayout != null)
      {
        paramCanvas.save();
        if (this.avatarVisible)
        {
          i = 54;
          f2 = AndroidUtilities.dp(i + 32);
          if (this.dateLayout == null)
            break label268;
          f1 = 10.0F;
          paramCanvas.translate(f2, AndroidUtilities.dp(f1));
          this.nameLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
      }
      else
      {
        if (this.dateLayout != null)
        {
          paramCanvas.save();
          if (!this.avatarVisible)
            break label274;
          i = k;
          paramCanvas.translate(AndroidUtilities.dp(i + 32), AndroidUtilities.dp(29.0F));
          this.dateLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
        if (this.textLayout != null)
        {
          paramCanvas.save();
          paramCanvas.translate(this.textX, this.textY);
          ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
          this.textLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
        f1 = AndroidUtilities.dp(18.0F);
        f2 = AndroidUtilities.dp(6.0F);
        f3 = AndroidUtilities.dp(20.0F);
        k = this.lineHeight;
        if (this.currentBlock.level == 0)
          break label280;
      }
      label268: label274: label280: for (int i = j; ; i = AndroidUtilities.dp(6.0F))
      {
        paramCanvas.drawRect(f1, f2, f3, k - i, ArticleViewer.quoteLinePaint);
        return;
        i = 0;
        break;
        f1 = 19.0F;
        break label75;
        i = 0;
        break label120;
      }
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      paramInt2 = 54;
      int i = View.MeasureSpec.getSize(paramInt1);
      boolean bool;
      if (this.currentBlock != null)
      {
        if (this.lastCreatedWidth == i)
          break label409;
        if (this.currentBlock.author_photo_id != 0L)
        {
          bool = true;
          this.avatarVisible = bool;
          if (bool)
          {
            localObject = ArticleViewer.this.getPhotoWithId(this.currentBlock.author_photo_id);
            if (localObject == null)
              break label380;
            bool = true;
            label73: this.avatarVisible = bool;
            if (bool)
            {
              this.avatarDrawable.setInfo(0, this.currentBlock.author, null, false);
              localObject = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo)localObject).sizes, AndroidUtilities.dp(40.0F), true);
              this.avatarImageView.setImage(((TLRPC.PhotoSize)localObject).location, String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(40), Integer.valueOf(40) }), this.avatarDrawable, 0, null, true);
            }
          }
          Object localObject = ArticleViewer.this;
          String str = this.currentBlock.author;
          if (!this.avatarVisible)
            break label386;
          paramInt1 = 54;
          label189: this.nameLayout = ((ArticleViewer)localObject).createLayoutForText(str, null, i - AndroidUtilities.dp(paramInt1 + 50), this.currentBlock);
          if (this.currentBlock.date == 0)
            break label396;
          localObject = ArticleViewer.this;
          str = LocaleController.getInstance().chatFullDate.format(this.currentBlock.date * 1000L);
          if (!this.avatarVisible)
            break label391;
          paramInt1 = paramInt2;
          label263: this.dateLayout = ((ArticleViewer)localObject).createLayoutForText(str, null, i - AndroidUtilities.dp(paramInt1 + 50), this.currentBlock);
          label289: paramInt2 = AndroidUtilities.dp(56.0F);
          paramInt1 = paramInt2;
          if (this.currentBlock.text != null)
          {
            this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(50.0F), this.currentBlock);
            paramInt1 = paramInt2;
            if (this.textLayout != null)
              paramInt1 = paramInt2 + (AndroidUtilities.dp(8.0F) + this.textLayout.getHeight());
          }
          this.lineHeight = paramInt1;
        }
      }
      while (true)
      {
        setMeasuredDimension(i, paramInt1);
        return;
        bool = false;
        break;
        label380: bool = false;
        break label73;
        label386: paramInt1 = 0;
        break label189;
        label391: paramInt1 = 0;
        break label263;
        label396: this.dateLayout = null;
        break label289;
        paramInt1 = 1;
        continue;
        label409: paramInt1 = 0;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setBlock(TLRPC.TL_pageBlockEmbedPost paramTL_pageBlockEmbedPost)
    {
      this.currentBlock = paramTL_pageBlockEmbedPost;
      this.lastCreatedWidth = 0;
      requestLayout();
    }
  }

  private class BlockFooterCell extends View
  {
    private TLRPC.TL_pageBlockFooter currentBlock;
    private int lastCreatedWidth;
    private StaticLayout textLayout;
    private int textX = AndroidUtilities.dp(18.0F);
    private int textY = AndroidUtilities.dp(8.0F);

    public BlockFooterCell(Context arg2)
    {
      super();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
      {
        return;
        if (this.textLayout == null)
          continue;
        paramCanvas.save();
        paramCanvas.translate(this.textX, this.textY);
        ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
        this.textLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      while (this.currentBlock.level <= 0);
      float f1 = AndroidUtilities.dp(18.0F);
      float f2 = AndroidUtilities.dp(20.0F);
      int j = getMeasuredHeight();
      if (this.currentBlock.bottom);
      for (int i = AndroidUtilities.dp(6.0F); ; i = 0)
      {
        paramCanvas.drawRect(f1, 0.0F, f2, j - i, ArticleViewer.quoteLinePaint);
        return;
      }
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      paramInt2 = 0;
      int i = View.MeasureSpec.getSize(paramInt1);
      if (this.currentBlock != null)
        if (this.currentBlock.level == 0)
        {
          this.textY = AndroidUtilities.dp(8.0F);
          this.textX = AndroidUtilities.dp(18.0F);
          paramInt1 = paramInt2;
          if (this.lastCreatedWidth != i)
          {
            this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(18.0F) - this.textX, this.currentBlock);
            paramInt1 = paramInt2;
            if (this.textLayout != null)
            {
              paramInt1 = this.textLayout.getHeight();
              if (this.currentBlock.level <= 0)
                break label158;
              paramInt1 += AndroidUtilities.dp(8.0F);
            }
          }
        }
      while (true)
      {
        setMeasuredDimension(i, paramInt1);
        return;
        this.textY = 0;
        this.textX = AndroidUtilities.dp(this.currentBlock.level * 14 + 18);
        break;
        label158: paramInt1 += AndroidUtilities.dp(16.0F);
        continue;
        paramInt1 = 1;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setBlock(TLRPC.TL_pageBlockFooter paramTL_pageBlockFooter)
    {
      this.currentBlock = paramTL_pageBlockFooter;
      this.lastCreatedWidth = 0;
      requestLayout();
    }
  }

  private class BlockHeaderCell extends View
  {
    private TLRPC.TL_pageBlockHeader currentBlock;
    private int lastCreatedWidth;
    private StaticLayout textLayout;
    private int textX = AndroidUtilities.dp(18.0F);
    private int textY = AndroidUtilities.dp(8.0F);

    public BlockHeaderCell(Context arg2)
    {
      super();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
        return;
      while (this.textLayout == null);
      paramCanvas.save();
      paramCanvas.translate(this.textX, this.textY);
      ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
      this.textLayout.draw(paramCanvas);
      paramCanvas.restore();
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      paramInt2 = 0;
      if (this.currentBlock != null)
      {
        paramInt1 = paramInt2;
        if (this.lastCreatedWidth != i)
        {
          this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(36.0F), this.currentBlock);
          paramInt1 = paramInt2;
          if (this.textLayout == null);
        }
      }
      for (paramInt1 = 0 + (AndroidUtilities.dp(16.0F) + this.textLayout.getHeight()); ; paramInt1 = 1)
      {
        setMeasuredDimension(i, paramInt1);
        return;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setBlock(TLRPC.TL_pageBlockHeader paramTL_pageBlockHeader)
    {
      this.currentBlock = paramTL_pageBlockHeader;
      this.lastCreatedWidth = 0;
      requestLayout();
    }
  }

  private class BlockListCell extends View
  {
    private TLRPC.TL_pageBlockList currentBlock;
    private int lastCreatedWidth;
    private ArrayList<StaticLayout> textLayouts = new ArrayList();
    private ArrayList<StaticLayout> textNumLayouts = new ArrayList();
    private ArrayList<Integer> textYLayouts = new ArrayList();

    public BlockListCell(Context arg2)
    {
      super();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      while (true)
      {
        return;
        int j = this.textLayouts.size();
        int i = 0;
        while (i < j)
        {
          StaticLayout localStaticLayout1 = (StaticLayout)this.textLayouts.get(i);
          StaticLayout localStaticLayout2 = (StaticLayout)this.textNumLayouts.get(i);
          paramCanvas.save();
          paramCanvas.translate(AndroidUtilities.dp(18.0F), ((Integer)this.textYLayouts.get(i)).intValue());
          if (localStaticLayout2 != null)
            localStaticLayout2.draw(paramCanvas);
          paramCanvas.translate(AndroidUtilities.dp(18.0F), 0.0F);
          ArticleViewer.this.drawLayoutLink(paramCanvas, localStaticLayout1);
          if (localStaticLayout1 != null)
            localStaticLayout1.draw(paramCanvas);
          paramCanvas.restore();
          i += 1;
        }
      }
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int j = View.MeasureSpec.getSize(paramInt1);
      if (this.currentBlock != null)
      {
        if (this.lastCreatedWidth == j)
          break label253;
        this.textLayouts.clear();
        this.textYLayouts.clear();
        this.textNumLayouts.clear();
        int k = this.currentBlock.items.size();
        paramInt2 = 0;
        paramInt1 = 0;
        if (paramInt2 < k)
        {
          TLRPC.RichText localRichText = (TLRPC.RichText)this.currentBlock.items.get(paramInt2);
          int i = paramInt1 + AndroidUtilities.dp(8.0F);
          Object localObject = ArticleViewer.this.createLayoutForText(null, localRichText, j - AndroidUtilities.dp(54.0F), this.currentBlock);
          this.textYLayouts.add(Integer.valueOf(i));
          this.textLayouts.add(localObject);
          paramInt1 = i;
          if (localObject != null)
            paramInt1 = i + ((StaticLayout)localObject).getHeight();
          if (this.currentBlock.ordered);
          for (localObject = String.format(Locale.US, "%d.", new Object[] { Integer.valueOf(paramInt2 + 1) }); ; localObject = "•")
          {
            localObject = ArticleViewer.this.createLayoutForText((CharSequence)localObject, localRichText, j - AndroidUtilities.dp(54.0F), this.currentBlock);
            this.textNumLayouts.add(localObject);
            paramInt2 += 1;
            break;
          }
        }
        paramInt1 = AndroidUtilities.dp(8.0F) + paramInt1;
      }
      while (true)
      {
        setMeasuredDimension(j, paramInt1);
        return;
        paramInt1 = 1;
        continue;
        label253: paramInt1 = 0;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      int j = this.textLayouts.size();
      int k = AndroidUtilities.dp(36.0F);
      int i = 0;
      while (i < j)
      {
        StaticLayout localStaticLayout = (StaticLayout)this.textLayouts.get(i);
        if (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, localStaticLayout, k, ((Integer)this.textYLayouts.get(i)).intValue()))
          return true;
        i += 1;
      }
      return super.onTouchEvent(paramMotionEvent);
    }

    public void setBlock(TLRPC.TL_pageBlockList paramTL_pageBlockList)
    {
      this.currentBlock = paramTL_pageBlockList;
      this.lastCreatedWidth = 0;
      requestLayout();
    }
  }

  private class BlockParagraphCell extends View
  {
    private TLRPC.TL_pageBlockParagraph currentBlock;
    private int lastCreatedWidth;
    private StaticLayout textLayout;
    private int textX;
    private int textY;

    public BlockParagraphCell(Context arg2)
    {
      super();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
      {
        return;
        if (this.textLayout == null)
          continue;
        paramCanvas.save();
        paramCanvas.translate(this.textX, this.textY);
        ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
        this.textLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      while (this.currentBlock.level <= 0);
      float f1 = AndroidUtilities.dp(18.0F);
      float f2 = AndroidUtilities.dp(20.0F);
      int j = getMeasuredHeight();
      if (this.currentBlock.bottom);
      for (int i = AndroidUtilities.dp(6.0F); ; i = 0)
      {
        paramCanvas.drawRect(f1, 0.0F, f2, j - i, ArticleViewer.quoteLinePaint);
        return;
      }
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      paramInt2 = 0;
      int i = View.MeasureSpec.getSize(paramInt1);
      if (this.currentBlock != null)
        if (this.currentBlock.level == 0)
          if (this.currentBlock.caption != null)
          {
            this.textY = AndroidUtilities.dp(4.0F);
            this.textX = AndroidUtilities.dp(18.0F);
            label52: paramInt1 = paramInt2;
            if (this.lastCreatedWidth != i)
            {
              if (this.currentBlock.text == null)
                break label190;
              this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(18.0F) - this.textX, this.currentBlock);
              label107: paramInt1 = paramInt2;
              if (this.textLayout != null)
              {
                paramInt1 = this.textLayout.getHeight();
                if (this.currentBlock.level <= 0)
                  break label238;
                paramInt1 += AndroidUtilities.dp(8.0F);
              }
            }
          }
      while (true)
      {
        setMeasuredDimension(i, paramInt1);
        return;
        this.textY = AndroidUtilities.dp(8.0F);
        break;
        this.textY = 0;
        this.textX = AndroidUtilities.dp(this.currentBlock.level * 14 + 18);
        break label52;
        label190: if (this.currentBlock.caption == null)
          break label107;
        this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, i - AndroidUtilities.dp(18.0F) - this.textX, this.currentBlock);
        break label107;
        label238: paramInt1 += AndroidUtilities.dp(16.0F);
        continue;
        paramInt1 = 1;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setBlock(TLRPC.TL_pageBlockParagraph paramTL_pageBlockParagraph)
    {
      this.currentBlock = paramTL_pageBlockParagraph;
      this.lastCreatedWidth = 0;
      requestLayout();
    }
  }

  private class BlockPhotoCell extends View
  {
    private TLRPC.TL_pageBlockPhoto currentBlock;
    private int currentType;
    private ImageReceiver imageView = new ImageReceiver(this);
    private boolean isFirst;
    private boolean isLast;
    private int lastCreatedWidth;
    private TLRPC.PageBlock parentBlock;
    private boolean photoPressed;
    private StaticLayout textLayout;
    private int textX;
    private int textY;

    public BlockPhotoCell(Context paramInt, int arg3)
    {
      super();
      int i;
      this.currentType = i;
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
      {
        return;
        this.imageView.draw(paramCanvas);
        if (this.textLayout == null)
          continue;
        paramCanvas.save();
        f1 = this.textX;
        int i = this.imageView.getImageY() + this.imageView.getImageHeight() + AndroidUtilities.dp(8.0F);
        this.textY = i;
        paramCanvas.translate(f1, i);
        ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
        this.textLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      while (this.currentBlock.level <= 0);
      float f1 = AndroidUtilities.dp(18.0F);
      float f2 = AndroidUtilities.dp(20.0F);
      int k = getMeasuredHeight();
      if (this.currentBlock.bottom);
      for (int j = AndroidUtilities.dp(6.0F); ; j = 0)
      {
        paramCanvas.drawRect(f1, 0.0F, f2, k - j, ArticleViewer.quoteLinePaint);
        return;
      }
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      paramInt1 = View.MeasureSpec.getSize(paramInt1);
      if (this.currentType == 1)
      {
        paramInt2 = ArticleViewer.this.listView.getWidth();
        paramInt1 = ((View)getParent()).getMeasuredHeight();
      }
      while (true)
      {
        Object localObject1;
        int j;
        int k;
        label118: TLRPC.PhotoSize localPhotoSize;
        label159: int n;
        label204: Object localObject2;
        int m;
        label246: TLRPC.FileLocation localFileLocation1;
        if (this.currentBlock != null)
        {
          localObject1 = ArticleViewer.this.getPhotoWithId(this.currentBlock.photo_id);
          if ((this.currentType == 0) && (this.currentBlock.level > 0))
          {
            i = AndroidUtilities.dp(this.currentBlock.level * 14);
            i = AndroidUtilities.dp(18.0F) + i;
            this.textX = i;
            j = paramInt2 - (AndroidUtilities.dp(18.0F) + i);
            k = j;
            if (localObject1 == null)
              break label642;
            localPhotoSize = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo)localObject1).sizes, AndroidUtilities.getPhotoSize());
            localObject1 = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo)localObject1).sizes, 80, true);
            if (localPhotoSize != localObject1)
              break label639;
            localObject1 = null;
            if (this.currentType != 0)
              break label636;
            n = (int)(j / localPhotoSize.w * localPhotoSize.h);
            if (!(this.parentBlock instanceof TLRPC.TL_pageBlockCover))
              break label494;
            paramInt1 = Math.min(n, j);
            localObject2 = this.imageView;
            if ((!this.isFirst) && (this.currentType != 1) && (this.currentType != 2) && (this.currentBlock.level <= 0))
              break label577;
            m = 0;
            ((ImageReceiver)localObject2).setImageCoords(i, m, j, paramInt1);
            if (this.currentType != 0)
              break label587;
            localObject2 = null;
            label267: ImageReceiver localImageReceiver = this.imageView;
            TLRPC.FileLocation localFileLocation2 = localPhotoSize.location;
            if (localObject1 == null)
              break label619;
            localFileLocation1 = ((TLRPC.PhotoSize)localObject1).location;
            label292: if (localObject1 == null)
              break label625;
            localObject1 = "80_80_b";
            label301: localImageReceiver.setImage(localFileLocation2, (String)localObject2, localFileLocation1, (String)localObject1, localPhotoSize.size, null, true);
          }
        }
        label642: for (int i = paramInt1; ; i = paramInt1)
        {
          paramInt1 = i;
          if (this.currentType == 0)
          {
            paramInt1 = i;
            if (this.lastCreatedWidth != paramInt2)
            {
              this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, k, this.currentBlock);
              paramInt1 = i;
              if (this.textLayout != null)
                paramInt1 = i + (AndroidUtilities.dp(8.0F) + this.textLayout.getHeight());
            }
          }
          i = paramInt1;
          if (!this.isFirst)
          {
            i = paramInt1;
            if (this.currentType == 0)
            {
              i = paramInt1;
              if (this.currentBlock.level <= 0)
                i = paramInt1 + AndroidUtilities.dp(8.0F);
            }
          }
          paramInt1 = i;
          if (this.currentType != 2);
          for (paramInt1 = i + AndroidUtilities.dp(8.0F); ; paramInt1 = 1)
          {
            setMeasuredDimension(paramInt2, paramInt1);
            return;
            if (this.currentType != 2)
              break label647;
            paramInt2 = paramInt1;
            break;
            i = 0;
            this.textX = AndroidUtilities.dp(18.0F);
            k = paramInt2 - AndroidUtilities.dp(36.0F);
            j = paramInt2;
            break label118;
            label494: m = (int)((Math.max(ArticleViewer.this.listView.getMeasuredWidth(), ArticleViewer.this.listView.getMeasuredHeight()) - AndroidUtilities.dp(56.0F)) * 0.9F);
            paramInt1 = n;
            if (n <= m)
              break label636;
            j = (int)(m / localPhotoSize.h * localPhotoSize.w);
            i = (paramInt2 - i - j) / 2 + i;
            paramInt1 = m;
            break label204;
            label577: m = AndroidUtilities.dp(8.0F);
            break label246;
            label587: localObject2 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(j), Integer.valueOf(paramInt1) });
            break label267;
            label619: localFileLocation1 = null;
            break label292;
            label625: localObject1 = null;
            break label301;
          }
          label636: break label204;
          label639: break label159;
        }
        label647: paramInt2 = paramInt1;
        paramInt1 = 0;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      if ((paramMotionEvent.getAction() == 0) && (this.imageView.isInsideImage(f1, f2)))
        this.photoPressed = true;
      while ((this.photoPressed) || (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (super.onTouchEvent(paramMotionEvent)))
      {
        return true;
        if ((paramMotionEvent.getAction() == 1) && (this.photoPressed))
        {
          this.photoPressed = false;
          ArticleViewer.this.openPhoto(this.currentBlock);
          continue;
        }
        if (paramMotionEvent.getAction() != 3)
          continue;
        this.photoPressed = false;
      }
      return false;
    }

    public void setBlock(TLRPC.TL_pageBlockPhoto paramTL_pageBlockPhoto, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.parentBlock = null;
      this.currentBlock = paramTL_pageBlockPhoto;
      this.lastCreatedWidth = 0;
      this.isFirst = paramBoolean1;
      this.isLast = paramBoolean2;
      requestLayout();
    }

    public void setParentBlock(TLRPC.PageBlock paramPageBlock)
    {
      this.parentBlock = paramPageBlock;
    }
  }

  private class BlockPreformattedCell extends View
  {
    private TLRPC.TL_pageBlockPreformatted currentBlock;
    private int lastCreatedWidth;
    private StaticLayout textLayout;

    public BlockPreformattedCell(Context arg2)
    {
      super();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
      {
        return;
        paramCanvas.drawRect(0.0F, AndroidUtilities.dp(8.0F), getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(8.0F), ArticleViewer.preformattedBackgroundPaint);
      }
      while (this.textLayout == null);
      paramCanvas.save();
      paramCanvas.translate(AndroidUtilities.dp(12.0F), AndroidUtilities.dp(16.0F));
      this.textLayout.draw(paramCanvas);
      paramCanvas.restore();
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      paramInt2 = 0;
      if (this.currentBlock != null)
      {
        paramInt1 = paramInt2;
        if (this.lastCreatedWidth != i)
        {
          this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(24.0F), this.currentBlock);
          paramInt1 = paramInt2;
          if (this.textLayout == null);
        }
      }
      for (paramInt1 = 0 + (AndroidUtilities.dp(32.0F) + this.textLayout.getHeight()); ; paramInt1 = 1)
      {
        setMeasuredDimension(i, paramInt1);
        return;
      }
    }

    public void setBlock(TLRPC.TL_pageBlockPreformatted paramTL_pageBlockPreformatted)
    {
      this.currentBlock = paramTL_pageBlockPreformatted;
      this.lastCreatedWidth = 0;
      requestLayout();
    }
  }

  private class BlockPullquoteCell extends View
  {
    private TLRPC.TL_pageBlockPullquote currentBlock;
    private int lastCreatedWidth;
    private StaticLayout textLayout;
    private StaticLayout textLayout2;
    private int textX = AndroidUtilities.dp(18.0F);
    private int textY = AndroidUtilities.dp(8.0F);
    private int textY2;

    public BlockPullquoteCell(Context arg2)
    {
      super();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
      {
        return;
        if (this.textLayout == null)
          continue;
        paramCanvas.save();
        paramCanvas.translate(this.textX, this.textY);
        ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
        this.textLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      while (this.textLayout2 == null);
      paramCanvas.save();
      paramCanvas.translate(this.textX, this.textY2);
      ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout2);
      this.textLayout2.draw(paramCanvas);
      paramCanvas.restore();
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      paramInt1 = 0;
      paramInt2 = 0;
      if (this.currentBlock != null)
        if (this.lastCreatedWidth != i)
        {
          this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(36.0F), this.currentBlock);
          paramInt1 = paramInt2;
          if (this.textLayout != null)
            paramInt1 = 0 + (AndroidUtilities.dp(8.0F) + this.textLayout.getHeight());
          this.textLayout2 = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, i - AndroidUtilities.dp(36.0F), this.currentBlock);
          paramInt2 = paramInt1;
          if (this.textLayout2 != null)
          {
            this.textY2 = (AndroidUtilities.dp(2.0F) + paramInt1);
            paramInt2 = paramInt1 + (AndroidUtilities.dp(8.0F) + this.textLayout2.getHeight());
          }
          paramInt1 = paramInt2;
          if (paramInt2 == 0);
        }
      for (paramInt1 = paramInt2 + AndroidUtilities.dp(8.0F); ; paramInt1 = 1)
      {
        setMeasuredDimension(i, paramInt1);
        return;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout2, this.textX, this.textY2)) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setBlock(TLRPC.TL_pageBlockPullquote paramTL_pageBlockPullquote)
    {
      this.currentBlock = paramTL_pageBlockPullquote;
      this.lastCreatedWidth = 0;
      requestLayout();
    }
  }

  private class BlockSlideshowCell extends FrameLayout
  {
    private ab adapter;
    private TLRPC.TL_pageBlockSlideshow currentBlock;
    private View dotsContainer;
    private ViewPager innerListView;
    private int lastCreatedWidth;
    private StaticLayout textLayout;
    private int textX = AndroidUtilities.dp(18.0F);
    private int textY;

    public BlockSlideshowCell(Context arg2)
    {
      super();
      if (ArticleViewer.dotsPaint == null)
      {
        ArticleViewer.access$8302(new Paint(1));
        ArticleViewer.dotsPaint.setColor(-1);
      }
      this.innerListView = new ViewPager(localContext, ArticleViewer.this)
      {
        public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
        {
          ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
          return super.onInterceptTouchEvent(paramMotionEvent);
        }

        public boolean onTouchEvent(MotionEvent paramMotionEvent)
        {
          return super.onTouchEvent(paramMotionEvent);
        }
      };
      this.innerListView.addOnPageChangeListener(new ViewPager.f(ArticleViewer.this)
      {
        public void onPageScrollStateChanged(int paramInt)
        {
        }

        public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
        {
        }

        public void onPageSelected(int paramInt)
        {
          ArticleViewer.BlockSlideshowCell.this.dotsContainer.invalidate();
        }
      });
      ViewPager localViewPager = this.innerListView;
      3 local3 = new ab(ArticleViewer.this)
      {
        public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
        {
          paramViewGroup.removeView(((ObjectContainer)paramObject).view);
        }

        public int getCount()
        {
          if (ArticleViewer.BlockSlideshowCell.this.currentBlock == null)
            return 0;
          return ArticleViewer.BlockSlideshowCell.this.currentBlock.items.size();
        }

        public int getItemPosition(Object paramObject)
        {
          paramObject = (ObjectContainer)paramObject;
          if (ArticleViewer.BlockSlideshowCell.this.currentBlock.items.contains(paramObject.block))
            return -1;
          return -2;
        }

        public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
        {
          TLRPC.PageBlock localPageBlock = (TLRPC.PageBlock)ArticleViewer.BlockSlideshowCell.this.currentBlock.items.get(paramInt);
          Object localObject;
          if ((localPageBlock instanceof TLRPC.TL_pageBlockPhoto))
          {
            localObject = new ArticleViewer.BlockPhotoCell(ArticleViewer.this, ArticleViewer.BlockSlideshowCell.this.getContext(), 1);
            ((ArticleViewer.BlockPhotoCell)localObject).setBlock((TLRPC.TL_pageBlockPhoto)localPageBlock, true, true);
          }
          while (true)
          {
            paramViewGroup.addView((View)localObject);
            paramViewGroup = new ObjectContainer();
            ObjectContainer.access$8602(paramViewGroup, (View)localObject);
            ObjectContainer.access$8702(paramViewGroup, localPageBlock);
            return paramViewGroup;
            localObject = new ArticleViewer.BlockVideoCell(ArticleViewer.this, ArticleViewer.BlockSlideshowCell.this.getContext(), 1);
            ((ArticleViewer.BlockVideoCell)localObject).setBlock((TLRPC.TL_pageBlockVideo)localPageBlock, true, true);
          }
        }

        public boolean isViewFromObject(View paramView, Object paramObject)
        {
          return ((ObjectContainer)paramObject).view == paramView;
        }

        public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
        {
          if (paramDataSetObserver != null)
            super.unregisterDataSetObserver(paramDataSetObserver);
        }

        class ObjectContainer
        {
          private TLRPC.PageBlock block;
          private View view;

          ObjectContainer()
          {
          }
        }
      };
      this.adapter = local3;
      localViewPager.setAdapter(local3);
      AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, -657673);
      addView(this.innerListView);
      this.dotsContainer = new View(localContext, ArticleViewer.this)
      {
        protected void onDraw(Canvas paramCanvas)
        {
          if (ArticleViewer.BlockSlideshowCell.this.currentBlock == null)
            return;
          int j = ArticleViewer.BlockSlideshowCell.this.innerListView.getCurrentItem();
          int i = 0;
          label24: int k;
          if (i < ArticleViewer.BlockSlideshowCell.this.currentBlock.items.size())
          {
            k = AndroidUtilities.dp(4.0F);
            k = AndroidUtilities.dp(13.0F) * i + k;
            if (j != i)
              break label117;
          }
          label117: for (Drawable localDrawable = ArticleViewer.this.slideDotBigDrawable; ; localDrawable = ArticleViewer.this.slideDotDrawable)
          {
            localDrawable.setBounds(k - AndroidUtilities.dp(5.0F), 0, k + AndroidUtilities.dp(5.0F), AndroidUtilities.dp(10.0F));
            localDrawable.draw(paramCanvas);
            i += 1;
            break label24;
            break;
          }
        }
      };
      addView(this.dotsContainer);
      setWillNotDraw(false);
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
        return;
      while (this.textLayout == null);
      paramCanvas.save();
      paramCanvas.translate(this.textX, this.textY);
      ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
      this.textLayout.draw(paramCanvas);
      paramCanvas.restore();
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.innerListView.layout(0, AndroidUtilities.dp(8.0F), this.innerListView.getMeasuredWidth(), AndroidUtilities.dp(8.0F) + this.innerListView.getMeasuredHeight());
      paramInt2 = this.innerListView.getBottom() - AndroidUtilities.dp(23.0F);
      paramInt1 = (paramInt3 - paramInt1 - this.dotsContainer.getMeasuredWidth()) / 2;
      this.dotsContainer.layout(paramInt1, paramInt2, this.dotsContainer.getMeasuredWidth() + paramInt1, this.dotsContainer.getMeasuredHeight() + paramInt2);
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      if (this.currentBlock != null)
      {
        paramInt2 = AndroidUtilities.dp(310.0F);
        this.innerListView.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824));
        paramInt1 = this.currentBlock.items.size();
        View localView = this.dotsContainer;
        int j = AndroidUtilities.dp(7.0F);
        localView.measure(View.MeasureSpec.makeMeasureSpec((paramInt1 - 1) * AndroidUtilities.dp(6.0F) + j * paramInt1 + AndroidUtilities.dp(4.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0F), 1073741824));
        paramInt1 = paramInt2;
        if (this.lastCreatedWidth != i)
        {
          this.textY = (AndroidUtilities.dp(16.0F) + paramInt2);
          this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, i - AndroidUtilities.dp(36.0F), this.currentBlock);
          paramInt1 = paramInt2;
          if (this.textLayout != null)
            paramInt1 = paramInt2 + (AndroidUtilities.dp(8.0F) + this.textLayout.getHeight());
        }
        paramInt1 += AndroidUtilities.dp(16.0F);
      }
      while (true)
      {
        setMeasuredDimension(i, paramInt1);
        return;
        paramInt1 = 1;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setBlock(TLRPC.TL_pageBlockSlideshow paramTL_pageBlockSlideshow)
    {
      this.currentBlock = paramTL_pageBlockSlideshow;
      this.lastCreatedWidth = 0;
      this.innerListView.setCurrentItem(0, false);
      this.adapter.notifyDataSetChanged();
      requestLayout();
    }
  }

  private class BlockSubheaderCell extends View
  {
    private TLRPC.TL_pageBlockSubheader currentBlock;
    private int lastCreatedWidth;
    private StaticLayout textLayout;
    private int textX = AndroidUtilities.dp(18.0F);
    private int textY = AndroidUtilities.dp(8.0F);

    public BlockSubheaderCell(Context arg2)
    {
      super();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
        return;
      while (this.textLayout == null);
      paramCanvas.save();
      paramCanvas.translate(this.textX, this.textY);
      ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
      this.textLayout.draw(paramCanvas);
      paramCanvas.restore();
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      paramInt2 = 0;
      if (this.currentBlock != null)
      {
        paramInt1 = paramInt2;
        if (this.lastCreatedWidth != i)
        {
          this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(36.0F), this.currentBlock);
          paramInt1 = paramInt2;
          if (this.textLayout == null);
        }
      }
      for (paramInt1 = 0 + (AndroidUtilities.dp(16.0F) + this.textLayout.getHeight()); ; paramInt1 = 1)
      {
        setMeasuredDimension(i, paramInt1);
        return;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setBlock(TLRPC.TL_pageBlockSubheader paramTL_pageBlockSubheader)
    {
      this.currentBlock = paramTL_pageBlockSubheader;
      this.lastCreatedWidth = 0;
      requestLayout();
    }
  }

  private class BlockSubtitleCell extends View
  {
    private TLRPC.TL_pageBlockSubtitle currentBlock;
    private int lastCreatedWidth;
    private StaticLayout textLayout;
    private int textX = AndroidUtilities.dp(18.0F);
    private int textY = AndroidUtilities.dp(8.0F);

    public BlockSubtitleCell(Context arg2)
    {
      super();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
        return;
      while (this.textLayout == null);
      paramCanvas.save();
      paramCanvas.translate(this.textX, this.textY);
      ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
      this.textLayout.draw(paramCanvas);
      paramCanvas.restore();
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      paramInt2 = 0;
      if (this.currentBlock != null)
      {
        paramInt1 = paramInt2;
        if (this.lastCreatedWidth != i)
        {
          this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(36.0F), this.currentBlock);
          paramInt1 = paramInt2;
          if (this.textLayout == null);
        }
      }
      for (paramInt1 = 0 + (AndroidUtilities.dp(16.0F) + this.textLayout.getHeight()); ; paramInt1 = 1)
      {
        setMeasuredDimension(i, paramInt1);
        return;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setBlock(TLRPC.TL_pageBlockSubtitle paramTL_pageBlockSubtitle)
    {
      this.currentBlock = paramTL_pageBlockSubtitle;
      this.lastCreatedWidth = 0;
      requestLayout();
    }
  }

  private class BlockTitleCell extends View
  {
    private TLRPC.TL_pageBlockTitle currentBlock;
    private int lastCreatedWidth;
    private StaticLayout textLayout;
    private int textX = AndroidUtilities.dp(18.0F);
    private int textY;

    public BlockTitleCell(Context arg2)
    {
      super();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
        return;
      while (this.textLayout == null);
      paramCanvas.save();
      paramCanvas.translate(this.textX, this.textY);
      ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
      this.textLayout.draw(paramCanvas);
      paramCanvas.restore();
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      paramInt1 = 0;
      paramInt2 = 0;
      if (this.currentBlock != null)
        if (this.lastCreatedWidth != i)
        {
          this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(36.0F), this.currentBlock);
          paramInt1 = paramInt2;
          if (this.textLayout != null)
            paramInt1 = 0 + (AndroidUtilities.dp(16.0F) + this.textLayout.getHeight());
          if (!this.currentBlock.first)
            break label113;
          paramInt1 += AndroidUtilities.dp(8.0F);
          this.textY = AndroidUtilities.dp(16.0F);
        }
      while (true)
      {
        setMeasuredDimension(i, paramInt1);
        return;
        label113: this.textY = AndroidUtilities.dp(8.0F);
        continue;
        paramInt1 = 1;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setBlock(TLRPC.TL_pageBlockTitle paramTL_pageBlockTitle)
    {
      this.currentBlock = paramTL_pageBlockTitle;
      this.lastCreatedWidth = 0;
      requestLayout();
    }
  }

  private class BlockVideoCell extends View
    implements MediaController.FileDownloadProgressListener
  {
    private int TAG;
    private int buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private boolean cancelLoading;
    private TLRPC.TL_pageBlockVideo currentBlock;
    private TLRPC.Document currentDocument;
    private int currentType;
    private ImageReceiver imageView = new ImageReceiver(this);
    private boolean isFirst;
    private boolean isGif;
    private boolean isLast;
    private int lastCreatedWidth;
    private TLRPC.PageBlock parentBlock;
    private boolean photoPressed;
    private RadialProgress radialProgress;
    private StaticLayout textLayout;
    private int textX;
    private int textY;

    public BlockVideoCell(Context paramInt, int arg3)
    {
      super();
      int i;
      this.currentType = i;
      this.radialProgress = new RadialProgress(this);
      this.radialProgress.setProgressColor(-1);
      this.TAG = MediaController.getInstance().generateObserverTag();
    }

    private void didPressedButton(boolean paramBoolean)
    {
      TLRPC.FileLocation localFileLocation;
      if (this.buttonState == 0)
      {
        this.cancelLoading = false;
        this.radialProgress.setProgress(0.0F, false);
        if (this.isGif)
        {
          ImageReceiver localImageReceiver = this.imageView;
          TLRPC.Document localDocument = this.currentDocument;
          if (this.currentDocument.thumb != null)
          {
            localFileLocation = this.currentDocument.thumb.location;
            localImageReceiver.setImage(localDocument, null, localFileLocation, "80_80_b", this.currentDocument.size, null, true);
            label79: this.buttonState = 1;
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
            invalidate();
          }
        }
      }
      do
      {
        return;
        localFileLocation = null;
        break;
        FileLoader.getInstance().loadFile(this.currentDocument, true, true);
        break label79;
        if (this.buttonState == 1)
        {
          this.cancelLoading = true;
          if (this.isGif)
            this.imageView.cancelLoadImage();
          while (true)
          {
            this.buttonState = 0;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
            invalidate();
            return;
            FileLoader.getInstance().cancelLoadFile(this.currentDocument);
          }
        }
        if (this.buttonState != 2)
          continue;
        this.imageView.setAllowStartAnimation(true);
        this.imageView.startAnimation();
        this.buttonState = -1;
        this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
        return;
      }
      while (this.buttonState != 3);
      ArticleViewer.this.openPhoto(this.currentBlock);
    }

    private Drawable getDrawableForCurrentState()
    {
      this.radialProgress.setAlphaForPrevious(true);
      if ((this.buttonState >= 0) && (this.buttonState < 4))
        return Theme.chat_photoStatesDrawables[this.buttonState][this.buttonPressed];
      return null;
    }

    public int getObserverTag()
    {
      return this.TAG;
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if (this.currentBlock == null);
      do
      {
        return;
        this.imageView.draw(paramCanvas);
        if (this.imageView.getVisible())
          this.radialProgress.draw(paramCanvas);
        if (this.textLayout == null)
          continue;
        paramCanvas.save();
        f1 = this.textX;
        int i = this.imageView.getImageY() + this.imageView.getImageHeight() + AndroidUtilities.dp(8.0F);
        this.textY = i;
        paramCanvas.translate(f1, i);
        ArticleViewer.this.drawLayoutLink(paramCanvas, this.textLayout);
        this.textLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      while (this.currentBlock.level <= 0);
      float f1 = AndroidUtilities.dp(18.0F);
      float f2 = AndroidUtilities.dp(20.0F);
      int k = getMeasuredHeight();
      if (this.currentBlock.bottom);
      for (int j = AndroidUtilities.dp(6.0F); ; j = 0)
      {
        paramCanvas.drawRect(f1, 0.0F, f2, k - j, ArticleViewer.quoteLinePaint);
        return;
      }
    }

    public void onFailedDownload(String paramString)
    {
      updateButtonState(false);
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      paramInt1 = View.MeasureSpec.getSize(paramInt1);
      if (this.currentType == 1)
      {
        paramInt2 = ArticleViewer.this.listView.getWidth();
        paramInt1 = ((View)getParent()).getMeasuredHeight();
      }
      while (true)
      {
        int j;
        int k;
        label102: Object localObject2;
        int m;
        label163: Object localObject1;
        label205: Object localObject3;
        if (this.currentBlock != null)
          if ((this.currentType == 0) && (this.currentBlock.level > 0))
          {
            i = AndroidUtilities.dp(this.currentBlock.level * 14);
            i = AndroidUtilities.dp(18.0F) + i;
            this.textX = i;
            j = paramInt2 - (AndroidUtilities.dp(18.0F) + i);
            k = j;
            if (this.currentDocument == null)
              break label736;
            localObject2 = this.currentDocument.thumb;
            if (this.currentType != 0)
              break label733;
            m = (int)(j / ((TLRPC.PhotoSize)localObject2).w * ((TLRPC.PhotoSize)localObject2).h);
            if (!(this.parentBlock instanceof TLRPC.TL_pageBlockCover))
              break label568;
            paramInt1 = Math.min(m, j);
            localObject1 = this.imageView;
            if ((!this.isFirst) && (this.currentType != 1) && (this.currentType != 2) && (this.currentBlock.level <= 0))
              break label644;
            m = 0;
            ((ImageReceiver)localObject1).setImageCoords(i, m, j, paramInt1);
            if (!this.isGif)
              break label666;
            localObject3 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(j), Integer.valueOf(paramInt1) });
            ImageReceiver localImageReceiver = this.imageView;
            TLRPC.Document localDocument = this.currentDocument;
            if (localObject2 == null)
              break label654;
            localObject1 = ((TLRPC.PhotoSize)localObject2).location;
            label277: if (localObject2 == null)
              break label660;
            localObject2 = "80_80_b";
            label286: localImageReceiver.setImage(localDocument, (String)localObject3, (TLRPC.FileLocation)localObject1, (String)localObject2, this.currentDocument.size, null, true);
            i = AndroidUtilities.dp(48.0F);
            this.buttonX = (int)(this.imageView.getImageX() + (this.imageView.getImageWidth() - i) / 2.0F);
            this.buttonY = (int)(this.imageView.getImageY() + (this.imageView.getImageHeight() - i) / 2.0F);
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + i, i + this.buttonY);
          }
        label568: label716: label727: label733: label736: for (int i = paramInt1; ; i = paramInt1)
        {
          paramInt1 = i;
          if (this.currentType == 0)
          {
            paramInt1 = i;
            if (this.lastCreatedWidth != paramInt2)
            {
              this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, k, this.currentBlock);
              paramInt1 = i;
              if (this.textLayout != null)
                paramInt1 = i + (AndroidUtilities.dp(8.0F) + this.textLayout.getHeight());
            }
          }
          i = paramInt1;
          if (!this.isFirst)
          {
            i = paramInt1;
            if (this.currentType == 0)
            {
              i = paramInt1;
              if (this.currentBlock.level <= 0)
                i = paramInt1 + AndroidUtilities.dp(8.0F);
            }
          }
          paramInt1 = i;
          if (this.currentType != 2);
          for (paramInt1 = i + AndroidUtilities.dp(8.0F); ; paramInt1 = 1)
          {
            setMeasuredDimension(paramInt2, paramInt1);
            return;
            if (this.currentType != 2)
              break label741;
            paramInt2 = paramInt1;
            break;
            this.textX = AndroidUtilities.dp(18.0F);
            k = paramInt2 - AndroidUtilities.dp(36.0F);
            i = 0;
            j = paramInt2;
            break label102;
            paramInt1 = (int)((Math.max(ArticleViewer.this.listView.getMeasuredWidth(), ArticleViewer.this.listView.getMeasuredHeight()) - AndroidUtilities.dp(56.0F)) * 0.9F);
            if (m <= paramInt1)
              break label727;
            j = (int)(paramInt1 / ((TLRPC.PhotoSize)localObject2).h * ((TLRPC.PhotoSize)localObject2).w);
            i += (paramInt2 - i - j) / 2;
            break label163;
            m = AndroidUtilities.dp(8.0F);
            break label205;
            localObject1 = null;
            break label277;
            localObject2 = null;
            break label286;
            localObject3 = this.imageView;
            if (localObject2 != null)
            {
              localObject1 = ((TLRPC.PhotoSize)localObject2).location;
              if (localObject2 == null)
                break label716;
            }
            for (localObject2 = "80_80_b"; ; localObject2 = null)
            {
              ((ImageReceiver)localObject3).setImage(null, null, (TLRPC.FileLocation)localObject1, (String)localObject2, 0, null, true);
              break;
              localObject1 = null;
              break label684;
            }
          }
          paramInt1 = m;
          break label163;
          break label163;
        }
        label644: label654: label660: label666: label684: label741: paramInt2 = paramInt1;
        paramInt1 = 0;
      }
    }

    public void onProgressDownload(String paramString, float paramFloat)
    {
      this.radialProgress.setProgress(paramFloat, true);
      if (this.buttonState != 1)
        updateButtonState(false);
    }

    public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean)
    {
    }

    public void onSuccessDownload(String paramString)
    {
      this.radialProgress.setProgress(1.0F, true);
      if (this.isGif)
      {
        this.buttonState = 2;
        didPressedButton(true);
        return;
      }
      updateButtonState(true);
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      if ((paramMotionEvent.getAction() == 0) && (this.imageView.isInsideImage(f1, f2)))
        if (((this.buttonState != -1) && (f1 >= this.buttonX) && (f1 <= this.buttonX + AndroidUtilities.dp(48.0F)) && (f2 >= this.buttonY) && (f2 <= this.buttonY + AndroidUtilities.dp(48.0F))) || (this.buttonState == 0))
        {
          this.buttonPressed = 1;
          invalidate();
        }
      while ((this.photoPressed) || (this.buttonPressed != 0) || (ArticleViewer.this.checkLayoutForLinks(paramMotionEvent, this, this.textLayout, this.textX, this.textY)) || (super.onTouchEvent(paramMotionEvent)))
      {
        return true;
        this.photoPressed = true;
        continue;
        if (paramMotionEvent.getAction() == 1)
        {
          if (this.photoPressed)
          {
            this.photoPressed = false;
            ArticleViewer.this.openPhoto(this.currentBlock);
            continue;
          }
          if (this.buttonPressed != 1)
            continue;
          this.buttonPressed = 0;
          playSoundEffect(0);
          didPressedButton(false);
          this.radialProgress.swapBackground(getDrawableForCurrentState());
          invalidate();
          continue;
        }
        if (paramMotionEvent.getAction() != 3)
          continue;
        this.photoPressed = false;
      }
      return false;
    }

    public void setBlock(TLRPC.TL_pageBlockVideo paramTL_pageBlockVideo, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.currentBlock = paramTL_pageBlockVideo;
      this.parentBlock = null;
      this.cancelLoading = false;
      this.currentDocument = ArticleViewer.this.getDocumentWithId(this.currentBlock.video_id);
      this.isGif = MessageObject.isGifDocument(this.currentDocument);
      this.lastCreatedWidth = 0;
      this.isFirst = paramBoolean1;
      this.isLast = paramBoolean2;
      updateButtonState(false);
      requestLayout();
    }

    public void setParentBlock(TLRPC.PageBlock paramPageBlock)
    {
      this.parentBlock = paramPageBlock;
    }

    public void updateButtonState(boolean paramBoolean)
    {
      float f = 0.0F;
      Object localObject = FileLoader.getAttachFileName(this.currentDocument);
      boolean bool = FileLoader.getPathToAttach(this.currentDocument, true).exists();
      if (TextUtils.isEmpty((CharSequence)localObject))
      {
        this.radialProgress.setBackground(null, false, false);
        return;
      }
      if (!bool)
      {
        MediaController.getInstance().addLoadingFileObserver((String)localObject, null, this);
        if (!FileLoader.getInstance().isLoadingFile((String)localObject))
          if ((!this.cancelLoading) && (this.isGif))
          {
            this.buttonState = 1;
            f = 0.0F;
            bool = true;
          }
        while (true)
        {
          this.radialProgress.setBackground(getDrawableForCurrentState(), bool, paramBoolean);
          this.radialProgress.setProgress(f, false);
          invalidate();
          return;
          this.buttonState = 0;
          f = 0.0F;
          bool = false;
          continue;
          this.buttonState = 1;
          localObject = ImageLoader.getInstance().getFileProgress((String)localObject);
          if (localObject != null)
            f = ((Float)localObject).floatValue();
          bool = true;
        }
      }
      MediaController.getInstance().removeLoadingFileObserver(this);
      if (!this.isGif);
      for (this.buttonState = 3; ; this.buttonState = -1)
      {
        this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
        invalidate();
        return;
      }
    }
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
      if ((ArticleViewer.this.checkingForLongPress) && (ArticleViewer.this.windowView != null))
      {
        ArticleViewer.access$2502(ArticleViewer.this, false);
        ArticleViewer.this.windowView.performHapticFeedback(0);
        if (ArticleViewer.this.pressedLink != null)
        {
          Object localObject = ArticleViewer.this.pressedLink.getUrl();
          BottomSheet.Builder localBuilder = new BottomSheet.Builder(ArticleViewer.this.parentActivity);
          localBuilder.setTitle((CharSequence)localObject);
          String str1 = LocaleController.getString("Open", 2131166165);
          String str2 = LocaleController.getString("Copy", 2131165583);
          localObject = new DialogInterface.OnClickListener((String)localObject)
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              if (ArticleViewer.this.parentActivity == null);
              do
              {
                return;
                if (paramInt != 0)
                  continue;
                Browser.openUrl(ArticleViewer.this.parentActivity, this.val$urlFinal);
                return;
              }
              while (paramInt != 1);
              String str = this.val$urlFinal;
              if (str.startsWith("mailto:"))
                paramDialogInterface = str.substring(7);
              while (true)
              {
                AndroidUtilities.addToClipboard(paramDialogInterface);
                return;
                paramDialogInterface = str;
                if (!str.startsWith("tel:"))
                  continue;
                paramDialogInterface = str.substring(4);
              }
            }
          };
          localBuilder.setItems(new CharSequence[] { str1, str2 }, (DialogInterface.OnClickListener)localObject);
          ArticleViewer.this.showDialog(localBuilder.create());
          ArticleViewer.this.hideActionBar();
          ArticleViewer.access$2602(ArticleViewer.this, null);
          ArticleViewer.access$2802(ArticleViewer.this, null);
          ArticleViewer.this.pressedLinkOwnerView.invalidate();
        }
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
      if (ArticleViewer.this.pendingCheckForLongPress == null)
        ArticleViewer.access$302(ArticleViewer.this, new ArticleViewer.CheckForLongPress(ArticleViewer.this));
      ArticleViewer.this.pendingCheckForLongPress.currentPressCount = ArticleViewer.access$404(ArticleViewer.this);
      if (ArticleViewer.this.windowView != null)
        ArticleViewer.this.windowView.postDelayed(ArticleViewer.this.pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout());
    }
  }

  private class FrameLayoutDrawer extends FrameLayout
  {
    public FrameLayoutDrawer(Context arg2)
    {
      super();
    }

    protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
    {
      return (paramView != ArticleViewer.this.aspectRatioFrameLayout) && (super.drawChild(paramCanvas, paramView, paramLong));
    }

    protected void onDraw(Canvas paramCanvas)
    {
      ArticleViewer.this.drawContent(paramCanvas);
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      ArticleViewer.this.processTouchEvent(paramMotionEvent);
      return true;
    }
  }

  private class PhotoBackgroundDrawable extends ColorDrawable
  {
    private Runnable drawRunnable;

    public PhotoBackgroundDrawable(int arg2)
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
      DrawerLayoutContainer localDrawerLayoutContainer;
      if ((ArticleViewer.this.parentActivity instanceof LaunchActivity))
      {
        localDrawerLayoutContainer = ((LaunchActivity)ArticleViewer.this.parentActivity).drawerLayoutContainer;
        if ((ArticleViewer.this.isPhotoVisible) && (paramInt == 255))
          break label57;
      }
      label57: for (boolean bool = true; ; bool = false)
      {
        localDrawerLayoutContainer.setAllowDrawContent(bool);
        super.setAlpha(paramInt);
        return;
      }
    }
  }

  public static class PlaceProviderObject
  {
    public int clipBottomAddition;
    public int clipTopAddition;
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

  private class RadialProgressView
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

    public RadialProgressView(Context paramView, View arg3)
    {
      if (ArticleViewer.decelerateInterpolator == null)
      {
        ArticleViewer.access$9702(new DecelerateInterpolator(1.5F));
        ArticleViewer.access$9802(new Paint(1));
        ArticleViewer.progressPaint.setStyle(Paint.Style.STROKE);
        ArticleViewer.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        ArticleViewer.progressPaint.setStrokeWidth(AndroidUtilities.dp(3.0F));
        ArticleViewer.progressPaint.setColor(-1);
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
        this.animatedProgressValue = (f1 * ArticleViewer.decelerateInterpolator.getInterpolation((float)this.currentProgressTime / 300.0F) + f2);
      }
    }

    public void onDraw(Canvas paramCanvas)
    {
      int i = (int)(this.size * this.scale);
      int j = (ArticleViewer.this.getContainerViewWidth() - i) / 2;
      int k = (ArticleViewer.this.getContainerViewHeight() - i) / 2;
      Drawable localDrawable;
      if ((this.previousBackgroundState >= 0) && (this.previousBackgroundState < 4))
      {
        localDrawable = ArticleViewer.progressDrawables[this.previousBackgroundState];
        if (localDrawable != null)
        {
          localDrawable.setAlpha((int)(this.animatedAlphaValue * 255.0F * this.alpha));
          localDrawable.setBounds(j, k, j + i, k + i);
          localDrawable.draw(paramCanvas);
        }
      }
      if ((this.backgroundState >= 0) && (this.backgroundState < 4))
      {
        localDrawable = ArticleViewer.progressDrawables[this.backgroundState];
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
        ArticleViewer.progressPaint.setAlpha((int)(this.animatedAlphaValue * 255.0F * this.alpha));
      }
      while (true)
      {
        this.progressRect.set(j + m, k + m, j + i - m, i + k - m);
        paramCanvas.drawArc(this.progressRect, this.radOffset - 90.0F, Math.max(4.0F, 360.0F * this.animatedProgressValue), false, ArticleViewer.progressPaint);
        updateAnimation();
        return;
        label320: localDrawable.setAlpha((int)(this.alpha * 255.0F));
        break;
        label336: ArticleViewer.progressPaint.setAlpha((int)(this.alpha * 255.0F));
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

  private class WebpageAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context context;

    public WebpageAdapter(Context arg2)
    {
      Object localObject;
      this.context = localObject;
    }

    private int getTypeForBlock(TLRPC.PageBlock paramPageBlock)
    {
      if ((paramPageBlock instanceof TLRPC.TL_pageBlockParagraph));
      do
      {
        return 0;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockHeader))
          return 1;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockDivider))
          return 2;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockEmbed))
          return 3;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockSubtitle))
          return 4;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockVideo))
          return 5;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockPullquote))
          return 6;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockBlockquote))
          return 7;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockSlideshow))
          return 8;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockPhoto))
          return 9;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockAuthorDate))
          return 10;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockTitle))
          return 11;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockList))
          return 12;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockFooter))
          return 13;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockPreformatted))
          return 14;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockSubheader))
          return 15;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockEmbedPost))
          return 16;
        if ((paramPageBlock instanceof TLRPC.TL_pageBlockCollage))
          return 17;
      }
      while (!(paramPageBlock instanceof TLRPC.TL_pageBlockCover));
      return getTypeForBlock(paramPageBlock.cover);
    }

    public int getItemCount()
    {
      if ((ArticleViewer.this.currentPage != null) && (ArticleViewer.this.currentPage.cached_page != null))
        return ArticleViewer.this.blocks.size() + 1;
      return 0;
    }

    public int getItemViewType(int paramInt)
    {
      if (paramInt == ArticleViewer.this.blocks.size())
        return 90;
      return getTypeForBlock((TLRPC.PageBlock)ArticleViewer.this.blocks.get(paramInt));
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool3 = true;
      boolean bool2 = true;
      TLRPC.PageBlock localPageBlock;
      if (paramInt < ArticleViewer.this.blocks.size())
      {
        localPageBlock = (TLRPC.PageBlock)ArticleViewer.this.blocks.get(paramInt);
        if (!(localPageBlock instanceof TLRPC.TL_pageBlockCover))
          break label522;
      }
      label388: label522: for (Object localObject = localPageBlock.cover; ; localObject = localPageBlock)
      {
        boolean bool1;
        switch (paramViewHolder.getItemViewType())
        {
        default:
          return;
        case 0:
          ((ArticleViewer.BlockParagraphCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockParagraph)localObject);
          return;
        case 1:
          ((ArticleViewer.BlockHeaderCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockHeader)localObject);
          return;
        case 2:
          paramViewHolder = (ArticleViewer.BlockDividerCell)paramViewHolder.itemView;
          return;
        case 3:
          ((ArticleViewer.BlockEmbedCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockEmbed)localObject);
          return;
        case 4:
          ((ArticleViewer.BlockSubtitleCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockSubtitle)localObject);
          return;
        case 5:
          paramViewHolder = (ArticleViewer.BlockVideoCell)paramViewHolder.itemView;
          localObject = (TLRPC.TL_pageBlockVideo)localObject;
          if (paramInt == 0)
          {
            bool1 = true;
            if (paramInt != ArticleViewer.this.blocks.size() - 1)
              break label272;
          }
          while (true)
          {
            paramViewHolder.setBlock((TLRPC.TL_pageBlockVideo)localObject, bool1, bool2);
            paramViewHolder.setParentBlock(localPageBlock);
            return;
            bool1 = false;
            break;
            bool2 = false;
          }
        case 6:
          ((ArticleViewer.BlockPullquoteCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockPullquote)localObject);
          return;
        case 7:
          ((ArticleViewer.BlockBlockquoteCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockBlockquote)localObject);
          return;
        case 8:
          ((ArticleViewer.BlockSlideshowCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockSlideshow)localObject);
          return;
        case 9:
          paramViewHolder = (ArticleViewer.BlockPhotoCell)paramViewHolder.itemView;
          localObject = (TLRPC.TL_pageBlockPhoto)localObject;
          if (paramInt == 0)
          {
            bool1 = true;
            if (paramInt != ArticleViewer.this.blocks.size() - 1)
              break label388;
          }
          for (bool2 = bool3; ; bool2 = false)
          {
            paramViewHolder.setBlock((TLRPC.TL_pageBlockPhoto)localObject, bool1, bool2);
            paramViewHolder.setParentBlock(localPageBlock);
            return;
            bool1 = false;
            break;
          }
        case 10:
          ((ArticleViewer.BlockAuthorDateCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockAuthorDate)localObject);
          return;
        case 11:
          ((ArticleViewer.BlockTitleCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockTitle)localObject);
          return;
        case 12:
          ((ArticleViewer.BlockListCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockList)localObject);
          return;
        case 13:
          ((ArticleViewer.BlockFooterCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockFooter)localObject);
          return;
        case 14:
          ((ArticleViewer.BlockPreformattedCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockPreformatted)localObject);
          return;
        case 15:
          ((ArticleViewer.BlockSubheaderCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockSubheader)localObject);
          return;
        case 16:
          label272: ((ArticleViewer.BlockEmbedPostCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockEmbedPost)localObject);
          return;
        case 17:
        }
        ((ArticleViewer.BlockCollageCell)paramViewHolder.itemView).setBlock((TLRPC.TL_pageBlockCollage)localObject);
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new FrameLayout(this.context)
        {
          protected void onMeasure(int paramInt1, int paramInt2)
          {
            super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0F), 1073741824));
          }
        };
        TextView localTextView = new TextView(this.context);
        paramViewGroup.addView(localTextView, LayoutHelper.createFrame(-1, 34.0F, 51, 0.0F, 10.0F, 0.0F, 0.0F));
        localTextView.setTextColor(-8879475);
        localTextView.setBackgroundColor(-1183760);
        localTextView.setText(LocaleController.getString("PreviewFeedback", 2131166302));
        localTextView.setTextSize(1, 12.0F);
        localTextView.setGravity(17);
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      }
      while (true)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new ArticleViewer.BlockParagraphCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockHeaderCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockDividerCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockEmbedCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockSubtitleCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockVideoCell(ArticleViewer.this, this.context, 0);
        continue;
        paramViewGroup = new ArticleViewer.BlockPullquoteCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockBlockquoteCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockSlideshowCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockPhotoCell(ArticleViewer.this, this.context, 0);
        continue;
        paramViewGroup = new ArticleViewer.BlockAuthorDateCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockTitleCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockListCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockFooterCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockPreformattedCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockSubheaderCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockEmbedPostCell(ArticleViewer.this, this.context);
        continue;
        paramViewGroup = new ArticleViewer.BlockCollageCell(ArticleViewer.this, this.context);
      }
    }
  }

  private class WindowView extends FrameLayout
  {
    private float alpha;
    private Runnable attachRunnable;
    private boolean closeAnimationInProgress;
    private float innerTranslationX;
    private boolean maybeStartTracking;
    private boolean selfLayout;
    private boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private VelocityTracker tracker;

    public WindowView(Context arg2)
    {
      super();
    }

    private void prepareForMoving(MotionEvent paramMotionEvent)
    {
      this.maybeStartTracking = false;
      this.startedTracking = true;
      this.startedTrackingX = (int)paramMotionEvent.getX();
    }

    protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
    {
      int i = getMeasuredWidth();
      int j = (int)this.innerTranslationX;
      int k = paramCanvas.save();
      paramCanvas.clipRect(j, 0, i, getHeight());
      boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
      paramCanvas.restoreToCount(k);
      if ((j != 0) && (paramView == ArticleViewer.this.containerView))
      {
        float f2 = Math.min(0.8F, (i - j) / i);
        float f1 = f2;
        if (f2 < 0.0F)
          f1 = 0.0F;
        ArticleViewer.this.scrimPaint.setColor((int)(f1 * 153.0F) << 24);
        paramCanvas.drawRect(0.0F, 0.0F, j, getHeight(), ArticleViewer.this.scrimPaint);
        f1 = Math.max(0.0F, Math.min((i - j) / AndroidUtilities.dp(20.0F), 1.0F));
        ArticleViewer.this.layerShadowDrawable.setBounds(j - ArticleViewer.this.layerShadowDrawable.getIntrinsicWidth(), paramView.getTop(), j, paramView.getBottom());
        ArticleViewer.this.layerShadowDrawable.setAlpha((int)(f1 * 255.0F));
        ArticleViewer.this.layerShadowDrawable.draw(paramCanvas);
      }
      return bool;
    }

    public float getAlpha()
    {
      return this.alpha;
    }

    public float getInnerTranslationX()
    {
      return this.innerTranslationX;
    }

    public boolean handleTouchEvent(MotionEvent paramMotionEvent)
    {
      if ((!ArticleViewer.this.isPhotoVisible) && (!this.closeAnimationInProgress) && (ArticleViewer.this.fullscreenVideoContainer.getVisibility() != 0))
      {
        if ((paramMotionEvent != null) && (paramMotionEvent.getAction() == 0) && (!this.startedTracking) && (!this.maybeStartTracking))
        {
          this.startedTrackingPointerId = paramMotionEvent.getPointerId(0);
          this.maybeStartTracking = true;
          this.startedTrackingX = (int)paramMotionEvent.getX();
          this.startedTrackingY = (int)paramMotionEvent.getY();
          if (this.tracker != null)
            this.tracker.clear();
        }
        while (true)
        {
          return this.startedTracking;
          if ((paramMotionEvent != null) && (paramMotionEvent.getAction() == 2) && (paramMotionEvent.getPointerId(0) == this.startedTrackingPointerId))
          {
            if (this.tracker == null)
              this.tracker = VelocityTracker.obtain();
            int i = Math.max(0, (int)(paramMotionEvent.getX() - this.startedTrackingX));
            int j = Math.abs((int)paramMotionEvent.getY() - this.startedTrackingY);
            this.tracker.addMovement(paramMotionEvent);
            if ((this.maybeStartTracking) && (!this.startedTracking) && (i >= AndroidUtilities.getPixelsInCM(0.4F, true)) && (Math.abs(i) / 3 > j))
            {
              prepareForMoving(paramMotionEvent);
              continue;
            }
            if (!this.startedTracking)
              continue;
            ArticleViewer.this.containerView.setTranslationX(i);
            setInnerTranslationX(i);
            continue;
          }
          if ((paramMotionEvent != null) && (paramMotionEvent.getPointerId(0) == this.startedTrackingPointerId) && ((paramMotionEvent.getAction() == 3) || (paramMotionEvent.getAction() == 1) || (paramMotionEvent.getAction() == 6)))
          {
            if (this.tracker == null)
              this.tracker = VelocityTracker.obtain();
            this.tracker.computeCurrentVelocity(1000);
            float f1;
            float f2;
            if (!this.startedTracking)
            {
              f1 = this.tracker.getXVelocity();
              f2 = this.tracker.getYVelocity();
              if ((f1 >= 3500.0F) && (f1 > Math.abs(f2)))
                prepareForMoving(paramMotionEvent);
            }
            boolean bool;
            if (this.startedTracking)
            {
              f1 = ArticleViewer.this.containerView.getX();
              paramMotionEvent = new AnimatorSet();
              f2 = this.tracker.getXVelocity();
              float f3 = this.tracker.getYVelocity();
              if ((f1 < ArticleViewer.this.containerView.getMeasuredWidth() / 3.0F) && ((f2 < 3500.0F) || (f2 < f3)))
              {
                bool = true;
                label449: if (bool)
                  break label613;
                f1 = ArticleViewer.this.containerView.getMeasuredWidth() - f1;
                paramMotionEvent.playTogether(new Animator[] { ObjectAnimator.ofFloat(ArticleViewer.access$800(ArticleViewer.this), "translationX", new float[] { ArticleViewer.access$800(ArticleViewer.this).getMeasuredWidth() }), ObjectAnimator.ofFloat(this, "innerTranslationX", new float[] { ArticleViewer.access$800(ArticleViewer.this).getMeasuredWidth() }) });
                label534: paramMotionEvent.setDuration(Math.max((int)(f1 * (200.0F / ArticleViewer.this.containerView.getMeasuredWidth())), 50));
                paramMotionEvent.addListener(new AnimatorListenerAdapter(bool)
                {
                  public void onAnimationEnd(Animator paramAnimator)
                  {
                    if (!this.val$backAnimation)
                    {
                      ArticleViewer.this.saveCurrentPagePosition();
                      ArticleViewer.this.onClosed();
                    }
                    ArticleViewer.WindowView.access$2202(ArticleViewer.WindowView.this, false);
                    ArticleViewer.WindowView.access$2302(ArticleViewer.WindowView.this, false);
                  }
                });
                paramMotionEvent.start();
                this.closeAnimationInProgress = true;
              }
            }
            while (true)
            {
              if (this.tracker == null)
                break label673;
              this.tracker.recycle();
              this.tracker = null;
              break;
              bool = false;
              break label449;
              label613: paramMotionEvent.playTogether(new Animator[] { ObjectAnimator.ofFloat(ArticleViewer.access$800(ArticleViewer.this), "translationX", new float[] { 0.0F }), ObjectAnimator.ofFloat(this, "innerTranslationX", new float[] { 0.0F }) });
              break label534;
              this.maybeStartTracking = false;
              this.startedTracking = false;
            }
            label673: continue;
          }
          if (paramMotionEvent != null)
            continue;
          this.maybeStartTracking = false;
          this.startedTracking = false;
          if (this.tracker == null)
            continue;
          this.tracker.recycle();
          this.tracker = null;
        }
      }
      return false;
    }

    protected void onAttachedToWindow()
    {
      super.onAttachedToWindow();
      ArticleViewer.access$1302(ArticleViewer.this, true);
    }

    protected void onDetachedFromWindow()
    {
      super.onDetachedFromWindow();
      ArticleViewer.access$1302(ArticleViewer.this, false);
    }

    protected void onDraw(Canvas paramCanvas)
    {
      paramCanvas.drawRect(this.innerTranslationX, 0.0F, getMeasuredWidth(), getMeasuredHeight(), ArticleViewer.this.backgroundPaint);
    }

    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
    {
      return (!ArticleViewer.this.collapsed) && ((handleTouchEvent(paramMotionEvent)) || (super.onInterceptTouchEvent(paramMotionEvent)));
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (this.selfLayout)
        return;
      WindowInsets localWindowInsets;
      int i;
      if ((Build.VERSION.SDK_INT >= 21) && (ArticleViewer.this.lastInsets != null))
      {
        localWindowInsets = (WindowInsets)ArticleViewer.this.lastInsets;
        i = localWindowInsets.getSystemWindowInsetLeft();
        if (localWindowInsets.getSystemWindowInsetRight() != 0)
          ArticleViewer.this.barBackground.layout(paramInt3 - paramInt1 - localWindowInsets.getSystemWindowInsetRight(), 0, paramInt3 - paramInt1, paramInt4 - paramInt2);
      }
      for (paramInt1 = i; ; paramInt1 = 0)
      {
        ArticleViewer.this.containerView.layout(paramInt1, 0, ArticleViewer.this.containerView.getMeasuredWidth() + paramInt1, ArticleViewer.this.containerView.getMeasuredHeight());
        ArticleViewer.this.photoContainerView.layout(paramInt1, 0, ArticleViewer.this.photoContainerView.getMeasuredWidth() + paramInt1, ArticleViewer.this.photoContainerView.getMeasuredHeight());
        ArticleViewer.this.photoContainerBackground.layout(paramInt1, 0, ArticleViewer.this.photoContainerBackground.getMeasuredWidth() + paramInt1, ArticleViewer.this.photoContainerBackground.getMeasuredHeight());
        ArticleViewer.this.fullscreenVideoContainer.layout(paramInt1, 0, ArticleViewer.this.fullscreenVideoContainer.getMeasuredWidth() + paramInt1, ArticleViewer.this.fullscreenVideoContainer.getMeasuredHeight());
        ArticleViewer.this.animatingImageView.layout(0, 0, ArticleViewer.this.animatingImageView.getMeasuredWidth(), ArticleViewer.this.animatingImageView.getMeasuredHeight());
        return;
        if (localWindowInsets.getSystemWindowInsetLeft() != 0)
        {
          ArticleViewer.this.barBackground.layout(0, 0, localWindowInsets.getSystemWindowInsetLeft(), paramInt4 - paramInt2);
          break;
        }
        ArticleViewer.this.barBackground.layout(0, paramInt4 - paramInt2 - localWindowInsets.getStableInsetBottom(), paramInt3 - paramInt1, paramInt4 - paramInt2);
        break;
      }
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      paramInt1 = View.MeasureSpec.getSize(paramInt2);
      Object localObject;
      if ((Build.VERSION.SDK_INT >= 21) && (ArticleViewer.this.lastInsets != null))
      {
        setMeasuredDimension(i, paramInt1);
        localObject = (WindowInsets)ArticleViewer.this.lastInsets;
        paramInt2 = paramInt1;
        if (AndroidUtilities.incorrectDisplaySizeFix)
        {
          paramInt2 = paramInt1;
          if (paramInt1 > AndroidUtilities.displaySize.y)
            paramInt2 = AndroidUtilities.displaySize.y;
          paramInt2 += AndroidUtilities.statusBarHeight;
        }
        paramInt2 -= ((WindowInsets)localObject).getSystemWindowInsetBottom();
        paramInt1 = i - (((WindowInsets)localObject).getSystemWindowInsetRight() + ((WindowInsets)localObject).getSystemWindowInsetLeft());
        if (((WindowInsets)localObject).getSystemWindowInsetRight() != 0)
        {
          ArticleViewer.this.barBackground.measure(View.MeasureSpec.makeMeasureSpec(((WindowInsets)localObject).getSystemWindowInsetRight(), 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824));
          i = paramInt2;
          paramInt2 = paramInt1;
        }
      }
      while (true)
      {
        ArticleViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
        ArticleViewer.this.photoContainerView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
        ArticleViewer.this.photoContainerBackground.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
        ArticleViewer.this.fullscreenVideoContainer.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
        localObject = ArticleViewer.this.animatingImageView.getLayoutParams();
        ArticleViewer.this.animatingImageView.measure(View.MeasureSpec.makeMeasureSpec(((ViewGroup.LayoutParams)localObject).width, -2147483648), View.MeasureSpec.makeMeasureSpec(((ViewGroup.LayoutParams)localObject).height, -2147483648));
        return;
        if (((WindowInsets)localObject).getSystemWindowInsetLeft() != 0)
        {
          ArticleViewer.this.barBackground.measure(View.MeasureSpec.makeMeasureSpec(((WindowInsets)localObject).getSystemWindowInsetLeft(), 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824));
          break;
        }
        ArticleViewer.this.barBackground.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(((WindowInsets)localObject).getSystemWindowInsetBottom(), 1073741824));
        break;
        setMeasuredDimension(i, paramInt1);
        paramInt2 = i;
        i = paramInt1;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (!ArticleViewer.this.collapsed) && ((handleTouchEvent(paramMotionEvent)) || (super.onTouchEvent(paramMotionEvent)));
    }

    public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
    {
      handleTouchEvent(null);
      super.requestDisallowInterceptTouchEvent(paramBoolean);
    }

    public void setAlpha(float paramFloat)
    {
      ArticleViewer.this.backgroundPaint.setAlpha((int)(255.0F * paramFloat));
      this.alpha = paramFloat;
      DrawerLayoutContainer localDrawerLayoutContainer;
      if ((ArticleViewer.this.parentActivity instanceof LaunchActivity))
      {
        localDrawerLayoutContainer = ((LaunchActivity)ArticleViewer.this.parentActivity).drawerLayoutContainer;
        if ((ArticleViewer.this.isVisible) && (this.alpha == 1.0F) && (this.innerTranslationX == 0.0F))
          break label87;
      }
      label87: for (boolean bool = true; ; bool = false)
      {
        localDrawerLayoutContainer.setAllowDrawContent(bool);
        invalidate();
        return;
      }
    }

    public void setInnerTranslationX(float paramFloat)
    {
      this.innerTranslationX = paramFloat;
      DrawerLayoutContainer localDrawerLayoutContainer;
      if ((ArticleViewer.this.parentActivity instanceof LaunchActivity))
      {
        localDrawerLayoutContainer = ((LaunchActivity)ArticleViewer.this.parentActivity).drawerLayoutContainer;
        if ((ArticleViewer.this.isVisible) && (this.alpha == 1.0F) && (this.innerTranslationX == 0.0F))
          break label72;
      }
      label72: for (boolean bool = true; ; bool = false)
      {
        localDrawerLayoutContainer.setAllowDrawContent(bool);
        invalidate();
        return;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ArticleViewer
 * JD-Core Version:    0.6.0
 */