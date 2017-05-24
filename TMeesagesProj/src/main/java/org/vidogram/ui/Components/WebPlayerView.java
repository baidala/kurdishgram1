package org.vidogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.Bitmaps;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.PhotoSize;

@TargetApi(16)
public class WebPlayerView extends ViewGroup
  implements AudioManager.OnAudioFocusChangeListener, VideoPlayer.VideoPlayerDelegate
{
  private static final int AUDIO_FOCUSED = 2;
  private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
  private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
  private static final Pattern aparatFileListPattern;
  private static final Pattern aparatIdRegex;
  private static final Pattern coubIdRegex;
  private static final String exprName = "[a-zA-Z_$][a-zA-Z_$0-9]*";
  private static final Pattern exprParensPattern;
  private static final Pattern jsPattern;
  private static final Pattern playerIdPattern;
  private static final Pattern sigPattern;
  private static final Pattern sigPattern2;
  private static final Pattern stmtReturnPattern;
  private static final Pattern stmtVarPattern;
  private static final Pattern stsPattern;
  private static final Pattern vimeoIdRegex;
  private static final Pattern youtubeIdRegex = Pattern.compile("(?:youtube(?:-nocookie)?\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?)/|\\S*?[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})");
  private boolean allowInlineAnimation;
  private AspectRatioFrameLayout aspectRatioFrameLayout;
  private int audioFocus;
  private Paint backgroundPaint;
  private TextureView changedTextureView;
  private boolean changingTextureView;
  private ControlsView controlsView;
  private float currentAlpha;
  private Bitmap currentBitmap;
  private AsyncTask currentTask;
  private WebPlayerViewDelegate delegate;
  private boolean drawImage;
  private boolean firstFrameRendered;
  private ImageView fullscreenButton;
  private boolean hasAudioFocus;
  private boolean inFullscreen;
  private boolean initFailed;
  private boolean initied;
  private ImageView inlineButton;
  private String interfaceName;
  private boolean isAutoplay;
  private boolean isCompleted;
  private boolean isInline;
  private boolean isLoading;
  private long lastUpdateTime;
  private String playAudioType;
  private String playAudioUrl;
  private ImageView playButton;
  private String playVideoType;
  private String playVideoUrl;
  private AnimatorSet progressAnimation;
  private Runnable progressRunnable;
  private RadialProgressView progressView;
  private boolean resumeAudioOnFocusGain;
  private int seekToTime;
  private ImageView shareButton;
  private TextureView.SurfaceTextureListener surfaceTextureListener;
  private Runnable switchToInlineRunnable;
  private boolean switchingInlineMode;
  private ImageView textureImageView;
  private TextureView textureView;
  private ViewGroup textureViewContainer;
  private VideoPlayer videoPlayer;
  private int waitingForFirstTextureUpload;
  private WebView webView;

  static
  {
    vimeoIdRegex = Pattern.compile("https?://(?:(?:www|(player))\\.)?vimeo(pro)?\\.com/(?!(?:channels|album)/[^/?#]+/?(?:$|[?#])|[^/]+/review/|ondemand/)(?:.*?/)?(?:(?:play_redirect_hls|moogaloop\\.swf)\\?clip_id=)?(?:videos?/)?([0-9]+)(?:/[\\da-f]+)?/?(?:[?&].*)?(?:[#].*)?$");
    coubIdRegex = Pattern.compile("(?:coub:|https?://(?:coub\\.com/(?:view|embed|coubs)/|c-cdn\\.coub\\.com/fb-player\\.swf\\?.*\\bcoub(?:ID|id)=))([\\da-z]+)");
    aparatIdRegex = Pattern.compile("^https?://(?:www\\.)?aparat\\.com/(?:v/|video/video/embed/videohash/)([a-zA-Z0-9]+)");
    aparatFileListPattern = Pattern.compile("fileList\\s*=\\s*JSON\\.parse\\('([^']+)'\\)");
    stsPattern = Pattern.compile("\"sts\"\\s*:\\s*(\\d+)");
    jsPattern = Pattern.compile("\"assets\":.+?\"js\":\\s*(\"[^\"]+\")");
    sigPattern = Pattern.compile("\\.sig\\|\\|([a-zA-Z0-9$]+)\\(");
    sigPattern2 = Pattern.compile("[\"']signature[\"']\\s*,\\s*([a-zA-Z0-9$]+)\\(");
    stmtVarPattern = Pattern.compile("var\\s");
    stmtReturnPattern = Pattern.compile("return(?:\\s+|$)");
    exprParensPattern = Pattern.compile("[()]");
    playerIdPattern = Pattern.compile(".*?-([a-zA-Z0-9_-]+)(?:/watch_as3|/html5player(?:-new)?|/base)?\\.([a-z]+)$");
  }

  @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
  public WebPlayerView(Context paramContext, boolean paramBoolean1, boolean paramBoolean2, WebPlayerViewDelegate paramWebPlayerViewDelegate)
  {
    super(paramContext);
    boolean bool;
    if (Build.VERSION.SDK_INT >= 21)
    {
      bool = true;
      this.allowInlineAnimation = bool;
      this.backgroundPaint = new Paint();
      this.progressRunnable = new Runnable()
      {
        public void run()
        {
          if ((WebPlayerView.this.videoPlayer == null) || (!WebPlayerView.this.videoPlayer.isPlaying()))
            return;
          WebPlayerView.this.controlsView.setProgress((int)(WebPlayerView.this.videoPlayer.getCurrentPosition() / 1000L));
          WebPlayerView.this.controlsView.setBufferedProgress((int)(WebPlayerView.this.videoPlayer.getBufferedPosition() / 1000L), WebPlayerView.this.videoPlayer.getBufferedPercentage());
          AndroidUtilities.runOnUIThread(WebPlayerView.this.progressRunnable, 1000L);
        }
      };
      this.surfaceTextureListener = new TextureView.SurfaceTextureListener()
      {
        public void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
        {
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture paramSurfaceTexture)
        {
          if (WebPlayerView.this.changingTextureView)
          {
            if (WebPlayerView.this.switchingInlineMode)
              WebPlayerView.access$2902(WebPlayerView.this, 2);
            WebPlayerView.this.textureView.setSurfaceTexture(paramSurfaceTexture);
            WebPlayerView.this.textureView.setVisibility(0);
            WebPlayerView.access$2702(WebPlayerView.this, false);
            return false;
          }
          return true;
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
        {
        }

        public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture)
        {
          if (WebPlayerView.this.waitingForFirstTextureUpload == 1)
          {
            WebPlayerView.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
            {
              public boolean onPreDraw()
              {
                WebPlayerView.this.changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (WebPlayerView.this.textureImageView != null)
                {
                  WebPlayerView.this.textureImageView.setVisibility(4);
                  WebPlayerView.this.textureImageView.setImageDrawable(null);
                  if (WebPlayerView.this.currentBitmap != null)
                  {
                    WebPlayerView.this.currentBitmap.recycle();
                    WebPlayerView.access$3302(WebPlayerView.this, null);
                  }
                }
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    WebPlayerView.this.delegate.onInlineSurfaceTextureReady();
                  }
                });
                WebPlayerView.access$2902(WebPlayerView.this, 0);
                return true;
              }
            });
            WebPlayerView.this.changedTextureView.invalidate();
          }
        }
      };
      this.switchToInlineRunnable = new Runnable()
      {
        public void run()
        {
          WebPlayerView.access$2802(WebPlayerView.this, false);
          if (WebPlayerView.this.currentBitmap != null)
          {
            WebPlayerView.this.currentBitmap.recycle();
            WebPlayerView.access$3302(WebPlayerView.this, null);
          }
          WebPlayerView.access$2702(WebPlayerView.this, true);
          if (WebPlayerView.this.textureImageView != null);
          try
          {
            WebPlayerView.access$3302(WebPlayerView.this, Bitmaps.createBitmap(WebPlayerView.this.textureView.getWidth(), WebPlayerView.this.textureView.getHeight(), Bitmap.Config.ARGB_8888));
            WebPlayerView.this.textureView.getBitmap(WebPlayerView.this.currentBitmap);
            if (WebPlayerView.this.currentBitmap != null)
            {
              WebPlayerView.this.textureImageView.setVisibility(0);
              WebPlayerView.this.textureImageView.setImageBitmap(WebPlayerView.this.currentBitmap);
              WebPlayerView.access$3502(WebPlayerView.this, true);
              WebPlayerView.this.updatePlayButton();
              WebPlayerView.this.updateShareButton();
              WebPlayerView.this.updateFullscreenButton();
              WebPlayerView.this.updateInlineButton();
              ViewGroup localViewGroup = (ViewGroup)WebPlayerView.this.controlsView.getParent();
              if (localViewGroup != null)
                localViewGroup.removeView(WebPlayerView.this.controlsView);
              WebPlayerView.access$3102(WebPlayerView.this, WebPlayerView.this.delegate.onSwitchInlineMode(WebPlayerView.this.controlsView, WebPlayerView.this.isInline, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.aspectRatioFrameLayout.getVideoRotation(), WebPlayerView.this.allowInlineAnimation));
              WebPlayerView.this.changedTextureView.setVisibility(4);
              localViewGroup = (ViewGroup)WebPlayerView.this.textureView.getParent();
              if (localViewGroup != null)
                localViewGroup.removeView(WebPlayerView.this.textureView);
              WebPlayerView.this.controlsView.show(false, false);
              return;
            }
          }
          catch (Throwable localThrowable)
          {
            while (true)
            {
              if (WebPlayerView.this.currentBitmap != null)
              {
                WebPlayerView.this.currentBitmap.recycle();
                WebPlayerView.access$3302(WebPlayerView.this, null);
              }
              FileLog.e(localThrowable);
              continue;
              WebPlayerView.this.textureImageView.setImageDrawable(null);
            }
          }
        }
      };
      setWillNotDraw(false);
      this.delegate = paramWebPlayerViewDelegate;
      this.backgroundPaint.setColor(-16777216);
      this.aspectRatioFrameLayout = new AspectRatioFrameLayout(paramContext)
      {
        protected void onMeasure(int paramInt1, int paramInt2)
        {
          super.onMeasure(paramInt1, paramInt2);
          if (WebPlayerView.this.textureViewContainer != null)
          {
            ViewGroup.LayoutParams localLayoutParams = WebPlayerView.this.textureView.getLayoutParams();
            localLayoutParams.width = getMeasuredWidth();
            localLayoutParams.height = getMeasuredHeight();
            if (WebPlayerView.this.textureImageView != null)
            {
              localLayoutParams = WebPlayerView.this.textureImageView.getLayoutParams();
              localLayoutParams.width = getMeasuredWidth();
              localLayoutParams.height = getMeasuredHeight();
            }
          }
        }
      };
      addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
      this.interfaceName = "JavaScriptInterface";
      this.webView = new WebView(paramContext);
      this.webView.addJavascriptInterface(new JavaScriptInterface(new CallJavaResultInterface()
      {
        public void jsCallFinished(String paramString)
        {
          if ((WebPlayerView.this.currentTask != null) && (!WebPlayerView.this.currentTask.isCancelled()) && ((WebPlayerView.this.currentTask instanceof WebPlayerView.YoutubeVideoTask)))
            WebPlayerView.YoutubeVideoTask.access$5000((WebPlayerView.YoutubeVideoTask)WebPlayerView.this.currentTask, paramString);
        }
      }), this.interfaceName);
      paramWebPlayerViewDelegate = this.webView.getSettings();
      paramWebPlayerViewDelegate.setJavaScriptEnabled(true);
      paramWebPlayerViewDelegate.setDefaultTextEncodingName("utf-8");
      this.textureViewContainer = this.delegate.getTextureViewContainer();
      this.textureView = new TextureView(paramContext);
      this.textureView.setPivotX(0.0F);
      this.textureView.setPivotY(0.0F);
      if (this.textureViewContainer == null)
        break label691;
      this.textureViewContainer.addView(this.textureView);
      label246: if ((this.allowInlineAnimation) && (this.textureViewContainer != null))
      {
        this.textureImageView = new ImageView(paramContext);
        this.textureImageView.setBackgroundColor(-65536);
        this.textureImageView.setPivotX(0.0F);
        this.textureImageView.setPivotY(0.0F);
        this.textureImageView.setVisibility(4);
        this.textureViewContainer.addView(this.textureImageView);
      }
      this.videoPlayer = new VideoPlayer();
      this.videoPlayer.setDelegate(this);
      this.videoPlayer.setTextureView(this.textureView);
      this.controlsView = new ControlsView(paramContext);
      if (this.textureViewContainer == null)
        break label712;
      this.textureViewContainer.addView(this.controlsView);
    }
    while (true)
    {
      this.progressView = new RadialProgressView(paramContext);
      this.progressView.setProgressColor(-1);
      addView(this.progressView, LayoutHelper.createFrame(48, 48, 17));
      this.fullscreenButton = new ImageView(paramContext);
      this.fullscreenButton.setScaleType(ImageView.ScaleType.CENTER);
      this.controlsView.addView(this.fullscreenButton, LayoutHelper.createFrame(56, 56.0F, 85, 0.0F, 0.0F, 0.0F, 5.0F));
      this.fullscreenButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if ((!WebPlayerView.this.initied) || (WebPlayerView.this.changingTextureView) || (WebPlayerView.this.switchingInlineMode) || (!WebPlayerView.this.firstFrameRendered))
            return;
          paramView = WebPlayerView.this;
          if (!WebPlayerView.this.inFullscreen);
          for (boolean bool = true; ; bool = false)
          {
            WebPlayerView.access$4302(paramView, bool);
            WebPlayerView.this.updateFullscreenState(true);
            return;
          }
        }
      });
      this.playButton = new ImageView(paramContext);
      this.playButton.setScaleType(ImageView.ScaleType.CENTER);
      this.controlsView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
      this.playButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if ((!WebPlayerView.this.initied) || (WebPlayerView.this.playVideoUrl == null))
            return;
          if (!WebPlayerView.this.videoPlayer.isPlayerPrepared())
            WebPlayerView.this.preparePlayer();
          if (WebPlayerView.this.videoPlayer.isPlaying())
            WebPlayerView.this.videoPlayer.pause();
          while (true)
          {
            WebPlayerView.this.updatePlayButton();
            return;
            WebPlayerView.access$5202(WebPlayerView.this, false);
            WebPlayerView.this.videoPlayer.play();
          }
        }
      });
      if (paramBoolean1)
      {
        this.inlineButton = new ImageView(paramContext);
        this.inlineButton.setScaleType(ImageView.ScaleType.CENTER);
        this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
        this.inlineButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if ((WebPlayerView.this.textureView == null) || (!WebPlayerView.this.delegate.checkInlinePermissons()) || (WebPlayerView.this.changingTextureView) || (WebPlayerView.this.switchingInlineMode) || (!WebPlayerView.this.firstFrameRendered))
              return;
            WebPlayerView.access$2802(WebPlayerView.this, true);
            if (!WebPlayerView.this.isInline)
            {
              WebPlayerView.access$4302(WebPlayerView.this, false);
              WebPlayerView.this.delegate.prepareToSwitchInlineMode(true, WebPlayerView.this.switchToInlineRunnable, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.allowInlineAnimation);
              return;
            }
            paramView = (ViewGroup)WebPlayerView.this.aspectRatioFrameLayout.getParent();
            if (paramView != WebPlayerView.this)
            {
              if (paramView != null)
                paramView.removeView(WebPlayerView.this.aspectRatioFrameLayout);
              WebPlayerView.this.addView(WebPlayerView.this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
              WebPlayerView.this.aspectRatioFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(WebPlayerView.this.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(WebPlayerView.this.getMeasuredHeight() - AndroidUtilities.dp(10.0F), 1073741824));
            }
            if (WebPlayerView.this.currentBitmap != null)
            {
              WebPlayerView.this.currentBitmap.recycle();
              WebPlayerView.access$3302(WebPlayerView.this, null);
            }
            WebPlayerView.access$2702(WebPlayerView.this, true);
            WebPlayerView.access$3502(WebPlayerView.this, false);
            WebPlayerView.this.updatePlayButton();
            WebPlayerView.this.updateShareButton();
            WebPlayerView.this.updateFullscreenButton();
            WebPlayerView.this.updateInlineButton();
            WebPlayerView.this.textureView.setVisibility(4);
            if (WebPlayerView.this.textureViewContainer != null)
            {
              WebPlayerView.this.textureViewContainer.addView(WebPlayerView.this.textureView);
              paramView = (ViewGroup)WebPlayerView.this.controlsView.getParent();
              if (paramView != WebPlayerView.this)
              {
                if (paramView != null)
                  paramView.removeView(WebPlayerView.this.controlsView);
                if (WebPlayerView.this.textureViewContainer == null)
                  break label462;
                WebPlayerView.this.textureViewContainer.addView(WebPlayerView.this.controlsView);
              }
            }
            while (true)
            {
              WebPlayerView.this.controlsView.show(false, false);
              WebPlayerView.this.delegate.prepareToSwitchInlineMode(false, null, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.allowInlineAnimation);
              return;
              WebPlayerView.this.aspectRatioFrameLayout.addView(WebPlayerView.this.textureView);
              break;
              label462: WebPlayerView.this.addView(WebPlayerView.this.controlsView, 1);
            }
          }
        });
      }
      if (paramBoolean2)
      {
        this.shareButton = new ImageView(paramContext);
        this.shareButton.setScaleType(ImageView.ScaleType.CENTER);
        this.shareButton.setImageResource(2130837837);
        this.controlsView.addView(this.shareButton, LayoutHelper.createFrame(56, 48, 53));
        this.shareButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (WebPlayerView.this.delegate != null)
              WebPlayerView.this.delegate.onSharePressed();
          }
        });
      }
      updatePlayButton();
      updateFullscreenButton();
      updateInlineButton();
      updateShareButton();
      return;
      bool = false;
      break;
      label691: this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
      break label246;
      label712: addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0F));
    }
  }

  private void checkAudioFocus()
  {
    if (!this.hasAudioFocus)
    {
      AudioManager localAudioManager = (AudioManager)ApplicationLoader.applicationContext.getSystemService("audio");
      this.hasAudioFocus = true;
      if (localAudioManager.requestAudioFocus(this, 3, 1) == 1)
        this.audioFocus = 2;
    }
  }

  private View getControlView()
  {
    return this.controlsView;
  }

  private View getProgressView()
  {
    return this.progressView;
  }

  private void onInitFailed()
  {
    if (this.controlsView.getParent() != this)
      this.controlsView.setVisibility(8);
    this.delegate.onInitFailed();
  }

  private void preparePlayer()
  {
    if (this.playVideoUrl == null)
      return;
    if ((this.playVideoUrl != null) && (this.playAudioUrl != null))
    {
      this.videoPlayer.preparePlayerLoop(Uri.parse(this.playVideoUrl), this.playVideoType, Uri.parse(this.playAudioUrl), this.playAudioType);
      label51: this.videoPlayer.setPlayWhenReady(this.isAutoplay);
      this.isLoading = false;
      if (this.videoPlayer.getDuration() == -9223372036854775807L)
        break label165;
      this.controlsView.setDuration((int)(this.videoPlayer.getDuration() / 1000L));
    }
    while (true)
    {
      updateFullscreenButton();
      updateShareButton();
      updateInlineButton();
      this.controlsView.invalidate();
      if (this.seekToTime == -1)
        break;
      this.videoPlayer.seekTo(this.seekToTime * 1000);
      return;
      this.videoPlayer.preparePlayer(Uri.parse(this.playVideoUrl), this.playVideoType);
      break label51;
      label165: this.controlsView.setDuration(0);
    }
  }

  private void showProgress(boolean paramBoolean1, boolean paramBoolean2)
  {
    float f = 1.0F;
    if (paramBoolean2)
    {
      if (this.progressAnimation != null)
        this.progressAnimation.cancel();
      this.progressAnimation = new AnimatorSet();
      localObject = this.progressAnimation;
      RadialProgressView localRadialProgressView = this.progressView;
      if (paramBoolean1);
      while (true)
      {
        ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(localRadialProgressView, "alpha", new float[] { f }) });
        this.progressAnimation.setDuration(150L);
        this.progressAnimation.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            WebPlayerView.access$5602(WebPlayerView.this, null);
          }
        });
        this.progressAnimation.start();
        return;
        f = 0.0F;
      }
    }
    Object localObject = this.progressView;
    if (paramBoolean1);
    while (true)
    {
      ((RadialProgressView)localObject).setAlpha(f);
      return;
      f = 0.0F;
    }
  }

  private void updateFullscreenButton()
  {
    if ((!this.videoPlayer.isPlayerPrepared()) || (this.isInline))
    {
      this.fullscreenButton.setVisibility(8);
      return;
    }
    this.fullscreenButton.setVisibility(0);
    if (!this.inFullscreen)
    {
      this.fullscreenButton.setImageResource(2130837779);
      this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0F, 85, 0.0F, 0.0F, 0.0F, 5.0F));
      return;
    }
    this.fullscreenButton.setImageResource(2130837824);
    this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0F, 85, 0.0F, 0.0F, 0.0F, 1.0F));
  }

  private void updateFullscreenState(boolean paramBoolean)
  {
    if (this.textureView == null)
      return;
    updateFullscreenButton();
    ViewGroup localViewGroup;
    if (this.textureViewContainer == null)
    {
      this.changingTextureView = true;
      if (!this.inFullscreen)
      {
        if (this.textureViewContainer != null)
          this.textureViewContainer.addView(this.textureView);
      }
      else
      {
        if (!this.inFullscreen)
          break label182;
        localViewGroup = (ViewGroup)this.controlsView.getParent();
        if (localViewGroup != null)
          localViewGroup.removeView(this.controlsView);
      }
      while (true)
      {
        this.changedTextureView = this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), paramBoolean);
        this.changedTextureView.setVisibility(4);
        if ((this.inFullscreen) && (this.changedTextureView != null))
        {
          localViewGroup = (ViewGroup)this.textureView.getParent();
          if (localViewGroup != null)
            localViewGroup.removeView(this.textureView);
        }
        this.controlsView.checkNeedHide();
        return;
        this.aspectRatioFrameLayout.addView(this.textureView);
        break;
        label182: localViewGroup = (ViewGroup)this.controlsView.getParent();
        if (localViewGroup == this)
          continue;
        if (localViewGroup != null)
          localViewGroup.removeView(this.controlsView);
        if (this.textureViewContainer != null)
        {
          this.textureViewContainer.addView(this.controlsView);
          continue;
        }
        addView(this.controlsView, 1);
      }
    }
    if (this.inFullscreen)
    {
      localViewGroup = (ViewGroup)this.aspectRatioFrameLayout.getParent();
      if (localViewGroup != null)
        localViewGroup.removeView(this.aspectRatioFrameLayout);
    }
    while (true)
    {
      this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), paramBoolean);
      return;
      localViewGroup = (ViewGroup)this.aspectRatioFrameLayout.getParent();
      if (localViewGroup == this)
        continue;
      if (localViewGroup != null)
        localViewGroup.removeView(this.aspectRatioFrameLayout);
      addView(this.aspectRatioFrameLayout, 0);
    }
  }

  private void updateInlineButton()
  {
    if (this.inlineButton == null)
      return;
    ImageView localImageView = this.inlineButton;
    if (this.isInline)
    {
      i = 2130837780;
      localImageView.setImageResource(i);
      localImageView = this.inlineButton;
      if (!this.videoPlayer.isPlayerPrepared())
        break label82;
    }
    label82: for (int i = 0; ; i = 8)
    {
      localImageView.setVisibility(i);
      if (!this.isInline)
        break label88;
      this.inlineButton.setLayoutParams(LayoutHelper.createFrame(40, 40, 53));
      return;
      i = 2130837825;
      break;
    }
    label88: this.inlineButton.setLayoutParams(LayoutHelper.createFrame(56, 50, 53));
  }

  private void updatePlayButton()
  {
    this.controlsView.checkNeedHide();
    AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
    if (!this.videoPlayer.isPlaying())
    {
      if (this.isCompleted)
      {
        localImageView = this.playButton;
        if (this.isInline);
        for (i = 2130837747; ; i = 2130837746)
        {
          localImageView.setImageResource(i);
          return;
        }
      }
      localImageView = this.playButton;
      if (this.isInline);
      for (i = 2130837831; ; i = 2130837829)
      {
        localImageView.setImageResource(i);
        return;
      }
    }
    ImageView localImageView = this.playButton;
    if (this.isInline);
    for (int i = 2130837827; ; i = 2130837826)
    {
      localImageView.setImageResource(i);
      AndroidUtilities.runOnUIThread(this.progressRunnable, 500L);
      checkAudioFocus();
      return;
    }
  }

  private void updateShareButton()
  {
    if (this.shareButton == null)
      return;
    ImageView localImageView = this.shareButton;
    if ((this.isInline) || (!this.videoPlayer.isPlayerPrepared()));
    for (int i = 8; ; i = 0)
    {
      localImageView.setVisibility(i);
      return;
    }
  }

  public void destroy()
  {
    this.videoPlayer.releasePlayer();
    if (this.currentTask != null)
    {
      this.currentTask.cancel(true);
      this.currentTask = null;
    }
    this.webView.stopLoading();
  }

  // ERROR //
  protected String downloadUrlContent(AsyncTask paramAsyncTask, String paramString)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 5
    //   3: iconst_0
    //   4: istore 4
    //   6: new 757	java/net/URL
    //   9: dup
    //   10: aload_2
    //   11: invokespecial 759	java/net/URL:<init>	(Ljava/lang/String;)V
    //   14: invokevirtual 763	java/net/URL:openConnection	()Ljava/net/URLConnection;
    //   17: astore 8
    //   19: aload 8
    //   21: astore_2
    //   22: aload 8
    //   24: ldc_w 765
    //   27: ldc_w 767
    //   30: invokevirtual 773	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   33: aload 8
    //   35: astore_2
    //   36: aload 8
    //   38: ldc_w 775
    //   41: ldc_w 777
    //   44: invokevirtual 773	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   47: aload 8
    //   49: astore_2
    //   50: aload 8
    //   52: ldc_w 779
    //   55: ldc_w 781
    //   58: invokevirtual 773	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   61: aload 8
    //   63: astore_2
    //   64: aload 8
    //   66: ldc_w 783
    //   69: ldc_w 785
    //   72: invokevirtual 773	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   75: aload 8
    //   77: astore_2
    //   78: aload 8
    //   80: ldc_w 787
    //   83: ldc_w 789
    //   86: invokevirtual 773	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   89: aload 8
    //   91: astore_2
    //   92: aload 8
    //   94: sipush 5000
    //   97: invokevirtual 792	java/net/URLConnection:setConnectTimeout	(I)V
    //   100: aload 8
    //   102: astore_2
    //   103: aload 8
    //   105: sipush 5000
    //   108: invokevirtual 795	java/net/URLConnection:setReadTimeout	(I)V
    //   111: aload 8
    //   113: astore 7
    //   115: aload 8
    //   117: astore_2
    //   118: aload 8
    //   120: instanceof 797
    //   123: ifeq +182 -> 305
    //   126: aload 8
    //   128: astore_2
    //   129: aload 8
    //   131: checkcast 797	java/net/HttpURLConnection
    //   134: astore 9
    //   136: aload 8
    //   138: astore_2
    //   139: aload 9
    //   141: iconst_1
    //   142: invokevirtual 800	java/net/HttpURLConnection:setInstanceFollowRedirects	(Z)V
    //   145: aload 8
    //   147: astore_2
    //   148: aload 9
    //   150: invokevirtual 803	java/net/HttpURLConnection:getResponseCode	()I
    //   153: istore_3
    //   154: iload_3
    //   155: sipush 302
    //   158: if_icmpeq +21 -> 179
    //   161: iload_3
    //   162: sipush 301
    //   165: if_icmpeq +14 -> 179
    //   168: aload 8
    //   170: astore 7
    //   172: iload_3
    //   173: sipush 303
    //   176: if_icmpne +129 -> 305
    //   179: aload 8
    //   181: astore_2
    //   182: aload 9
    //   184: ldc_w 805
    //   187: invokevirtual 809	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
    //   190: astore 7
    //   192: aload 8
    //   194: astore_2
    //   195: aload 9
    //   197: ldc_w 811
    //   200: invokevirtual 809	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
    //   203: astore 9
    //   205: aload 8
    //   207: astore_2
    //   208: new 757	java/net/URL
    //   211: dup
    //   212: aload 7
    //   214: invokespecial 759	java/net/URL:<init>	(Ljava/lang/String;)V
    //   217: invokevirtual 763	java/net/URL:openConnection	()Ljava/net/URLConnection;
    //   220: astore 7
    //   222: aload 7
    //   224: astore_2
    //   225: aload 7
    //   227: ldc_w 813
    //   230: aload 9
    //   232: invokevirtual 816	java/net/URLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   235: aload 7
    //   237: astore_2
    //   238: aload 7
    //   240: ldc_w 765
    //   243: ldc_w 767
    //   246: invokevirtual 773	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   249: aload 7
    //   251: astore_2
    //   252: aload 7
    //   254: ldc_w 775
    //   257: ldc_w 777
    //   260: invokevirtual 773	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   263: aload 7
    //   265: astore_2
    //   266: aload 7
    //   268: ldc_w 779
    //   271: ldc_w 781
    //   274: invokevirtual 773	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   277: aload 7
    //   279: astore_2
    //   280: aload 7
    //   282: ldc_w 783
    //   285: ldc_w 785
    //   288: invokevirtual 773	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   291: aload 7
    //   293: astore_2
    //   294: aload 7
    //   296: ldc_w 787
    //   299: ldc_w 789
    //   302: invokevirtual 773	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   305: aload 7
    //   307: astore_2
    //   308: aload 7
    //   310: invokevirtual 819	java/net/URLConnection:connect	()V
    //   313: aload 7
    //   315: astore_2
    //   316: new 821	java/util/zip/GZIPInputStream
    //   319: dup
    //   320: aload 7
    //   322: invokevirtual 825	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
    //   325: invokespecial 828	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
    //   328: astore 8
    //   330: iconst_1
    //   331: istore_3
    //   332: iload_3
    //   333: ifeq +376 -> 709
    //   336: aload 7
    //   338: ifnull +41 -> 379
    //   341: aload 7
    //   343: instanceof 797
    //   346: ifeq +33 -> 379
    //   349: aload 7
    //   351: checkcast 797	java/net/HttpURLConnection
    //   354: invokevirtual 803	java/net/HttpURLConnection:getResponseCode	()I
    //   357: istore_3
    //   358: iload_3
    //   359: sipush 200
    //   362: if_icmpeq +17 -> 379
    //   365: iload_3
    //   366: sipush 202
    //   369: if_icmpeq +10 -> 379
    //   372: iload_3
    //   373: sipush 304
    //   376: if_icmpeq +3 -> 379
    //   379: aload 8
    //   381: ifnull +320 -> 701
    //   384: ldc_w 829
    //   387: newarray byte
    //   389: astore 10
    //   391: aconst_null
    //   392: astore_2
    //   393: aload_2
    //   394: astore 7
    //   396: aload_1
    //   397: invokevirtual 832	android/os/AsyncTask:isCancelled	()Z
    //   400: istore 6
    //   402: iload 6
    //   404: ifeq +141 -> 545
    //   407: iload 4
    //   409: istore_3
    //   410: aload_2
    //   411: astore_1
    //   412: aload_1
    //   413: astore_2
    //   414: iload_3
    //   415: istore 4
    //   417: aload 8
    //   419: ifnull +13 -> 432
    //   422: aload 8
    //   424: invokevirtual 837	java/io/InputStream:close	()V
    //   427: iload_3
    //   428: istore 4
    //   430: aload_1
    //   431: astore_2
    //   432: iload 4
    //   434: ifeq +237 -> 671
    //   437: aload_2
    //   438: invokevirtual 843	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   441: areturn
    //   442: astore 7
    //   444: aconst_null
    //   445: astore_2
    //   446: aload 7
    //   448: instanceof 845
    //   451: ifeq +25 -> 476
    //   454: invokestatic 850	org/vidogram/tgnet/ConnectionsManager:isNetworkOnline	()Z
    //   457: ifeq +261 -> 718
    //   460: iconst_0
    //   461: istore_3
    //   462: aload 7
    //   464: invokestatic 856	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   467: aconst_null
    //   468: astore 8
    //   470: aload_2
    //   471: astore 7
    //   473: goto -141 -> 332
    //   476: aload 7
    //   478: instanceof 858
    //   481: ifeq +8 -> 489
    //   484: iconst_0
    //   485: istore_3
    //   486: goto -24 -> 462
    //   489: aload 7
    //   491: instanceof 860
    //   494: ifeq +30 -> 524
    //   497: aload 7
    //   499: invokevirtual 863	java/lang/Throwable:getMessage	()Ljava/lang/String;
    //   502: ifnull +216 -> 718
    //   505: aload 7
    //   507: invokevirtual 863	java/lang/Throwable:getMessage	()Ljava/lang/String;
    //   510: ldc_w 865
    //   513: invokevirtual 871	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   516: ifeq +202 -> 718
    //   519: iconst_0
    //   520: istore_3
    //   521: goto -59 -> 462
    //   524: aload 7
    //   526: instanceof 873
    //   529: ifeq +189 -> 718
    //   532: iconst_0
    //   533: istore_3
    //   534: goto -72 -> 462
    //   537: astore_2
    //   538: aload_2
    //   539: invokestatic 856	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   542: goto -163 -> 379
    //   545: aload_2
    //   546: astore 7
    //   548: aload 8
    //   550: aload 10
    //   552: invokevirtual 877	java/io/InputStream:read	([B)I
    //   555: istore 5
    //   557: iload 5
    //   559: ifle +45 -> 604
    //   562: aload_2
    //   563: ifnonnull +135 -> 698
    //   566: aload_2
    //   567: astore 7
    //   569: new 839	java/lang/StringBuilder
    //   572: dup
    //   573: invokespecial 878	java/lang/StringBuilder:<init>	()V
    //   576: astore 9
    //   578: aload 9
    //   580: astore_2
    //   581: aload_2
    //   582: new 867	java/lang/String
    //   585: dup
    //   586: aload 10
    //   588: iconst_0
    //   589: iload 5
    //   591: ldc_w 880
    //   594: invokespecial 883	java/lang/String:<init>	([BIILjava/lang/String;)V
    //   597: invokevirtual 887	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   600: pop
    //   601: goto -208 -> 393
    //   604: aload_2
    //   605: astore_1
    //   606: iload 4
    //   608: istore_3
    //   609: iload 5
    //   611: iconst_m1
    //   612: if_icmpne -200 -> 412
    //   615: iconst_1
    //   616: istore_3
    //   617: aload_2
    //   618: astore_1
    //   619: goto -207 -> 412
    //   622: astore 7
    //   624: aload_2
    //   625: astore_1
    //   626: aload 7
    //   628: astore_2
    //   629: aload_1
    //   630: astore 7
    //   632: aload_2
    //   633: invokestatic 856	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   636: iload 4
    //   638: istore_3
    //   639: goto -227 -> 412
    //   642: astore_1
    //   643: aload 7
    //   645: astore_2
    //   646: aload_1
    //   647: invokestatic 856	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   650: aload_2
    //   651: astore_1
    //   652: iload 4
    //   654: istore_3
    //   655: goto -243 -> 412
    //   658: astore_2
    //   659: aload_2
    //   660: invokestatic 856	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   663: aload_1
    //   664: astore_2
    //   665: iload_3
    //   666: istore 4
    //   668: goto -236 -> 432
    //   671: aconst_null
    //   672: areturn
    //   673: astore_1
    //   674: aconst_null
    //   675: astore_2
    //   676: goto -30 -> 646
    //   679: astore_1
    //   680: goto -34 -> 646
    //   683: astore 7
    //   685: aload_2
    //   686: astore_1
    //   687: aload 7
    //   689: astore_2
    //   690: goto -61 -> 629
    //   693: astore 7
    //   695: goto -249 -> 446
    //   698: goto -117 -> 581
    //   701: aconst_null
    //   702: astore_1
    //   703: iload 4
    //   705: istore_3
    //   706: goto -294 -> 412
    //   709: aconst_null
    //   710: astore_2
    //   711: iload 5
    //   713: istore 4
    //   715: goto -283 -> 432
    //   718: iconst_1
    //   719: istore_3
    //   720: goto -258 -> 462
    //
    // Exception table:
    //   from	to	target	type
    //   6	19	442	java/lang/Throwable
    //   341	358	537	java/lang/Exception
    //   581	601	622	java/lang/Exception
    //   396	402	642	java/lang/Throwable
    //   548	557	642	java/lang/Throwable
    //   569	578	642	java/lang/Throwable
    //   632	636	642	java/lang/Throwable
    //   422	427	658	java/lang/Throwable
    //   384	391	673	java/lang/Throwable
    //   581	601	679	java/lang/Throwable
    //   548	557	683	java/lang/Exception
    //   569	578	683	java/lang/Exception
    //   22	33	693	java/lang/Throwable
    //   36	47	693	java/lang/Throwable
    //   50	61	693	java/lang/Throwable
    //   64	75	693	java/lang/Throwable
    //   78	89	693	java/lang/Throwable
    //   92	100	693	java/lang/Throwable
    //   103	111	693	java/lang/Throwable
    //   118	126	693	java/lang/Throwable
    //   129	136	693	java/lang/Throwable
    //   139	145	693	java/lang/Throwable
    //   148	154	693	java/lang/Throwable
    //   182	192	693	java/lang/Throwable
    //   195	205	693	java/lang/Throwable
    //   208	222	693	java/lang/Throwable
    //   225	235	693	java/lang/Throwable
    //   238	249	693	java/lang/Throwable
    //   252	263	693	java/lang/Throwable
    //   266	277	693	java/lang/Throwable
    //   280	291	693	java/lang/Throwable
    //   294	305	693	java/lang/Throwable
    //   308	313	693	java/lang/Throwable
    //   316	330	693	java/lang/Throwable
  }

  public void enterFullscreen()
  {
    if (this.inFullscreen)
      return;
    this.inFullscreen = true;
    updateInlineButton();
    updateFullscreenState(false);
  }

  public void exitFullscreen()
  {
    if (!this.inFullscreen)
      return;
    this.inFullscreen = false;
    updateInlineButton();
    updateFullscreenState(false);
  }

  public View getAspectRatioView()
  {
    return this.aspectRatioFrameLayout;
  }

  public View getControlsView()
  {
    return this.controlsView;
  }

  public ImageView getTextureImageView()
  {
    return this.textureImageView;
  }

  public TextureView getTextureView()
  {
    return this.textureView;
  }

  public boolean isInFullscreen()
  {
    return this.inFullscreen;
  }

  public boolean isInitied()
  {
    return this.initied;
  }

  public boolean isInline()
  {
    return (this.isInline) || (this.switchingInlineMode);
  }

  public boolean loadVideo(String paramString1, TLRPC.Photo paramPhoto, String paramString2, boolean paramBoolean)
  {
    Object localObject1 = null;
    Object localObject3 = null;
    this.seekToTime = -1;
    Object localObject5;
    Object localObject6;
    if (paramString2.equals("hls"))
    {
      paramString2 = null;
      localObject1 = null;
      localObject3 = null;
      localObject5 = null;
      localObject6 = "hls";
    }
    while (true)
    {
      this.initied = false;
      this.isCompleted = false;
      this.isAutoplay = paramBoolean;
      this.playVideoUrl = null;
      this.playAudioUrl = null;
      destroy();
      this.firstFrameRendered = false;
      this.currentAlpha = 1.0F;
      if (this.currentTask != null)
      {
        this.currentTask.cancel(true);
        this.currentTask = null;
      }
      updateFullscreenButton();
      updateShareButton();
      updateInlineButton();
      updatePlayButton();
      Object localObject7;
      ImageReceiver localImageReceiver;
      if (paramPhoto != null)
      {
        localObject7 = FileLoader.getClosestPhotoSizeWithSize(paramPhoto.sizes, 80, true);
        if (localObject7 != null)
        {
          localImageReceiver = this.controlsView.imageReceiver;
          if (paramPhoto != null)
          {
            localObject7 = ((TLRPC.PhotoSize)localObject7).location;
            label155: if (paramPhoto == null)
              break label623;
            paramPhoto = "80_80_b";
            label163: localImageReceiver.setImage(null, null, (TLRPC.FileLocation)localObject7, paramPhoto, 0, null, true);
            this.drawImage = true;
          }
        }
        else
        {
          label181: if (this.progressAnimation != null)
          {
            this.progressAnimation.cancel();
            this.progressAnimation = null;
          }
          this.isLoading = true;
          this.controlsView.setProgress(0);
          if (localObject6 == null)
            break label636;
          this.initied = true;
          this.playVideoUrl = paramString1;
          this.playVideoType = "hls";
          if (this.isAutoplay)
            preparePlayer();
          showProgress(false, false);
          this.controlsView.show(true, true);
          label261: if ((localObject5 == null) && (localObject3 == null) && (0 == 0) && (localObject1 == null) && (paramString2 == null) && (localObject6 == null))
            break label891;
          return true;
          if (paramString1 == null)
            break label919;
          if (paramString1.endsWith(".mp4"))
          {
            paramString2 = paramString1;
            localObject1 = null;
            localObject3 = null;
            localObject5 = null;
            localObject6 = null;
            continue;
          }
          if (paramString2 == null);
        }
      }
      while (true)
      {
        try
        {
          paramString2 = Uri.parse(paramString2).getQueryParameter("t");
          if (paramString2 == null)
            continue;
          if (!paramString2.contains("m"))
            continue;
          paramString2 = paramString2.split("m");
          int i = Utilities.parseInt(paramString2[0]).intValue();
          this.seekToTime = (Utilities.parseInt(paramString2[1]).intValue() + i * 60);
        }
        catch (Exception localObject2)
        {
          try
          {
            localObject5 = youtubeIdRegex.matcher(paramString1);
            paramString2 = null;
            if (!((Matcher)localObject5).find())
              continue;
            paramString2 = ((Matcher)localObject5).group(1);
            if (paramString2 == null)
              break label914;
            localObject1 = localObject3;
            if (paramString2 != null)
              continue;
          }
          catch (Exception localObject2)
          {
            try
            {
              localObject5 = vimeoIdRegex.matcher(paramString1);
              localObject1 = null;
              if (!((Matcher)localObject5).find())
                continue;
              localObject1 = ((Matcher)localObject5).group(3);
              if (localObject1 == null)
                break label908;
              if ((paramString2 != null) || (localObject1 != null))
                continue;
            }
            catch (Exception localObject2)
            {
              try
              {
                localObject5 = aparatIdRegex.matcher(paramString1);
                localObject3 = null;
                if (!((Matcher)localObject5).find())
                  continue;
                localObject3 = ((Matcher)localObject5).group(1);
                if (localObject3 == null)
                  break label902;
                localImageReceiver = null;
                localObject5 = localObject1;
                localObject6 = paramString2;
                localObject7 = null;
                paramString2 = localImageReceiver;
                localObject1 = localObject3;
                localObject3 = localObject5;
                localObject5 = localObject6;
                localObject6 = localObject7;
                break;
                this.seekToTime = Utilities.parseInt(paramString2).intValue();
                continue;
                paramString2 = paramString2;
                FileLog.e(paramString2);
                continue;
                paramString2 = paramString2;
                FileLog.e(paramString2);
                paramString2 = (String)localObject1;
                continue;
                localException1 = localException1;
                FileLog.e(localException1);
                localObject2 = localObject3;
              }
              catch (Exception localException2)
              {
                FileLog.e(localException2);
              }
              localImageReceiver = null;
              localObject7 = null;
              localObject4 = localObject2;
              localObject5 = paramString2;
              localObject6 = null;
              paramString2 = localImageReceiver;
              localObject2 = localObject7;
            }
          }
        }
        break;
        localObject7 = null;
        break label155;
        label623: paramPhoto = null;
        break label163;
        this.drawImage = false;
        break label181;
        label636: if (paramString2 != null)
        {
          this.initied = true;
          this.playVideoUrl = paramString2;
          this.playVideoType = "other";
          if (this.isAutoplay)
            preparePlayer();
          showProgress(false, false);
          this.controlsView.show(true, true);
          break label261;
        }
        if (localObject5 != null)
        {
          paramString1 = new YoutubeVideoTask((String)localObject5);
          paramString1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
          this.currentTask = paramString1;
        }
        while (true)
        {
          this.controlsView.show(false, false);
          showProgress(true, false);
          break;
          if (localObject4 != null)
          {
            paramString1 = new VimeoVideoTask(localObject4);
            paramString1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
            this.currentTask = paramString1;
            continue;
          }
          if (0 != 0)
          {
            paramString1 = new CoubVideoTask(null);
            paramString1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
            this.currentTask = paramString1;
            continue;
          }
          if (localObject2 == null)
            continue;
          paramString1 = new AparatVideoTask(localObject2);
          paramString1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
          this.currentTask = paramString1;
        }
        label891: this.controlsView.setVisibility(8);
        return false;
        label902: localObject4 = null;
        continue;
        label908: localObject2 = null;
        continue;
        label914: paramString2 = null;
      }
      label919: paramString2 = null;
      Object localObject2 = null;
      Object localObject4 = null;
      localObject5 = null;
      localObject6 = null;
    }
  }

  public void onAudioFocusChange(int paramInt)
  {
    if (paramInt == -1)
    {
      if (this.videoPlayer.isPlaying())
      {
        this.videoPlayer.pause();
        updatePlayButton();
      }
      this.hasAudioFocus = false;
      this.audioFocus = 0;
    }
    do
    {
      do
      {
        while (true)
        {
          return;
          if (paramInt != 1)
            break;
          this.audioFocus = 2;
          if (!this.resumeAudioOnFocusGain)
            continue;
          this.resumeAudioOnFocusGain = false;
          this.videoPlayer.play();
          return;
        }
        if (paramInt != -3)
          continue;
        this.audioFocus = 1;
        return;
      }
      while (paramInt != -2);
      this.audioFocus = 0;
    }
    while (!this.videoPlayer.isPlaying());
    this.resumeAudioOnFocusGain = true;
    this.videoPlayer.pause();
    updatePlayButton();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(10.0F), this.backgroundPaint);
  }

  public void onError(Exception paramException)
  {
    FileLog.e(paramException);
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = (paramInt3 - paramInt1 - this.aspectRatioFrameLayout.getMeasuredWidth()) / 2;
    int j = (paramInt4 - paramInt2 - AndroidUtilities.dp(10.0F) - this.aspectRatioFrameLayout.getMeasuredHeight()) / 2;
    this.aspectRatioFrameLayout.layout(i, j, this.aspectRatioFrameLayout.getMeasuredWidth() + i, this.aspectRatioFrameLayout.getMeasuredHeight() + j);
    if (this.controlsView.getParent() == this)
      this.controlsView.layout(0, 0, this.controlsView.getMeasuredWidth(), this.controlsView.getMeasuredHeight());
    paramInt1 = (paramInt3 - paramInt1 - this.progressView.getMeasuredWidth()) / 2;
    paramInt2 = (paramInt4 - paramInt2 - this.progressView.getMeasuredHeight()) / 2;
    this.progressView.layout(paramInt1, paramInt2, this.progressView.getMeasuredWidth() + paramInt1, this.progressView.getMeasuredHeight() + paramInt2);
    this.controlsView.imageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(10.0F));
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    this.aspectRatioFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2 - AndroidUtilities.dp(10.0F), 1073741824));
    if (this.controlsView.getParent() == this)
      this.controlsView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824));
    this.progressView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0F), 1073741824));
    setMeasuredDimension(paramInt1, paramInt2);
  }

  public void onRenderedFirstFrame()
  {
    this.firstFrameRendered = true;
    this.lastUpdateTime = System.currentTimeMillis();
    this.controlsView.invalidate();
  }

  public void onStateChanged(boolean paramBoolean, int paramInt)
  {
    if (paramInt != 2)
    {
      if (this.videoPlayer.getDuration() != -9223372036854775807L)
        this.controlsView.setDuration((int)(this.videoPlayer.getDuration() / 1000L));
    }
    else
    {
      if ((paramInt == 4) || (paramInt == 1) || (!this.videoPlayer.isPlaying()))
        break label100;
      this.delegate.onPlayStateChanged(this, true);
      label69: if ((!this.videoPlayer.isPlaying()) || (paramInt == 4))
        break label114;
      updatePlayButton();
    }
    label100: label114: 
    do
    {
      return;
      this.controlsView.setDuration(0);
      break;
      this.delegate.onPlayStateChanged(this, false);
      break label69;
    }
    while (paramInt != 4);
    this.isCompleted = true;
    this.videoPlayer.pause();
    this.videoPlayer.seekTo(0L);
    updatePlayButton();
    this.controlsView.show(true, true);
  }

  public boolean onSurfaceDestroyed(SurfaceTexture paramSurfaceTexture)
  {
    if (this.changingTextureView)
    {
      this.changingTextureView = false;
      if ((this.inFullscreen) || (this.isInline))
      {
        if (this.isInline)
          this.waitingForFirstTextureUpload = 1;
        this.changedTextureView.setSurfaceTexture(paramSurfaceTexture);
        this.changedTextureView.setSurfaceTextureListener(this.surfaceTextureListener);
        this.changedTextureView.setVisibility(0);
        return true;
      }
    }
    return false;
  }

  public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture)
  {
    if (this.waitingForFirstTextureUpload == 2)
    {
      if (this.textureImageView != null)
      {
        this.textureImageView.setVisibility(4);
        this.textureImageView.setImageDrawable(null);
        if (this.currentBitmap != null)
        {
          this.currentBitmap.recycle();
          this.currentBitmap = null;
        }
      }
      this.switchingInlineMode = false;
      this.delegate.onSwitchInlineMode(this.controlsView, false, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), this.allowInlineAnimation);
      this.waitingForFirstTextureUpload = 0;
    }
  }

  public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    int j;
    int i;
    if (this.aspectRatioFrameLayout != null)
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
      if (i != 0)
        break label70;
      paramFloat = 1.0F;
    }
    while (true)
    {
      this.aspectRatioFrameLayout.setAspectRatio(paramFloat, paramInt3);
      if (this.inFullscreen)
        this.delegate.onVideoSizeChanged(paramFloat, paramInt3);
      return;
      label70: paramFloat = j * paramFloat / i;
    }
  }

  public void pause()
  {
    this.videoPlayer.pause();
    updatePlayButton();
    this.controlsView.show(true, true);
  }

  public void updateTextureImageView()
  {
    if (this.textureImageView == null)
      return;
    try
    {
      this.currentBitmap = Bitmaps.createBitmap(this.textureView.getWidth(), this.textureView.getHeight(), Bitmap.Config.ARGB_8888);
      this.changedTextureView.getBitmap(this.currentBitmap);
      if (this.currentBitmap != null)
      {
        this.textureImageView.setVisibility(0);
        this.textureImageView.setImageBitmap(this.currentBitmap);
        return;
      }
    }
    catch (Throwable localThrowable)
    {
      while (true)
      {
        if (this.currentBitmap != null)
        {
          this.currentBitmap.recycle();
          this.currentBitmap = null;
        }
        FileLog.e(localThrowable);
      }
      this.textureImageView.setImageDrawable(null);
    }
  }

  private class AparatVideoTask extends AsyncTask<Void, Void, String>
  {
    private boolean canRetry = true;
    private String[] results = new String[2];
    private String videoId;

    public AparatVideoTask(String arg2)
    {
      Object localObject;
      this.videoId = localObject;
    }

    protected String doInBackground(Void[] paramArrayOfVoid)
    {
      paramArrayOfVoid = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "http://www.aparat.com/video/video/embed/vt/frame/showvideo/yes/videohash/%s", new Object[] { this.videoId }));
      if (isCancelled())
        return null;
      while (true)
      {
        int i;
        try
        {
          paramArrayOfVoid = WebPlayerView.aparatFileListPattern.matcher(paramArrayOfVoid);
          if (paramArrayOfVoid.find())
          {
            paramArrayOfVoid = new JSONArray(paramArrayOfVoid.group(1));
            i = 0;
            if (i < paramArrayOfVoid.length())
            {
              Object localObject = paramArrayOfVoid.getJSONArray(i);
              if (((JSONArray)localObject).length() == 0)
                break label148;
              localObject = ((JSONArray)localObject).getJSONObject(0);
              if (!((JSONObject)localObject).has("file"))
                break label148;
              this.results[0] = ((JSONObject)localObject).getString("file");
              this.results[1] = "other";
            }
          }
        }
        catch (Exception paramArrayOfVoid)
        {
          FileLog.e(paramArrayOfVoid);
        }
        if (isCancelled())
          break;
        return this.results[0];
        label148: i += 1;
      }
    }

    protected void onPostExecute(String paramString)
    {
      if (paramString != null)
      {
        WebPlayerView.access$1702(WebPlayerView.this, true);
        WebPlayerView.access$1902(WebPlayerView.this, paramString);
        WebPlayerView.access$1802(WebPlayerView.this, this.results[1]);
        if (WebPlayerView.this.isAutoplay)
          WebPlayerView.this.preparePlayer();
        WebPlayerView.this.showProgress(false, true);
        WebPlayerView.this.controlsView.show(true, true);
      }
      do
        return;
      while (isCancelled());
      WebPlayerView.this.onInitFailed();
    }
  }

  public static abstract interface CallJavaResultInterface
  {
    public abstract void jsCallFinished(String paramString);
  }

  private class ControlsView extends FrameLayout
  {
    private int bufferedPercentage;
    private int bufferedPosition;
    private AnimatorSet currentAnimation;
    private int currentProgressX;
    private int duration;
    private StaticLayout durationLayout;
    private int durationWidth;
    private Runnable hideRunnable = new Runnable()
    {
      public void run()
      {
        WebPlayerView.ControlsView.this.show(false, true);
      }
    };
    private ImageReceiver imageReceiver;
    private boolean isVisible = true;
    private int lastProgressX;
    private int progress;
    private Paint progressBufferedPaint;
    private Paint progressInnerPaint;
    private StaticLayout progressLayout;
    private Paint progressPaint;
    private boolean progressPressed;
    private TextPaint textPaint;

    public ControlsView(Context arg2)
    {
      super();
      setWillNotDraw(false);
      this.textPaint = new TextPaint(1);
      this.textPaint.setColor(-1);
      this.textPaint.setTextSize(AndroidUtilities.dp(12.0F));
      this.progressPaint = new Paint(1);
      this.progressPaint.setColor(-15095832);
      this.progressInnerPaint = new Paint();
      this.progressInnerPaint.setColor(-6975081);
      this.progressBufferedPaint = new Paint(1);
      this.progressBufferedPaint.setColor(-1);
      this.imageReceiver = new ImageReceiver(this);
    }

    private void checkNeedHide()
    {
      AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
      if ((this.isVisible) && (WebPlayerView.this.videoPlayer.isPlaying()))
        AndroidUtilities.runOnUIThread(this.hideRunnable, 3000L);
    }

    protected void onDraw(Canvas paramCanvas)
    {
      int m = 6;
      int i7 = 0;
      if (WebPlayerView.this.drawImage)
      {
        if ((WebPlayerView.this.firstFrameRendered) && (WebPlayerView.this.currentAlpha != 0.0F))
        {
          long l1 = System.currentTimeMillis();
          long l2 = WebPlayerView.this.lastUpdateTime;
          WebPlayerView.access$4702(WebPlayerView.this, l1);
          WebPlayerView.access$4602(WebPlayerView.this, WebPlayerView.this.currentAlpha - (float)(l1 - l2) / 150.0F);
          if (WebPlayerView.this.currentAlpha < 0.0F)
            WebPlayerView.access$4602(WebPlayerView.this, 0.0F);
          invalidate();
        }
        this.imageReceiver.setAlpha(WebPlayerView.this.currentAlpha);
        this.imageReceiver.draw(paramCanvas);
      }
      int i;
      int i8;
      float f1;
      label275: label357: float f2;
      label406: float f3;
      Paint localPaint;
      if (WebPlayerView.this.videoPlayer.isPlayerPrepared())
      {
        i = getMeasuredWidth();
        i8 = getMeasuredHeight();
        int j;
        if (!WebPlayerView.this.isInline)
        {
          if (this.durationLayout != null)
          {
            paramCanvas.save();
            f1 = i - AndroidUtilities.dp(58.0F) - this.durationWidth;
            if (!WebPlayerView.this.inFullscreen)
              break label600;
            j = 6;
            paramCanvas.translate(f1, i8 - AndroidUtilities.dp(j + 29));
            this.durationLayout.draw(paramCanvas);
            paramCanvas.restore();
          }
          if (this.progressLayout != null)
          {
            paramCanvas.save();
            f1 = AndroidUtilities.dp(18.0F);
            if (!WebPlayerView.this.inFullscreen)
              break label607;
            j = m;
            paramCanvas.translate(f1, i8 - AndroidUtilities.dp(j + 29));
            this.progressLayout.draw(paramCanvas);
            paramCanvas.restore();
          }
        }
        if (this.duration != 0)
        {
          if (!WebPlayerView.this.isInline)
            break label614;
          j = AndroidUtilities.dp(3.0F);
          int i3 = i8 - AndroidUtilities.dp(7.0F);
          m = 0;
          int i1 = i8 - j;
          j = i;
          i = i3;
          if (WebPlayerView.this.inFullscreen)
            paramCanvas.drawRect(m, i1, j, AndroidUtilities.dp(3.0F) + i1, this.progressInnerPaint);
          if (!this.progressPressed)
            break label752;
          i3 = this.currentProgressX;
          if ((this.bufferedPercentage != 0) && (this.duration != 0))
          {
            i8 = (j - m) / this.duration * this.bufferedPosition + m;
            if (i3 < i8)
              i7 = i8 - i3;
            f1 = i8 - i7;
            f2 = i1;
            f3 = i8;
            float f4 = (j - i8) * this.bufferedPercentage / 100.0F;
            float f5 = AndroidUtilities.dp(3.0F) + i1;
            if (!WebPlayerView.this.inFullscreen)
              break label779;
            localPaint = this.progressBufferedPaint;
            label513: paramCanvas.drawRect(f1, f2, f3 + f4, f5, localPaint);
          }
          paramCanvas.drawRect(m, i1, i3, AndroidUtilities.dp(3.0F) + i1, this.progressPaint);
          if (!WebPlayerView.this.isInline)
          {
            f2 = i3;
            f3 = i;
            if (!this.progressPressed)
              break label788;
            f1 = 7.0F;
          }
        }
      }
      while (true)
      {
        paramCanvas.drawCircle(f2, f3, AndroidUtilities.dp(f1), this.progressPaint);
        return;
        label600: int k = 10;
        break;
        label607: k = 10;
        break label275;
        label614: if (WebPlayerView.this.inFullscreen)
        {
          int i4 = AndroidUtilities.dp(29.0F);
          i2 = AndroidUtilities.dp(36.0F);
          int i9 = this.durationWidth;
          n = AndroidUtilities.dp(76.0F);
          int i10 = this.durationWidth;
          k = i8 - AndroidUtilities.dp(28.0F);
          n = i - n - i10;
          i2 += i9;
          i5 = i8 - i4;
          i = k;
          k = n;
          n = i2;
          i2 = i5;
          break label357;
        }
        int i5 = AndroidUtilities.dp(13.0F);
        int i2 = i8 - AndroidUtilities.dp(12.0F);
        k = i;
        int n = 0;
        int i6 = i8 - i5;
        i = i2;
        i2 = i6;
        break label357;
        label752: i6 = (int)((k - n) * (this.progress / this.duration)) + n;
        break label406;
        label779: localPaint = this.progressInnerPaint;
        break label513;
        label788: f1 = 5.0F;
      }
    }

    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
    {
      if (paramMotionEvent.getAction() == 0)
      {
        if (!this.isVisible)
        {
          show(true, true);
          return true;
        }
        onTouchEvent(paramMotionEvent);
        return this.progressPressed;
      }
      return super.onInterceptTouchEvent(paramMotionEvent);
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      int k;
      int j;
      int m;
      if (WebPlayerView.this.inFullscreen)
      {
        k = AndroidUtilities.dp(36.0F) + this.durationWidth;
        j = getMeasuredWidth() - AndroidUtilities.dp(76.0F) - this.durationWidth;
        i = getMeasuredHeight() - AndroidUtilities.dp(28.0F);
        if (this.duration == 0)
          break label246;
        m = (int)((j - k) * (this.progress / this.duration));
        label76: m += k;
        if (paramMotionEvent.getAction() != 0)
          break label261;
        if ((!this.isVisible) || (WebPlayerView.this.isInline))
          break label252;
        if (this.duration != 0)
        {
          j = (int)paramMotionEvent.getX();
          k = (int)paramMotionEvent.getY();
          if ((j >= m - AndroidUtilities.dp(10.0F)) && (j <= AndroidUtilities.dp(10.0F) + m) && (k >= i - AndroidUtilities.dp(10.0F)) && (k <= i + AndroidUtilities.dp(10.0F)))
          {
            this.progressPressed = true;
            this.lastProgressX = j;
            this.currentProgressX = m;
            getParent().requestDisallowInterceptTouchEvent(true);
            invalidate();
          }
        }
        label209: AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
      }
      label246: label252: 
      do
        while (true)
        {
          super.onTouchEvent(paramMotionEvent);
          return true;
          j = getMeasuredWidth();
          i = getMeasuredHeight() - AndroidUtilities.dp(12.0F);
          k = 0;
          break;
          m = 0;
          break label76;
          show(true, true);
          break label209;
          if ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3))
            break label379;
          if ((WebPlayerView.this.initied) && (WebPlayerView.this.videoPlayer.isPlaying()))
            AndroidUtilities.runOnUIThread(this.hideRunnable, 3000L);
          if (!this.progressPressed)
            continue;
          this.progressPressed = false;
          if (!WebPlayerView.this.initied)
            continue;
          this.progress = (int)(this.duration * ((this.currentProgressX - k) / (j - k)));
          WebPlayerView.this.videoPlayer.seekTo(this.progress * 1000L);
        }
      while ((paramMotionEvent.getAction() != 2) || (!this.progressPressed));
      label261: label379: int i = (int)paramMotionEvent.getX();
      this.currentProgressX -= this.lastProgressX - i;
      this.lastProgressX = i;
      if (this.currentProgressX < k)
        this.currentProgressX = k;
      while (true)
      {
        setProgress((int)(this.duration * 1000 * ((this.currentProgressX - k) / (j - k))));
        invalidate();
        break;
        if (this.currentProgressX <= j)
          continue;
        this.currentProgressX = j;
      }
    }

    public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
    {
      super.requestDisallowInterceptTouchEvent(paramBoolean);
      checkNeedHide();
    }

    public void setBufferedProgress(int paramInt1, int paramInt2)
    {
      this.bufferedPosition = paramInt1;
      this.bufferedPercentage = paramInt2;
      invalidate();
    }

    public void setDuration(int paramInt)
    {
      if ((this.duration == paramInt) || (paramInt < 0))
        return;
      this.duration = paramInt;
      this.durationLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[] { Integer.valueOf(this.duration / 60), Integer.valueOf(this.duration % 60) }), this.textPaint, AndroidUtilities.dp(1000.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      if (this.durationLayout.getLineCount() > 0)
        this.durationWidth = (int)Math.ceil(this.durationLayout.getLineWidth(0));
      invalidate();
    }

    public void setProgress(int paramInt)
    {
      if ((this.progressPressed) || (paramInt < 0))
        return;
      this.progress = paramInt;
      this.progressLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[] { Integer.valueOf(this.progress / 60), Integer.valueOf(this.progress % 60) }), this.textPaint, AndroidUtilities.dp(1000.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      invalidate();
    }

    public void show(boolean paramBoolean1, boolean paramBoolean2)
    {
      if (this.isVisible == paramBoolean1)
        return;
      this.isVisible = paramBoolean1;
      if (this.currentAnimation != null)
        this.currentAnimation.cancel();
      if (this.isVisible)
        if (paramBoolean2)
        {
          this.currentAnimation = new AnimatorSet();
          this.currentAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "alpha", new float[] { 1.0F }) });
          this.currentAnimation.setDuration(150L);
          this.currentAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              WebPlayerView.ControlsView.access$4202(WebPlayerView.ControlsView.this, null);
            }
          });
          this.currentAnimation.start();
        }
      while (true)
      {
        checkNeedHide();
        return;
        setAlpha(1.0F);
        continue;
        if (paramBoolean2)
        {
          this.currentAnimation = new AnimatorSet();
          this.currentAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "alpha", new float[] { 0.0F }) });
          this.currentAnimation.setDuration(150L);
          this.currentAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              WebPlayerView.ControlsView.access$4202(WebPlayerView.ControlsView.this, null);
            }
          });
          this.currentAnimation.start();
          continue;
        }
        setAlpha(0.0F);
      }
    }
  }

  private class CoubVideoTask extends AsyncTask<Void, Void, String>
  {
    private boolean canRetry = true;
    private String[] results = new String[4];
    private String videoId;

    public CoubVideoTask(String arg2)
    {
      Object localObject;
      this.videoId = localObject;
    }

    protected String doInBackground(Void[] paramArrayOfVoid)
    {
      paramArrayOfVoid = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://coub.com/api/v2/coubs/%s.json", new Object[] { this.videoId }));
      if (isCancelled());
      while (true)
      {
        return null;
        try
        {
          Object localObject = new JSONObject(paramArrayOfVoid);
          paramArrayOfVoid = ((JSONObject)localObject).getString("file");
          localObject = ((JSONObject)localObject).getString("audio_file_url");
          if ((paramArrayOfVoid != null) && (localObject != null))
          {
            this.results[0] = paramArrayOfVoid;
            this.results[1] = "other";
            this.results[2] = localObject;
            this.results[3] = "other";
          }
          if (isCancelled())
            continue;
          return this.results[0];
        }
        catch (Exception paramArrayOfVoid)
        {
          while (true)
            FileLog.e(paramArrayOfVoid);
        }
      }
    }

    protected void onPostExecute(String paramString)
    {
      if (paramString != null)
      {
        WebPlayerView.access$1702(WebPlayerView.this, true);
        WebPlayerView.access$1902(WebPlayerView.this, paramString);
        WebPlayerView.access$1802(WebPlayerView.this, this.results[1]);
        WebPlayerView.access$2502(WebPlayerView.this, this.results[2]);
        WebPlayerView.access$2602(WebPlayerView.this, this.results[3]);
        if (WebPlayerView.this.isAutoplay)
          WebPlayerView.this.preparePlayer();
        WebPlayerView.this.showProgress(false, true);
        WebPlayerView.this.controlsView.show(true, true);
      }
      do
        return;
      while (isCancelled());
      WebPlayerView.this.onInitFailed();
    }
  }

  private class JSExtractor
  {
    private String[] assign_operators = { "|=", "^=", "&=", ">>=", "<<=", "-=", "+=", "%=", "/=", "*=", "=" };
    ArrayList<String> codeLines = new ArrayList();
    private String jsCode;
    private String[] operators = { "|", "^", "&", ">>", "<<", "-", "+", "%", "/", "*" };

    public JSExtractor(String arg2)
    {
      Object localObject;
      this.jsCode = localObject;
    }

    private void buildFunction(String[] paramArrayOfString, String paramString)
    {
      HashMap localHashMap = new HashMap();
      int i = 0;
      while (i < paramArrayOfString.length)
      {
        localHashMap.put(paramArrayOfString[i], "");
        i += 1;
      }
      paramArrayOfString = paramString.split(";");
      paramString = new boolean[1];
      i = 0;
      while (true)
      {
        if (i < paramArrayOfString.length)
        {
          interpretStatement(paramArrayOfString[i], localHashMap, paramString, 100);
          if (paramString[0] == 0);
        }
        else
        {
          return;
        }
        i += 1;
      }
    }

    private String extractFunction(String paramString)
    {
      try
      {
        paramString = Pattern.quote(paramString);
        paramString = Pattern.compile(String.format(Locale.US, "(?x)(?:function\\s+%s|[{;,]\\s*%s\\s*=\\s*function|var\\s+%s\\s*=\\s*function)\\s*\\(([^)]*)\\)\\s*\\{([^}]+)\\}", new Object[] { paramString, paramString, paramString })).matcher(this.jsCode);
        if (paramString.find())
        {
          String str = paramString.group();
          if (!this.codeLines.contains(str))
            this.codeLines.add(str + ";");
          buildFunction(paramString.group(1).split(","), paramString.group(2));
        }
        return TextUtils.join("", this.codeLines);
      }
      catch (Exception paramString)
      {
        while (true)
        {
          this.codeLines.clear();
          FileLog.e(paramString);
        }
      }
    }

    private HashMap<String, Object> extractObject(String paramString)
    {
      HashMap localHashMap = new HashMap();
      Matcher localMatcher = Pattern.compile(String.format(Locale.US, "(?:var\\s+)?%s\\s*=\\s*\\{\\s*(([a-zA-Z$0-9]+\\s*:\\s*function\\(.*?\\)\\s*\\{.*?\\}(?:,\\s*)?)*)\\}\\s*;", new Object[] { Pattern.quote(paramString) })).matcher(this.jsCode);
      paramString = null;
      while (localMatcher.find())
      {
        String str2 = localMatcher.group();
        String str1 = localMatcher.group(2);
        paramString = str1;
        if (TextUtils.isEmpty(str1))
          continue;
        paramString = str1;
        if (this.codeLines.contains(str2))
          break;
        this.codeLines.add(localMatcher.group());
        paramString = str1;
      }
      paramString = Pattern.compile("([a-zA-Z$0-9]+)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}").matcher(paramString);
      while (paramString.find())
        buildFunction(paramString.group(2).split(","), paramString.group(3));
      return localHashMap;
    }

    private void interpretExpression(String paramString, HashMap<String, String> paramHashMap, int paramInt)
    {
      // Byte code:
      //   0: aload_1
      //   1: invokevirtual 200	java/lang/String:trim	()Ljava/lang/String;
      //   4: astore_1
      //   5: aload_1
      //   6: invokestatic 191	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
      //   9: ifeq +4 -> 13
      //   12: return
      //   13: aload_1
      //   14: iconst_0
      //   15: invokevirtual 204	java/lang/String:charAt	(I)C
      //   18: bipush 40
      //   20: if_icmpne +128 -> 148
      //   23: iconst_0
      //   24: istore 4
      //   26: invokestatic 208	org/vidogram/ui/Components/WebPlayerView:access$300	()Ljava/util/regex/Pattern;
      //   29: aload_1
      //   30: invokevirtual 134	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
      //   33: astore 6
      //   35: aload 6
      //   37: invokevirtual 140	java/util/regex/Matcher:find	()Z
      //   40: ifeq +761 -> 801
      //   43: aload 6
      //   45: iconst_0
      //   46: invokevirtual 164	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   49: bipush 48
      //   51: invokevirtual 212	java/lang/String:indexOf	(I)I
      //   54: bipush 40
      //   56: if_icmpne +12 -> 68
      //   59: iload 4
      //   61: iconst_1
      //   62: iadd
      //   63: istore 4
      //   65: goto -30 -> 35
      //   68: iload 4
      //   70: iconst_1
      //   71: isub
      //   72: istore 5
      //   74: iload 5
      //   76: istore 4
      //   78: iload 5
      //   80: ifne -45 -> 35
      //   83: aload_0
      //   84: aload_1
      //   85: iconst_1
      //   86: aload 6
      //   88: invokevirtual 216	java/util/regex/Matcher:start	()I
      //   91: invokevirtual 220	java/lang/String:substring	(II)Ljava/lang/String;
      //   94: aload_2
      //   95: iload_3
      //   96: invokespecial 222	org/vidogram/ui/Components/WebPlayerView$JSExtractor:interpretExpression	(Ljava/lang/String;Ljava/util/HashMap;I)V
      //   99: aload_1
      //   100: aload 6
      //   102: invokevirtual 225	java/util/regex/Matcher:end	()I
      //   105: invokevirtual 227	java/lang/String:substring	(I)Ljava/lang/String;
      //   108: invokevirtual 200	java/lang/String:trim	()Ljava/lang/String;
      //   111: astore_1
      //   112: aload_1
      //   113: invokestatic 191	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
      //   116: ifne -104 -> 12
      //   119: aload_1
      //   120: astore 6
      //   122: iload 5
      //   124: ifeq +27 -> 151
      //   127: new 109	java/lang/Exception
      //   130: dup
      //   131: ldc 229
      //   133: iconst_1
      //   134: anewarray 4	java/lang/Object
      //   137: dup
      //   138: iconst_0
      //   139: aload_1
      //   140: aastore
      //   141: invokestatic 232	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   144: invokespecial 235	java/lang/Exception:<init>	(Ljava/lang/String;)V
      //   147: athrow
      //   148: aload_1
      //   149: astore 6
      //   151: iconst_0
      //   152: istore 4
      //   154: iload 4
      //   156: aload_0
      //   157: getfield 77	org/vidogram/ui/Components/WebPlayerView$JSExtractor:assign_operators	[Ljava/lang/String;
      //   160: arraylength
      //   161: if_icmpge +110 -> 271
      //   164: aload_0
      //   165: getfield 77	org/vidogram/ui/Components/WebPlayerView$JSExtractor:assign_operators	[Ljava/lang/String;
      //   168: iload 4
      //   170: aaload
      //   171: astore_1
      //   172: getstatic 120	java/util/Locale:US	Ljava/util/Locale;
      //   175: ldc 237
      //   177: iconst_2
      //   178: anewarray 4	java/lang/Object
      //   181: dup
      //   182: iconst_0
      //   183: ldc 239
      //   185: aastore
      //   186: dup
      //   187: iconst_1
      //   188: aload_1
      //   189: invokestatic 114	java/util/regex/Pattern:quote	(Ljava/lang/String;)Ljava/lang/String;
      //   192: aastore
      //   193: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   196: invokestatic 130	java/util/regex/Pattern:compile	(Ljava/lang/String;)Ljava/util/regex/Pattern;
      //   199: aload 6
      //   201: invokevirtual 134	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
      //   204: astore_1
      //   205: aload_1
      //   206: invokevirtual 140	java/util/regex/Matcher:find	()Z
      //   209: ifne +12 -> 221
      //   212: iload 4
      //   214: iconst_1
      //   215: iadd
      //   216: istore 4
      //   218: goto -64 -> 154
      //   221: aload_0
      //   222: aload_1
      //   223: iconst_3
      //   224: invokevirtual 164	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   227: aload_2
      //   228: iload_3
      //   229: iconst_1
      //   230: isub
      //   231: invokespecial 222	org/vidogram/ui/Components/WebPlayerView$JSExtractor:interpretExpression	(Ljava/lang/String;Ljava/util/HashMap;I)V
      //   234: aload_1
      //   235: iconst_2
      //   236: invokevirtual 164	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   239: astore 6
      //   241: aload 6
      //   243: invokestatic 191	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
      //   246: ifne +12 -> 258
      //   249: aload_0
      //   250: aload 6
      //   252: aload_2
      //   253: iload_3
      //   254: invokespecial 222	org/vidogram/ui/Components/WebPlayerView$JSExtractor:interpretExpression	(Ljava/lang/String;Ljava/util/HashMap;I)V
      //   257: return
      //   258: aload_2
      //   259: aload_1
      //   260: iconst_1
      //   261: invokevirtual 164	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   264: ldc 93
      //   266: invokevirtual 97	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      //   269: pop
      //   270: return
      //   271: aload 6
      //   273: invokestatic 245	java/lang/Integer:parseInt	(Ljava/lang/String;)I
      //   276: pop
      //   277: return
      //   278: astore_1
      //   279: getstatic 120	java/util/Locale:US	Ljava/util/Locale;
      //   282: ldc 247
      //   284: iconst_1
      //   285: anewarray 4	java/lang/Object
      //   288: dup
      //   289: iconst_0
      //   290: ldc 239
      //   292: aastore
      //   293: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   296: invokestatic 130	java/util/regex/Pattern:compile	(Ljava/lang/String;)Ljava/util/regex/Pattern;
      //   299: aload 6
      //   301: invokevirtual 134	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
      //   304: invokevirtual 140	java/util/regex/Matcher:find	()Z
      //   307: ifne -295 -> 12
      //   310: aload 6
      //   312: iconst_0
      //   313: invokevirtual 204	java/lang/String:charAt	(I)C
      //   316: bipush 34
      //   318: if_icmpne +20 -> 338
      //   321: aload 6
      //   323: aload 6
      //   325: invokevirtual 250	java/lang/String:length	()I
      //   328: iconst_1
      //   329: isub
      //   330: invokevirtual 204	java/lang/String:charAt	(I)C
      //   333: bipush 34
      //   335: if_icmpeq -323 -> 12
      //   338: new 252	org/json/JSONObject
      //   341: dup
      //   342: aload 6
      //   344: invokespecial 253	org/json/JSONObject:<init>	(Ljava/lang/String;)V
      //   347: invokevirtual 254	org/json/JSONObject:toString	()Ljava/lang/String;
      //   350: pop
      //   351: return
      //   352: astore_1
      //   353: getstatic 120	java/util/Locale:US	Ljava/util/Locale;
      //   356: ldc_w 256
      //   359: iconst_1
      //   360: anewarray 4	java/lang/Object
      //   363: dup
      //   364: iconst_0
      //   365: ldc 239
      //   367: aastore
      //   368: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   371: invokestatic 130	java/util/regex/Pattern:compile	(Ljava/lang/String;)Ljava/util/regex/Pattern;
      //   374: aload 6
      //   376: invokevirtual 134	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
      //   379: astore 7
      //   381: aload 7
      //   383: invokevirtual 140	java/util/regex/Matcher:find	()Z
      //   386: ifeq +117 -> 503
      //   389: aload 7
      //   391: iconst_1
      //   392: invokevirtual 164	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   395: astore_1
      //   396: aload 7
      //   398: iconst_2
      //   399: invokevirtual 164	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   402: pop
      //   403: aload 7
      //   405: iconst_3
      //   406: invokevirtual 164	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   409: astore 7
      //   411: aload_2
      //   412: aload_1
      //   413: invokevirtual 260	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   416: ifnonnull +9 -> 425
      //   419: aload_0
      //   420: aload_1
      //   421: invokespecial 262	org/vidogram/ui/Components/WebPlayerView$JSExtractor:extractObject	(Ljava/lang/String;)Ljava/util/HashMap;
      //   424: pop
      //   425: aload 7
      //   427: ifnull -415 -> 12
      //   430: aload 6
      //   432: aload 6
      //   434: invokevirtual 250	java/lang/String:length	()I
      //   437: iconst_1
      //   438: isub
      //   439: invokevirtual 204	java/lang/String:charAt	(I)C
      //   442: bipush 41
      //   444: if_icmpeq +14 -> 458
      //   447: new 109	java/lang/Exception
      //   450: dup
      //   451: ldc_w 264
      //   454: invokespecial 235	java/lang/Exception:<init>	(Ljava/lang/String;)V
      //   457: athrow
      //   458: aload 7
      //   460: invokevirtual 250	java/lang/String:length	()I
      //   463: ifeq -451 -> 12
      //   466: aload 7
      //   468: ldc 166
      //   470: invokevirtual 103	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
      //   473: astore_1
      //   474: iconst_0
      //   475: istore 4
      //   477: iload 4
      //   479: aload_1
      //   480: arraylength
      //   481: if_icmpge -469 -> 12
      //   484: aload_0
      //   485: aload_1
      //   486: iload 4
      //   488: aaload
      //   489: aload_2
      //   490: iload_3
      //   491: invokespecial 222	org/vidogram/ui/Components/WebPlayerView$JSExtractor:interpretExpression	(Ljava/lang/String;Ljava/util/HashMap;I)V
      //   494: iload 4
      //   496: iconst_1
      //   497: iadd
      //   498: istore 4
      //   500: goto -23 -> 477
      //   503: getstatic 120	java/util/Locale:US	Ljava/util/Locale;
      //   506: ldc_w 266
      //   509: iconst_1
      //   510: anewarray 4	java/lang/Object
      //   513: dup
      //   514: iconst_0
      //   515: ldc 239
      //   517: aastore
      //   518: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   521: invokestatic 130	java/util/regex/Pattern:compile	(Ljava/lang/String;)Ljava/util/regex/Pattern;
      //   524: aload 6
      //   526: invokevirtual 134	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
      //   529: astore_1
      //   530: aload_1
      //   531: invokevirtual 140	java/util/regex/Matcher:find	()Z
      //   534: ifeq +27 -> 561
      //   537: aload_2
      //   538: aload_1
      //   539: iconst_1
      //   540: invokevirtual 164	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   543: invokevirtual 260	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   546: pop
      //   547: aload_0
      //   548: aload_1
      //   549: iconst_2
      //   550: invokevirtual 164	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   553: aload_2
      //   554: iload_3
      //   555: iconst_1
      //   556: isub
      //   557: invokespecial 222	org/vidogram/ui/Components/WebPlayerView$JSExtractor:interpretExpression	(Ljava/lang/String;Ljava/util/HashMap;I)V
      //   560: return
      //   561: iconst_0
      //   562: istore 4
      //   564: iload 4
      //   566: aload_0
      //   567: getfield 53	org/vidogram/ui/Components/WebPlayerView$JSExtractor:operators	[Ljava/lang/String;
      //   570: arraylength
      //   571: if_icmpge +163 -> 734
      //   574: aload_0
      //   575: getfield 53	org/vidogram/ui/Components/WebPlayerView$JSExtractor:operators	[Ljava/lang/String;
      //   578: iload 4
      //   580: aaload
      //   581: astore_1
      //   582: getstatic 120	java/util/Locale:US	Ljava/util/Locale;
      //   585: ldc_w 268
      //   588: iconst_1
      //   589: anewarray 4	java/lang/Object
      //   592: dup
      //   593: iconst_0
      //   594: aload_1
      //   595: invokestatic 114	java/util/regex/Pattern:quote	(Ljava/lang/String;)Ljava/lang/String;
      //   598: aastore
      //   599: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   602: invokestatic 130	java/util/regex/Pattern:compile	(Ljava/lang/String;)Ljava/util/regex/Pattern;
      //   605: aload 6
      //   607: invokevirtual 134	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
      //   610: astore 7
      //   612: aload 7
      //   614: invokevirtual 140	java/util/regex/Matcher:find	()Z
      //   617: ifne +12 -> 629
      //   620: iload 4
      //   622: iconst_1
      //   623: iadd
      //   624: istore 4
      //   626: goto -62 -> 564
      //   629: iconst_1
      //   630: newarray boolean
      //   632: astore 8
      //   634: aload_0
      //   635: aload 7
      //   637: iconst_1
      //   638: invokevirtual 164	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   641: aload_2
      //   642: aload 8
      //   644: iload_3
      //   645: iconst_1
      //   646: isub
      //   647: invokespecial 107	org/vidogram/ui/Components/WebPlayerView$JSExtractor:interpretStatement	(Ljava/lang/String;Ljava/util/HashMap;[ZI)V
      //   650: aload 8
      //   652: iconst_0
      //   653: baload
      //   654: ifeq +30 -> 684
      //   657: new 109	java/lang/Exception
      //   660: dup
      //   661: ldc_w 270
      //   664: iconst_2
      //   665: anewarray 4	java/lang/Object
      //   668: dup
      //   669: iconst_0
      //   670: aload_1
      //   671: aastore
      //   672: dup
      //   673: iconst_1
      //   674: aload 6
      //   676: aastore
      //   677: invokestatic 232	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   680: invokespecial 235	java/lang/Exception:<init>	(Ljava/lang/String;)V
      //   683: athrow
      //   684: aload_0
      //   685: aload 7
      //   687: iconst_2
      //   688: invokevirtual 164	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   691: aload_2
      //   692: aload 8
      //   694: iload_3
      //   695: iconst_1
      //   696: isub
      //   697: invokespecial 107	org/vidogram/ui/Components/WebPlayerView$JSExtractor:interpretStatement	(Ljava/lang/String;Ljava/util/HashMap;[ZI)V
      //   700: aload 8
      //   702: iconst_0
      //   703: baload
      //   704: ifeq -84 -> 620
      //   707: new 109	java/lang/Exception
      //   710: dup
      //   711: ldc_w 272
      //   714: iconst_2
      //   715: anewarray 4	java/lang/Object
      //   718: dup
      //   719: iconst_0
      //   720: aload_1
      //   721: aastore
      //   722: dup
      //   723: iconst_1
      //   724: aload 6
      //   726: aastore
      //   727: invokestatic 232	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   730: invokespecial 235	java/lang/Exception:<init>	(Ljava/lang/String;)V
      //   733: athrow
      //   734: getstatic 120	java/util/Locale:US	Ljava/util/Locale;
      //   737: ldc_w 274
      //   740: iconst_1
      //   741: anewarray 4	java/lang/Object
      //   744: dup
      //   745: iconst_0
      //   746: ldc 239
      //   748: aastore
      //   749: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   752: invokestatic 130	java/util/regex/Pattern:compile	(Ljava/lang/String;)Ljava/util/regex/Pattern;
      //   755: aload 6
      //   757: invokevirtual 134	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
      //   760: astore_1
      //   761: aload_1
      //   762: invokevirtual 140	java/util/regex/Matcher:find	()Z
      //   765: ifeq +13 -> 778
      //   768: aload_0
      //   769: aload_1
      //   770: iconst_1
      //   771: invokevirtual 164	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   774: invokespecial 86	org/vidogram/ui/Components/WebPlayerView$JSExtractor:extractFunction	(Ljava/lang/String;)Ljava/lang/String;
      //   777: pop
      //   778: new 109	java/lang/Exception
      //   781: dup
      //   782: ldc_w 276
      //   785: iconst_1
      //   786: anewarray 4	java/lang/Object
      //   789: dup
      //   790: iconst_0
      //   791: aload 6
      //   793: aastore
      //   794: invokestatic 232	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   797: invokespecial 235	java/lang/Exception:<init>	(Ljava/lang/String;)V
      //   800: athrow
      //   801: iload 4
      //   803: istore 5
      //   805: goto -686 -> 119
      //
      // Exception table:
      //   from	to	target	type
      //   271	277	278	java/lang/Exception
      //   338	351	352	java/lang/Exception
    }

    private void interpretStatement(String paramString, HashMap<String, String> paramHashMap, boolean[] paramArrayOfBoolean, int paramInt)
    {
      if (paramInt < 0)
        throw new Exception("recursion limit reached");
      paramArrayOfBoolean[0] = false;
      String str = paramString.trim();
      paramString = WebPlayerView.stmtVarPattern.matcher(str);
      if (paramString.find())
        paramString = str.substring(paramString.group(0).length());
      while (true)
      {
        interpretExpression(paramString, paramHashMap, paramInt);
        return;
        Matcher localMatcher = WebPlayerView.stmtReturnPattern.matcher(str);
        paramString = str;
        if (!localMatcher.find())
          continue;
        paramString = str.substring(localMatcher.group(0).length());
        paramArrayOfBoolean[0] = true;
      }
    }
  }

  public class JavaScriptInterface
  {
    private final WebPlayerView.CallJavaResultInterface callJavaResultInterface;

    public JavaScriptInterface(WebPlayerView.CallJavaResultInterface arg2)
    {
      Object localObject;
      this.callJavaResultInterface = localObject;
    }

    @JavascriptInterface
    public void returnResultToJava(String paramString)
    {
      this.callJavaResultInterface.jsCallFinished(paramString);
    }
  }

  private class VimeoVideoTask extends AsyncTask<Void, Void, String>
  {
    private boolean canRetry = true;
    private String[] results = new String[2];
    private String videoId;

    public VimeoVideoTask(String arg2)
    {
      Object localObject;
      this.videoId = localObject;
    }

    protected String doInBackground(Void[] paramArrayOfVoid)
    {
      paramArrayOfVoid = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://player.vimeo.com/video/%s/config", new Object[] { this.videoId }));
      if (isCancelled());
      while (true)
      {
        return null;
        try
        {
          paramArrayOfVoid = new JSONObject(paramArrayOfVoid).getJSONObject("request").getJSONObject("files");
          if (paramArrayOfVoid.has("hls"))
          {
            paramArrayOfVoid = paramArrayOfVoid.getJSONObject("hls");
            try
            {
              this.results[0] = paramArrayOfVoid.getString("url");
              this.results[1] = "hls";
              if (isCancelled())
                continue;
              return this.results[0];
            }
            catch (Exception str)
            {
              while (true)
              {
                String str = paramArrayOfVoid.getString("default_cdn");
                paramArrayOfVoid = paramArrayOfVoid.getJSONObject("cdns").getJSONObject(str);
                this.results[0] = paramArrayOfVoid.getString("url");
              }
            }
          }
        }
        catch (Exception paramArrayOfVoid)
        {
          while (true)
          {
            FileLog.e(paramArrayOfVoid);
            continue;
            if (!paramArrayOfVoid.has("progressive"))
              continue;
            this.results[1] = "other";
            paramArrayOfVoid = paramArrayOfVoid.getJSONArray("progressive");
            if (paramArrayOfVoid.length() >= 0)
              continue;
            paramArrayOfVoid = paramArrayOfVoid.getJSONObject(0);
            this.results[0] = paramArrayOfVoid.getString("url");
          }
        }
      }
    }

    protected void onPostExecute(String paramString)
    {
      if (paramString != null)
      {
        WebPlayerView.access$1702(WebPlayerView.this, true);
        WebPlayerView.access$1902(WebPlayerView.this, paramString);
        WebPlayerView.access$1802(WebPlayerView.this, this.results[1]);
        if (WebPlayerView.this.isAutoplay)
          WebPlayerView.this.preparePlayer();
        WebPlayerView.this.showProgress(false, true);
        WebPlayerView.this.controlsView.show(true, true);
      }
      do
        return;
      while (isCancelled());
      WebPlayerView.this.onInitFailed();
    }
  }

  public static abstract interface WebPlayerViewDelegate
  {
    public abstract boolean checkInlinePermissons();

    public abstract ViewGroup getTextureViewContainer();

    public abstract void onInitFailed();

    public abstract void onInlineSurfaceTextureReady();

    public abstract void onPlayStateChanged(WebPlayerView paramWebPlayerView, boolean paramBoolean);

    public abstract void onSharePressed();

    public abstract TextureView onSwitchInlineMode(View paramView, boolean paramBoolean1, float paramFloat, int paramInt, boolean paramBoolean2);

    public abstract TextureView onSwitchToFullscreen(View paramView, boolean paramBoolean1, float paramFloat, int paramInt, boolean paramBoolean2);

    public abstract void onVideoSizeChanged(float paramFloat, int paramInt);

    public abstract void prepareToSwitchInlineMode(boolean paramBoolean1, Runnable paramRunnable, float paramFloat, boolean paramBoolean2);
  }

  private class YoutubeVideoTask extends AsyncTask<Void, Void, String>
  {
    private boolean canRetry = true;
    private String[] result = new String[1];
    private Semaphore semaphore = new Semaphore(0);
    private String sig;
    private String videoId;

    public YoutubeVideoTask(String arg2)
    {
      Object localObject;
      this.videoId = localObject;
    }

    private void onInterfaceResult(String paramString)
    {
      this.result[0] = this.result[0].replace(this.sig, "/signature/" + paramString);
      this.semaphore.release();
    }

    // ERROR //
    protected String doInBackground(Void[] paramArrayOfVoid)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 27	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:this$0	Lorg/vidogram/ui/Components/WebPlayerView;
      //   4: aload_0
      //   5: new 62	java/lang/StringBuilder
      //   8: dup
      //   9: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   12: ldc 91
      //   14: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   17: aload_0
      //   18: getfield 45	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:videoId	Ljava/lang/String;
      //   21: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   24: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   27: invokevirtual 95	org/vidogram/ui/Components/WebPlayerView:downloadUrlContent	(Landroid/os/AsyncTask;Ljava/lang/String;)Ljava/lang/String;
      //   30: astore 9
      //   32: aload_0
      //   33: invokevirtual 99	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:isCancelled	()Z
      //   36: ifeq +5 -> 41
      //   39: aconst_null
      //   40: areturn
      //   41: new 62	java/lang/StringBuilder
      //   44: dup
      //   45: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   48: ldc 101
      //   50: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   53: aload_0
      //   54: getfield 45	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:videoId	Ljava/lang/String;
      //   57: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   60: ldc 103
      //   62: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   65: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   68: astore 8
      //   70: new 62	java/lang/StringBuilder
      //   73: dup
      //   74: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   77: aload 8
      //   79: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   82: ldc 105
      //   84: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   87: new 62	java/lang/StringBuilder
      //   90: dup
      //   91: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   94: ldc 107
      //   96: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   99: aload_0
      //   100: getfield 45	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:videoId	Ljava/lang/String;
      //   103: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   106: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   109: ldc 109
      //   111: invokestatic 115	java/net/URLEncoder:encode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
      //   114: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   117: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   120: astore_1
      //   121: aload_1
      //   122: astore 8
      //   124: aload 8
      //   126: astore_1
      //   127: aload 9
      //   129: ifnull +59 -> 188
      //   132: invokestatic 119	org/vidogram/ui/Components/WebPlayerView:access$600	()Ljava/util/regex/Pattern;
      //   135: aload 9
      //   137: invokevirtual 125	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
      //   140: astore_1
      //   141: aload_1
      //   142: invokevirtual 130	java/util/regex/Matcher:find	()Z
      //   145: ifeq +148 -> 293
      //   148: new 62	java/lang/StringBuilder
      //   151: dup
      //   152: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   155: aload 8
      //   157: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   160: ldc 132
      //   162: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   165: aload 9
      //   167: aload_1
      //   168: invokevirtual 136	java/util/regex/Matcher:start	()I
      //   171: bipush 6
      //   173: iadd
      //   174: aload_1
      //   175: invokevirtual 139	java/util/regex/Matcher:end	()I
      //   178: invokevirtual 143	java/lang/String:substring	(II)Ljava/lang/String;
      //   181: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   184: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   187: astore_1
      //   188: iconst_0
      //   189: istore_2
      //   190: iconst_5
      //   191: anewarray 41	java/lang/String
      //   194: astore 8
      //   196: aload 8
      //   198: iconst_0
      //   199: ldc 145
      //   201: aastore
      //   202: aload 8
      //   204: iconst_1
      //   205: ldc 147
      //   207: aastore
      //   208: aload 8
      //   210: iconst_2
      //   211: ldc 149
      //   213: aastore
      //   214: aload 8
      //   216: iconst_3
      //   217: ldc 151
      //   219: aastore
      //   220: aload 8
      //   222: iconst_4
      //   223: ldc 153
      //   225: aastore
      //   226: iconst_0
      //   227: istore 4
      //   229: iload_2
      //   230: istore_3
      //   231: iload 4
      //   233: aload 8
      //   235: arraylength
      //   236: if_icmpge +292 -> 528
      //   239: aload_0
      //   240: getfield 27	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:this$0	Lorg/vidogram/ui/Components/WebPlayerView;
      //   243: aload_0
      //   244: new 62	java/lang/StringBuilder
      //   247: dup
      //   248: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   251: ldc 155
      //   253: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   256: aload_1
      //   257: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   260: aload 8
      //   262: iload 4
      //   264: aaload
      //   265: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   268: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   271: invokevirtual 95	org/vidogram/ui/Components/WebPlayerView:downloadUrlContent	(Landroid/os/AsyncTask;Ljava/lang/String;)Ljava/lang/String;
      //   274: astore 10
      //   276: aload_0
      //   277: invokevirtual 99	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:isCancelled	()Z
      //   280: ifeq +37 -> 317
      //   283: aconst_null
      //   284: areturn
      //   285: astore_1
      //   286: aload_1
      //   287: invokestatic 161	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   290: goto -166 -> 124
      //   293: new 62	java/lang/StringBuilder
      //   296: dup
      //   297: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   300: aload 8
      //   302: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   305: ldc 132
      //   307: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   310: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   313: astore_1
      //   314: goto -126 -> 188
      //   317: aload 10
      //   319: ifnull +975 -> 1294
      //   322: aload 10
      //   324: ldc 163
      //   326: invokevirtual 167	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
      //   329: astore 10
      //   331: iconst_0
      //   332: istore 5
      //   334: iconst_0
      //   335: istore 6
      //   337: iload_2
      //   338: istore_3
      //   339: iload 5
      //   341: istore_2
      //   342: iload 6
      //   344: aload 10
      //   346: arraylength
      //   347: if_icmpge +169 -> 516
      //   350: aload 10
      //   352: iload 6
      //   354: aaload
      //   355: ldc 169
      //   357: invokevirtual 173	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   360: ifeq +82 -> 442
      //   363: iconst_1
      //   364: istore_2
      //   365: aload 10
      //   367: iload 6
      //   369: aaload
      //   370: ldc 175
      //   372: invokevirtual 167	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
      //   375: astore 11
      //   377: iload_2
      //   378: istore 5
      //   380: iload_3
      //   381: istore 7
      //   383: aload 11
      //   385: arraylength
      //   386: iconst_2
      //   387: if_icmpne +24 -> 411
      //   390: aload_0
      //   391: getfield 43	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:result	[Ljava/lang/String;
      //   394: iconst_0
      //   395: aload 11
      //   397: iconst_1
      //   398: aaload
      //   399: ldc 109
      //   401: invokestatic 180	java/net/URLDecoder:decode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
      //   404: aastore
      //   405: iload_3
      //   406: istore 7
      //   408: iload_2
      //   409: istore 5
      //   411: iload 6
      //   413: iconst_1
      //   414: iadd
      //   415: istore 6
      //   417: iload 5
      //   419: istore_2
      //   420: iload 7
      //   422: istore_3
      //   423: goto -81 -> 342
      //   426: astore 11
      //   428: aload 11
      //   430: invokestatic 161	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   433: iload_2
      //   434: istore 5
      //   436: iload_3
      //   437: istore 7
      //   439: goto -28 -> 411
      //   442: iload_2
      //   443: istore 5
      //   445: iload_3
      //   446: istore 7
      //   448: aload 10
      //   450: iload 6
      //   452: aaload
      //   453: ldc 182
      //   455: invokevirtual 173	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   458: ifeq -47 -> 411
      //   461: aload 10
      //   463: iload 6
      //   465: aaload
      //   466: ldc 175
      //   468: invokevirtual 167	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
      //   471: astore 11
      //   473: iload_2
      //   474: istore 5
      //   476: iload_3
      //   477: istore 7
      //   479: aload 11
      //   481: arraylength
      //   482: iconst_2
      //   483: if_icmpne -72 -> 411
      //   486: iload_2
      //   487: istore 5
      //   489: iload_3
      //   490: istore 7
      //   492: aload 11
      //   494: iconst_1
      //   495: aaload
      //   496: invokevirtual 185	java/lang/String:toLowerCase	()Ljava/lang/String;
      //   499: ldc 187
      //   501: invokevirtual 191	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   504: ifeq -93 -> 411
      //   507: iconst_1
      //   508: istore 7
      //   510: iload_2
      //   511: istore 5
      //   513: goto -102 -> 411
      //   516: iload_2
      //   517: istore 5
      //   519: iload_3
      //   520: istore_2
      //   521: iload 5
      //   523: ifeq +343 -> 866
      //   526: iload_2
      //   527: istore_3
      //   528: iload_3
      //   529: istore_2
      //   530: aload_0
      //   531: getfield 43	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:result	[Ljava/lang/String;
      //   534: iconst_0
      //   535: aaload
      //   536: ifnull +595 -> 1131
      //   539: iload_3
      //   540: ifne +19 -> 559
      //   543: iload_3
      //   544: istore_2
      //   545: aload_0
      //   546: getfield 43	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:result	[Ljava/lang/String;
      //   549: iconst_0
      //   550: aaload
      //   551: ldc 193
      //   553: invokevirtual 197	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   556: ifeq +575 -> 1131
      //   559: iload_3
      //   560: istore_2
      //   561: aload 9
      //   563: ifnull +568 -> 1131
      //   566: aload_0
      //   567: getfield 43	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:result	[Ljava/lang/String;
      //   570: iconst_0
      //   571: aaload
      //   572: ldc 193
      //   574: invokevirtual 201	java/lang/String:indexOf	(Ljava/lang/String;)I
      //   577: istore 4
      //   579: aload_0
      //   580: getfield 43	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:result	[Ljava/lang/String;
      //   583: iconst_0
      //   584: aaload
      //   585: bipush 47
      //   587: iload 4
      //   589: bipush 10
      //   591: iadd
      //   592: invokevirtual 204	java/lang/String:indexOf	(II)I
      //   595: istore_3
      //   596: iload 4
      //   598: iconst_m1
      //   599: if_icmpeq +656 -> 1255
      //   602: iload_3
      //   603: istore_2
      //   604: iload_3
      //   605: iconst_m1
      //   606: if_icmpne +13 -> 619
      //   609: aload_0
      //   610: getfield 43	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:result	[Ljava/lang/String;
      //   613: iconst_0
      //   614: aaload
      //   615: invokevirtual 207	java/lang/String:length	()I
      //   618: istore_2
      //   619: aload_0
      //   620: aload_0
      //   621: getfield 43	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:result	[Ljava/lang/String;
      //   624: iconst_0
      //   625: aaload
      //   626: iload 4
      //   628: iload_2
      //   629: invokevirtual 143	java/lang/String:substring	(II)Ljava/lang/String;
      //   632: putfield 52	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:sig	Ljava/lang/String;
      //   635: aconst_null
      //   636: astore_1
      //   637: invokestatic 210	org/vidogram/ui/Components/WebPlayerView:access$700	()Ljava/util/regex/Pattern;
      //   640: aload 9
      //   642: invokevirtual 125	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
      //   645: astore 8
      //   647: aload_1
      //   648: astore 9
      //   650: aload 8
      //   652: invokevirtual 130	java/util/regex/Matcher:find	()Z
      //   655: ifeq +42 -> 697
      //   658: new 212	org/json/JSONTokener
      //   661: dup
      //   662: aload 8
      //   664: iconst_1
      //   665: invokevirtual 216	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   668: invokespecial 218	org/json/JSONTokener:<init>	(Ljava/lang/String;)V
      //   671: invokevirtual 222	org/json/JSONTokener:nextValue	()Ljava/lang/Object;
      //   674: astore 8
      //   676: aload 8
      //   678: instanceof 41
      //   681: ifeq +608 -> 1289
      //   684: aload 8
      //   686: checkcast 41	java/lang/String
      //   689: astore 8
      //   691: aload 8
      //   693: astore_1
      //   694: aload_1
      //   695: astore 9
      //   697: aload 9
      //   699: ifnull +556 -> 1255
      //   702: invokestatic 225	org/vidogram/ui/Components/WebPlayerView:access$800	()Ljava/util/regex/Pattern;
      //   705: aload 9
      //   707: invokevirtual 125	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
      //   710: astore_1
      //   711: aload_1
      //   712: invokevirtual 130	java/util/regex/Matcher:find	()Z
      //   715: ifeq +173 -> 888
      //   718: new 62	java/lang/StringBuilder
      //   721: dup
      //   722: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   725: aload_1
      //   726: iconst_1
      //   727: invokevirtual 216	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   730: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   733: aload_1
      //   734: iconst_2
      //   735: invokevirtual 216	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   738: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   741: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   744: astore 11
      //   746: aconst_null
      //   747: astore 8
      //   749: aconst_null
      //   750: astore_1
      //   751: getstatic 231	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   754: ldc 233
      //   756: iconst_0
      //   757: invokevirtual 239	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
      //   760: astore 12
      //   762: aload 11
      //   764: ifnull +44 -> 808
      //   767: aload 12
      //   769: aload 11
      //   771: aconst_null
      //   772: invokeinterface 244 3 0
      //   777: astore 8
      //   779: aload 12
      //   781: new 62	java/lang/StringBuilder
      //   784: dup
      //   785: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   788: aload 11
      //   790: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   793: ldc 246
      //   795: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   798: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   801: aconst_null
      //   802: invokeinterface 244 3 0
      //   807: astore_1
      //   808: aload 8
      //   810: ifnonnull +476 -> 1286
      //   813: aload 9
      //   815: ldc 248
      //   817: invokevirtual 173	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   820: ifeq +74 -> 894
      //   823: new 62	java/lang/StringBuilder
      //   826: dup
      //   827: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   830: ldc 250
      //   832: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   835: aload 9
      //   837: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   840: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   843: astore 10
      //   845: aload_0
      //   846: getfield 27	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:this$0	Lorg/vidogram/ui/Components/WebPlayerView;
      //   849: aload_0
      //   850: aload 10
      //   852: invokevirtual 95	org/vidogram/ui/Components/WebPlayerView:downloadUrlContent	(Landroid/os/AsyncTask;Ljava/lang/String;)Ljava/lang/String;
      //   855: astore 9
      //   857: aload_0
      //   858: invokevirtual 99	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:isCancelled	()Z
      //   861: ifeq +72 -> 933
      //   864: aconst_null
      //   865: areturn
      //   866: iload 4
      //   868: iconst_1
      //   869: iadd
      //   870: istore 4
      //   872: goto -643 -> 229
      //   875: astore 8
      //   877: aload 8
      //   879: invokestatic 161	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   882: aload_1
      //   883: astore 9
      //   885: goto -188 -> 697
      //   888: aconst_null
      //   889: astore 11
      //   891: goto -145 -> 746
      //   894: aload 9
      //   896: astore 10
      //   898: aload 9
      //   900: ldc 252
      //   902: invokevirtual 173	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   905: ifeq -60 -> 845
      //   908: new 62	java/lang/StringBuilder
      //   911: dup
      //   912: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   915: ldc 254
      //   917: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   920: aload 9
      //   922: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   925: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   928: astore 10
      //   930: goto -85 -> 845
      //   933: aload 9
      //   935: ifnull +351 -> 1286
      //   938: invokestatic 257	org/vidogram/ui/Components/WebPlayerView:access$900	()Ljava/util/regex/Pattern;
      //   941: aload 9
      //   943: invokevirtual 125	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
      //   946: astore 10
      //   948: aload 10
      //   950: invokevirtual 130	java/util/regex/Matcher:find	()Z
      //   953: ifeq +191 -> 1144
      //   956: aload 10
      //   958: iconst_1
      //   959: invokevirtual 216	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   962: astore_1
      //   963: aload_1
      //   964: ifnull +316 -> 1280
      //   967: new 259	org/vidogram/ui/Components/WebPlayerView$JSExtractor
      //   970: dup
      //   971: aload_0
      //   972: getfield 27	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:this$0	Lorg/vidogram/ui/Components/WebPlayerView;
      //   975: aload 9
      //   977: invokespecial 261	org/vidogram/ui/Components/WebPlayerView$JSExtractor:<init>	(Lorg/vidogram/ui/Components/WebPlayerView;Ljava/lang/String;)V
      //   980: aload_1
      //   981: invokestatic 265	org/vidogram/ui/Components/WebPlayerView$JSExtractor:access$1100	(Lorg/vidogram/ui/Components/WebPlayerView$JSExtractor;Ljava/lang/String;)Ljava/lang/String;
      //   984: astore 9
      //   986: aload 9
      //   988: invokestatic 270	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
      //   991: ifne +56 -> 1047
      //   994: aload 11
      //   996: ifnull +51 -> 1047
      //   999: aload 12
      //   1001: invokeinterface 274 1 0
      //   1006: aload 11
      //   1008: aload 9
      //   1010: invokeinterface 280 3 0
      //   1015: new 62	java/lang/StringBuilder
      //   1018: dup
      //   1019: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   1022: aload 11
      //   1024: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1027: ldc 246
      //   1029: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1032: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1035: aload_1
      //   1036: invokeinterface 280 3 0
      //   1041: invokeinterface 283 1 0
      //   1046: pop
      //   1047: aload 9
      //   1049: astore 8
      //   1051: aload 8
      //   1053: invokestatic 270	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
      //   1056: ifne +199 -> 1255
      //   1059: getstatic 289	android/os/Build$VERSION:SDK_INT	I
      //   1062: bipush 21
      //   1064: if_icmplt +118 -> 1182
      //   1067: new 62	java/lang/StringBuilder
      //   1070: dup
      //   1071: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   1074: aload 8
      //   1076: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1079: aload_1
      //   1080: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1083: ldc_w 291
      //   1086: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1089: aload_0
      //   1090: getfield 52	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:sig	Ljava/lang/String;
      //   1093: iconst_3
      //   1094: invokevirtual 293	java/lang/String:substring	(I)Ljava/lang/String;
      //   1097: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1100: ldc_w 295
      //   1103: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1106: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1109: astore_1
      //   1110: new 10	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask$1
      //   1113: dup
      //   1114: aload_0
      //   1115: aload_1
      //   1116: invokespecial 297	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask$1:<init>	(Lorg/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask;Ljava/lang/String;)V
      //   1119: invokestatic 303	org/vidogram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
      //   1122: aload_0
      //   1123: getfield 39	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:semaphore	Ljava/util/concurrent/Semaphore;
      //   1126: invokevirtual 306	java/util/concurrent/Semaphore:acquire	()V
      //   1129: iconst_0
      //   1130: istore_2
      //   1131: aload_0
      //   1132: invokevirtual 99	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:isCancelled	()Z
      //   1135: ifne +7 -> 1142
      //   1138: iload_2
      //   1139: ifeq +121 -> 1260
      //   1142: aconst_null
      //   1143: areturn
      //   1144: invokestatic 309	org/vidogram/ui/Components/WebPlayerView:access$1000	()Ljava/util/regex/Pattern;
      //   1147: aload 9
      //   1149: invokevirtual 125	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
      //   1152: astore 10
      //   1154: aload 10
      //   1156: invokevirtual 130	java/util/regex/Matcher:find	()Z
      //   1159: ifeq +124 -> 1283
      //   1162: aload 10
      //   1164: iconst_1
      //   1165: invokevirtual 216	java/util/regex/Matcher:group	(I)Ljava/lang/String;
      //   1168: astore_1
      //   1169: goto -206 -> 963
      //   1172: astore 9
      //   1174: aload 9
      //   1176: invokestatic 161	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   1179: goto -128 -> 1051
      //   1182: new 62	java/lang/StringBuilder
      //   1185: dup
      //   1186: invokespecial 63	java/lang/StringBuilder:<init>	()V
      //   1189: aload 8
      //   1191: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1194: ldc_w 311
      //   1197: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1200: aload_0
      //   1201: getfield 27	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:this$0	Lorg/vidogram/ui/Components/WebPlayerView;
      //   1204: invokestatic 315	org/vidogram/ui/Components/WebPlayerView:access$1200	(Lorg/vidogram/ui/Components/WebPlayerView;)Ljava/lang/String;
      //   1207: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1210: ldc_w 317
      //   1213: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1216: aload_1
      //   1217: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1220: ldc_w 291
      //   1223: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1226: aload_0
      //   1227: getfield 52	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:sig	Ljava/lang/String;
      //   1230: iconst_3
      //   1231: invokevirtual 293	java/lang/String:substring	(I)Ljava/lang/String;
      //   1234: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1237: ldc_w 319
      //   1240: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1243: invokevirtual 73	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   1246: astore_1
      //   1247: goto -137 -> 1110
      //   1250: astore_1
      //   1251: aload_1
      //   1252: invokestatic 161	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   1255: iconst_1
      //   1256: istore_2
      //   1257: goto -126 -> 1131
      //   1260: aload_0
      //   1261: getfield 43	org/vidogram/ui/Components/WebPlayerView$YoutubeVideoTask:result	[Ljava/lang/String;
      //   1264: iconst_0
      //   1265: aaload
      //   1266: areturn
      //   1267: astore 10
      //   1269: aload 9
      //   1271: astore 8
      //   1273: aload 10
      //   1275: astore 9
      //   1277: goto -103 -> 1174
      //   1280: goto -229 -> 1051
      //   1283: goto -320 -> 963
      //   1286: goto -235 -> 1051
      //   1289: aconst_null
      //   1290: astore_1
      //   1291: goto -597 -> 694
      //   1294: iconst_0
      //   1295: istore 5
      //   1297: goto -776 -> 521
      //
      // Exception table:
      //   from	to	target	type
      //   70	121	285	java/lang/Exception
      //   390	405	426	java/lang/Exception
      //   658	691	875	java/lang/Exception
      //   967	986	1172	java/lang/Exception
      //   1110	1129	1250	java/lang/Exception
      //   986	994	1267	java/lang/Exception
      //   999	1047	1267	java/lang/Exception
    }

    protected void onPostExecute(String paramString)
    {
      if (paramString != null)
      {
        WebPlayerView.access$1702(WebPlayerView.this, true);
        WebPlayerView.access$1802(WebPlayerView.this, "dash");
        WebPlayerView.access$1902(WebPlayerView.this, paramString);
        if (WebPlayerView.this.isAutoplay)
          WebPlayerView.this.preparePlayer();
        WebPlayerView.this.showProgress(false, true);
        WebPlayerView.this.controlsView.show(true, true);
      }
      do
        return;
      while (isCancelled());
      WebPlayerView.this.onInitFailed();
    }
  }

  private abstract class function
  {
    private function()
    {
    }

    public abstract Object run(Object[] paramArrayOfObject);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.WebPlayerView
 * JD-Core Version:    0.6.0
 */