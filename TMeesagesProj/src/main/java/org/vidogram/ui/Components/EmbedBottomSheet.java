package org.vidogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.text.TextUtils.TruncateAt;
import android.view.Display;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.BringAppForegroundService;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.browser.Browser;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BottomSheet;
import org.vidogram.ui.ActionBar.BottomSheet.BottomSheetDelegate;
import org.vidogram.ui.ActionBar.BottomSheet.ContainerView;
import org.vidogram.ui.ActionBar.Theme;

@TargetApi(16)
public class EmbedBottomSheet extends BottomSheet
{

  @SuppressLint({"StaticFieldLeak"})
  private static EmbedBottomSheet instance;
  private boolean animationInProgress;
  private View customView;
  private WebChromeClient.CustomViewCallback customViewCallback;
  private String embedUrl;
  private FrameLayout fullscreenVideoContainer;
  private boolean fullscreenedByButton;
  private boolean hasDescription;
  private int height;
  private int lastOrientation = -1;
  private DialogInterface.OnShowListener onShowListener = new DialogInterface.OnShowListener()
  {
    public void onShow(DialogInterface paramDialogInterface)
    {
      if ((EmbedBottomSheet.this.pipVideoView != null) && (EmbedBottomSheet.this.videoView.isInline()))
        EmbedBottomSheet.this.videoView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
          public boolean onPreDraw()
          {
            EmbedBottomSheet.this.videoView.getViewTreeObserver().removeOnPreDrawListener(this);
            return true;
          }
        });
    }
  };
  private String openUrl;
  private OrientationEventListener orientationEventListener;
  private Activity parentActivity;
  private PipVideoView pipVideoView;
  private int[] position = new int[2];
  private int prevOrientation = -2;
  private RadialProgressView progressBar;
  private WebPlayerView videoView;
  private int waitingForDraw;
  private boolean wasInLandscape;
  private WebView webView;
  private int width;

  @SuppressLint({"SetJavaScriptEnabled"})
  private EmbedBottomSheet(Context paramContext, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2)
  {
    super(paramContext, false);
    this.fullWidth = true;
    setApplyTopPadding(false);
    setApplyBottomPadding(false);
    if ((paramContext instanceof Activity))
      this.parentActivity = ((Activity)paramContext);
    this.embedUrl = paramString4;
    boolean bool;
    if ((paramString2 != null) && (paramString2.length() > 0))
    {
      bool = true;
      this.hasDescription = bool;
      this.openUrl = paramString3;
      this.width = paramInt1;
      this.height = paramInt2;
      if ((this.width == 0) || (this.height == 0))
      {
        this.width = AndroidUtilities.displaySize.x;
        this.height = (AndroidUtilities.displaySize.y / 2);
      }
      this.fullscreenVideoContainer = new FrameLayout(paramContext);
      this.fullscreenVideoContainer.setBackgroundColor(-16777216);
      if (Build.VERSION.SDK_INT >= 21)
        this.fullscreenVideoContainer.setFitsSystemWindows(true);
      this.container.addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0F));
      this.fullscreenVideoContainer.setVisibility(4);
      this.fullscreenVideoContainer.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          return true;
        }
      });
      paramString3 = new FrameLayout(paramContext)
      {
        protected void onDetachedFromWindow()
        {
          super.onDetachedFromWindow();
          try
          {
            if (EmbedBottomSheet.this.webView.getParent() != null)
            {
              removeView(EmbedBottomSheet.this.webView);
              EmbedBottomSheet.this.webView.stopLoading();
              EmbedBottomSheet.this.webView.loadUrl("about:blank");
              EmbedBottomSheet.this.webView.destroy();
            }
            if (!EmbedBottomSheet.this.videoView.isInline())
            {
              if (EmbedBottomSheet.instance == EmbedBottomSheet.this)
                EmbedBottomSheet.access$302(null);
              EmbedBottomSheet.this.videoView.destroy();
            }
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }

        protected void onMeasure(int paramInt1, int paramInt2)
        {
          paramInt2 = View.MeasureSpec.getSize(paramInt1);
          float f = EmbedBottomSheet.this.width / paramInt2;
          int i = (int)Math.min(EmbedBottomSheet.this.height / f, AndroidUtilities.displaySize.y / 2);
          if (EmbedBottomSheet.this.hasDescription);
          for (paramInt2 = 22; ; paramInt2 = 0)
          {
            super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(paramInt2 + 84) + i + 1, 1073741824));
            return;
          }
        }
      };
      paramString3.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          return true;
        }
      });
      setCustomView(paramString3);
      this.webView = new WebView(paramContext);
      this.webView.getSettings().setJavaScriptEnabled(true);
      this.webView.getSettings().setDomStorageEnabled(true);
      if (Build.VERSION.SDK_INT >= 17)
        this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
      if (Build.VERSION.SDK_INT >= 21)
      {
        this.webView.getSettings().setMixedContentMode(0);
        CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
      }
      this.webView.setWebChromeClient(new WebChromeClient()
      {
        public void onHideCustomView()
        {
          super.onHideCustomView();
          if (EmbedBottomSheet.this.customView == null)
            return;
          EmbedBottomSheet.this.getSheetContainer().setVisibility(0);
          EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
          EmbedBottomSheet.this.fullscreenVideoContainer.removeView(EmbedBottomSheet.this.customView);
          if ((EmbedBottomSheet.this.customViewCallback != null) && (!EmbedBottomSheet.this.customViewCallback.getClass().getName().contains(".chromium.")))
            EmbedBottomSheet.this.customViewCallback.onCustomViewHidden();
          EmbedBottomSheet.access$702(EmbedBottomSheet.this, null);
        }

        public void onShowCustomView(View paramView, int paramInt, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
          onShowCustomView(paramView, paramCustomViewCallback);
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
          if (EmbedBottomSheet.this.customView != null)
          {
            paramCustomViewCallback.onCustomViewHidden();
            return;
          }
          EmbedBottomSheet.access$702(EmbedBottomSheet.this, paramView);
          EmbedBottomSheet.this.getSheetContainer().setVisibility(4);
          EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
          EmbedBottomSheet.this.fullscreenVideoContainer.addView(paramView, LayoutHelper.createFrame(-1, -1.0F));
          EmbedBottomSheet.access$902(EmbedBottomSheet.this, paramCustomViewCallback);
        }
      });
      this.webView.setWebViewClient(new WebViewClient()
      {
        public void onLoadResource(WebView paramWebView, String paramString)
        {
          super.onLoadResource(paramWebView, paramString);
        }

        public void onPageFinished(WebView paramWebView, String paramString)
        {
          super.onPageFinished(paramWebView, paramString);
          EmbedBottomSheet.this.progressBar.setVisibility(4);
        }
      });
      paramString4 = this.webView;
      if (!this.hasDescription)
        break label1253;
      paramInt1 = 22;
      label383: paramString3.addView(paramString4, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, paramInt1 + 84));
      this.videoView = new WebPlayerView(paramContext, true, false, new WebPlayerView.WebPlayerViewDelegate()
      {
        public boolean checkInlinePermissons()
        {
          if (EmbedBottomSheet.this.parentActivity == null)
            return false;
          if ((Build.VERSION.SDK_INT < 23) || (Settings.canDrawOverlays(EmbedBottomSheet.this.parentActivity)))
            return true;
          new AlertDialog.Builder(EmbedBottomSheet.this.parentActivity).setTitle(LocaleController.getString("AppName", 2131165319)).setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", 2131166254)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", 2131166260), new DialogInterface.OnClickListener()
          {
            @TargetApi(23)
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              if (EmbedBottomSheet.this.parentActivity != null)
                EmbedBottomSheet.this.parentActivity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + EmbedBottomSheet.this.parentActivity.getPackageName())));
            }
          }).show();
          return false;
        }

        public ViewGroup getTextureViewContainer()
        {
          return EmbedBottomSheet.this.container;
        }

        public void onInitFailed()
        {
          EmbedBottomSheet.this.webView.setVisibility(0);
          EmbedBottomSheet.this.videoView.setVisibility(4);
          EmbedBottomSheet.this.videoView.getControlsView().setVisibility(4);
          EmbedBottomSheet.this.videoView.getTextureView().setVisibility(4);
          if (EmbedBottomSheet.this.videoView.getTextureImageView() != null)
            EmbedBottomSheet.this.videoView.getTextureImageView().setVisibility(4);
          EmbedBottomSheet.this.videoView.loadVideo(null, null, null, false);
          HashMap localHashMap = new HashMap();
          localHashMap.put("Referer", "http://youtube.com");
          try
          {
            EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, localHashMap);
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }

        public void onInlineSurfaceTextureReady()
        {
          if (EmbedBottomSheet.this.videoView.isInline())
            EmbedBottomSheet.this.dismissInternal();
        }

        public void onPlayStateChanged(WebPlayerView paramWebPlayerView, boolean paramBoolean)
        {
          if (paramBoolean)
            try
            {
              EmbedBottomSheet.this.parentActivity.getWindow().addFlags(128);
              return;
            }
            catch (Exception paramWebPlayerView)
            {
              FileLog.e(paramWebPlayerView);
              return;
            }
          try
          {
            EmbedBottomSheet.this.parentActivity.getWindow().clearFlags(128);
            return;
          }
          catch (Exception paramWebPlayerView)
          {
            FileLog.e(paramWebPlayerView);
          }
        }

        public void onSharePressed()
        {
        }

        public TextureView onSwitchInlineMode(View paramView, boolean paramBoolean1, float paramFloat, int paramInt, boolean paramBoolean2)
        {
          if (paramBoolean1)
          {
            paramView.setTranslationY(0.0F);
            EmbedBottomSheet.access$002(EmbedBottomSheet.this, new PipVideoView());
            return EmbedBottomSheet.this.pipVideoView.show(EmbedBottomSheet.this.parentActivity, EmbedBottomSheet.this, paramView, paramFloat, paramInt);
          }
          if (paramBoolean2)
          {
            EmbedBottomSheet.access$3002(EmbedBottomSheet.this, true);
            EmbedBottomSheet.this.videoView.getAspectRatioView().getLocationInWindow(EmbedBottomSheet.this.position);
            paramView = EmbedBottomSheet.this.position;
            paramView[0] -= EmbedBottomSheet.access$3200(EmbedBottomSheet.this);
            paramView = EmbedBottomSheet.this.position;
            paramView[1] = (int)(paramView[1] - EmbedBottomSheet.access$3300(EmbedBottomSheet.this).getTranslationY());
            paramView = EmbedBottomSheet.this.videoView.getTextureView();
            ImageView localImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
            AnimatorSet localAnimatorSet = new AnimatorSet();
            localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(localImageView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(localImageView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(localImageView, "translationX", new float[] { EmbedBottomSheet.access$3100(EmbedBottomSheet.this)[0] }), ObjectAnimator.ofFloat(localImageView, "translationY", new float[] { EmbedBottomSheet.access$3100(EmbedBottomSheet.this)[1] }), ObjectAnimator.ofFloat(paramView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(paramView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(paramView, "translationX", new float[] { EmbedBottomSheet.access$3100(EmbedBottomSheet.this)[0] }), ObjectAnimator.ofFloat(paramView, "translationY", new float[] { EmbedBottomSheet.access$3100(EmbedBottomSheet.this)[1] }), ObjectAnimator.ofFloat(EmbedBottomSheet.access$3400(EmbedBottomSheet.this), "translationY", new float[] { 0.0F }), ObjectAnimator.ofInt(EmbedBottomSheet.access$3500(EmbedBottomSheet.this), "alpha", new int[] { 51 }) });
            localAnimatorSet.setInterpolator(new DecelerateInterpolator());
            localAnimatorSet.setDuration(250L);
            localAnimatorSet.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnimator)
              {
                EmbedBottomSheet.access$3002(EmbedBottomSheet.this, false);
              }
            });
            localAnimatorSet.start();
          }
          while (true)
          {
            return null;
            EmbedBottomSheet.this.containerView.setTranslationY(0.0F);
          }
        }

        public TextureView onSwitchToFullscreen(View paramView, boolean paramBoolean1, float paramFloat, int paramInt, boolean paramBoolean2)
        {
          if (paramBoolean1)
          {
            EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
            EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0F);
            EmbedBottomSheet.this.fullscreenVideoContainer.addView(EmbedBottomSheet.this.videoView.getAspectRatioView());
            EmbedBottomSheet.access$1202(EmbedBottomSheet.this, false);
            EmbedBottomSheet.access$1302(EmbedBottomSheet.this, paramBoolean2);
            if (EmbedBottomSheet.this.parentActivity != null)
              try
              {
                EmbedBottomSheet.access$1502(EmbedBottomSheet.this, EmbedBottomSheet.this.parentActivity.getRequestedOrientation());
                if (paramBoolean2)
                {
                  if (((WindowManager)EmbedBottomSheet.this.parentActivity.getSystemService("window")).getDefaultDisplay().getRotation() != 3)
                    break label154;
                  EmbedBottomSheet.this.parentActivity.setRequestedOrientation(8);
                }
                while (true)
                {
                  EmbedBottomSheet.this.containerView.setSystemUiVisibility(1028);
                  break;
                  label154: EmbedBottomSheet.this.parentActivity.setRequestedOrientation(0);
                }
              }
              catch (Exception paramView)
              {
                FileLog.e(paramView);
              }
          }
          else
          {
            EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
            EmbedBottomSheet.access$1302(EmbedBottomSheet.this, false);
            if (EmbedBottomSheet.this.parentActivity != null)
              try
              {
                EmbedBottomSheet.this.containerView.setSystemUiVisibility(0);
                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
              }
              catch (Exception paramView)
              {
                FileLog.e(paramView);
              }
          }
          return null;
        }

        public void onVideoSizeChanged(float paramFloat, int paramInt)
        {
        }

        public void prepareToSwitchInlineMode(boolean paramBoolean1, Runnable paramRunnable, float paramFloat, boolean paramBoolean2)
        {
          Object localObject;
          if (paramBoolean1)
          {
            if (EmbedBottomSheet.this.parentActivity != null);
            try
            {
              EmbedBottomSheet.this.containerView.setSystemUiVisibility(0);
              if (EmbedBottomSheet.this.prevOrientation != -2)
                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
              if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0)
              {
                EmbedBottomSheet.this.containerView.setTranslationY(EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0F));
                EmbedBottomSheet.this.backDrawable.setAlpha(0);
              }
              EmbedBottomSheet.this.setOnShowListener(null);
              if (paramBoolean2)
              {
                TextureView localTextureView1 = EmbedBottomSheet.this.videoView.getTextureView();
                localObject = EmbedBottomSheet.this.videoView.getControlsView();
                ImageView localImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
                Rect localRect = PipVideoView.getPipRect(paramFloat);
                paramFloat = localRect.width / localTextureView1.getWidth();
                if (Build.VERSION.SDK_INT >= 21)
                  localRect.y += AndroidUtilities.statusBarHeight;
                AnimatorSet localAnimatorSet = new AnimatorSet();
                localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(localImageView, "scaleX", new float[] { paramFloat }), ObjectAnimator.ofFloat(localImageView, "scaleY", new float[] { paramFloat }), ObjectAnimator.ofFloat(localImageView, "translationX", new float[] { localRect.x }), ObjectAnimator.ofFloat(localImageView, "translationY", new float[] { localRect.y }), ObjectAnimator.ofFloat(localTextureView1, "scaleX", new float[] { paramFloat }), ObjectAnimator.ofFloat(localTextureView1, "scaleY", new float[] { paramFloat }), ObjectAnimator.ofFloat(localTextureView1, "translationX", new float[] { localRect.x }), ObjectAnimator.ofFloat(localTextureView1, "translationY", new float[] { localRect.y }), ObjectAnimator.ofFloat(EmbedBottomSheet.access$2200(EmbedBottomSheet.this), "translationY", new float[] { EmbedBottomSheet.access$2300(EmbedBottomSheet.this).getMeasuredHeight() + AndroidUtilities.dp(10.0F) }), ObjectAnimator.ofInt(EmbedBottomSheet.access$2400(EmbedBottomSheet.this), "alpha", new int[] { 0 }), ObjectAnimator.ofFloat(EmbedBottomSheet.access$800(EmbedBottomSheet.this), "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(localObject, "alpha", new float[] { 0.0F }) });
                localAnimatorSet.setInterpolator(new DecelerateInterpolator());
                localAnimatorSet.setDuration(250L);
                localAnimatorSet.addListener(new AnimatorListenerAdapter(paramRunnable)
                {
                  public void onAnimationEnd(Animator paramAnimator)
                  {
                    if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0)
                    {
                      EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0F);
                      EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                    }
                    this.val$switchInlineModeRunnable.run();
                  }
                });
                localAnimatorSet.start();
                return;
              }
            }
            catch (Exception localException)
            {
              while (true)
                FileLog.e(localException);
              if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0)
              {
                EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0F);
                EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
              }
              paramRunnable.run();
              EmbedBottomSheet.this.dismissInternal();
              return;
            }
          }
          if (ApplicationLoader.mainInterfacePaused)
            EmbedBottomSheet.this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
          if (paramBoolean2)
          {
            EmbedBottomSheet.this.setOnShowListener(EmbedBottomSheet.this.onShowListener);
            paramRunnable = PipVideoView.getPipRect(paramFloat);
            TextureView localTextureView2 = EmbedBottomSheet.this.videoView.getTextureView();
            localObject = EmbedBottomSheet.this.videoView.getTextureImageView();
            paramFloat = paramRunnable.width / localTextureView2.getLayoutParams().width;
            if (Build.VERSION.SDK_INT >= 21)
              paramRunnable.y += AndroidUtilities.statusBarHeight;
            ((ImageView)localObject).setScaleX(paramFloat);
            ((ImageView)localObject).setScaleY(paramFloat);
            ((ImageView)localObject).setTranslationX(paramRunnable.x);
            ((ImageView)localObject).setTranslationY(paramRunnable.y);
            localTextureView2.setScaleX(paramFloat);
            localTextureView2.setScaleY(paramFloat);
            localTextureView2.setTranslationX(paramRunnable.x);
            localTextureView2.setTranslationY(paramRunnable.y);
          }
          while (true)
          {
            EmbedBottomSheet.this.setShowWithoutAnimation(true);
            EmbedBottomSheet.this.show();
            if (!paramBoolean2)
              break;
            EmbedBottomSheet.access$2602(EmbedBottomSheet.this, 4);
            EmbedBottomSheet.this.backDrawable.setAlpha(1);
            EmbedBottomSheet.this.containerView.setTranslationY(EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0F));
            return;
            EmbedBottomSheet.this.pipVideoView.close();
            EmbedBottomSheet.access$002(EmbedBottomSheet.this, null);
          }
        }
      });
      this.videoView.setVisibility(4);
      paramString4 = this.videoView;
      if (!this.hasDescription)
        break label1259;
      paramInt1 = 22;
      label454: paramString3.addView(paramString4, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, paramInt1 + 84 - 10));
      this.progressBar = new RadialProgressView(paramContext);
      this.progressBar.setVisibility(4);
      paramString4 = this.progressBar;
      if (!this.hasDescription)
        break label1265;
      paramInt1 = 22;
      label518: paramString3.addView(paramString4, LayoutHelper.createFrame(-2, -2.0F, 17, 0.0F, 0.0F, 0.0F, (paramInt1 + 84) / 2));
      if (this.hasDescription)
      {
        paramString4 = new TextView(paramContext);
        paramString4.setTextSize(1, 16.0F);
        paramString4.setTextColor(Theme.getColor("dialogTextBlack"));
        paramString4.setText(paramString2);
        paramString4.setSingleLine(true);
        paramString4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        paramString4.setEllipsize(TextUtils.TruncateAt.END);
        paramString4.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
        paramString3.addView(paramString4, LayoutHelper.createFrame(-1, -2.0F, 83, 0.0F, 0.0F, 0.0F, 77.0F));
      }
      paramString2 = new TextView(paramContext);
      paramString2.setTextSize(1, 14.0F);
      paramString2.setTextColor(Theme.getColor("dialogTextGray"));
      paramString2.setText(paramString1);
      paramString2.setSingleLine(true);
      paramString2.setEllipsize(TextUtils.TruncateAt.END);
      paramString2.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramString3.addView(paramString2, LayoutHelper.createFrame(-1, -2.0F, 83, 0.0F, 0.0F, 0.0F, 57.0F));
      paramString1 = new View(paramContext);
      paramString1.setBackgroundColor(Theme.getColor("dialogGrayLine"));
      paramString3.addView(paramString1, new FrameLayout.LayoutParams(-1, 1, 83));
      ((FrameLayout.LayoutParams)paramString1.getLayoutParams()).bottomMargin = AndroidUtilities.dp(48.0F);
      paramString1 = new FrameLayout(paramContext);
      paramString1.setBackgroundColor(Theme.getColor("dialogBackground"));
      paramString3.addView(paramString1, LayoutHelper.createFrame(-1, 48, 83));
      paramString2 = new TextView(paramContext);
      paramString2.setTextSize(1, 14.0F);
      paramString2.setTextColor(Theme.getColor("dialogTextBlue4"));
      paramString2.setGravity(17);
      paramString2.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 0));
      paramString2.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramString2.setText(LocaleController.getString("Close", 2131165556).toUpperCase());
      paramString2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramString1.addView(paramString2, LayoutHelper.createFrame(-2, -1, 51));
      paramString2.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          EmbedBottomSheet.this.dismiss();
        }
      });
      paramString2 = new LinearLayout(paramContext);
      paramString2.setOrientation(0);
      paramString1.addView(paramString2, LayoutHelper.createFrame(-2, -1, 53));
      paramString1 = new TextView(paramContext);
      paramString1.setTextSize(1, 14.0F);
      paramString1.setTextColor(Theme.getColor("dialogTextBlue4"));
      paramString1.setGravity(17);
      paramString1.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 0));
      paramString1.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramString1.setText(LocaleController.getString("Copy", 2131165583).toUpperCase());
      paramString1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramString2.addView(paramString1, LayoutHelper.createFrame(-2, -1, 51));
      paramString1.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          try
          {
            ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", EmbedBottomSheet.this.openUrl));
            Toast.makeText(EmbedBottomSheet.this.getContext(), LocaleController.getString("LinkCopied", 2131165909), 0).show();
            EmbedBottomSheet.this.dismiss();
            return;
          }
          catch (Exception paramView)
          {
            while (true)
              FileLog.e(paramView);
          }
        }
      });
      paramContext = new TextView(paramContext);
      paramContext.setTextSize(1, 14.0F);
      paramContext.setTextColor(Theme.getColor("dialogTextBlue4"));
      paramContext.setGravity(17);
      paramContext.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 0));
      paramContext.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramContext.setText(LocaleController.getString("OpenInBrowser", 2131166166).toUpperCase());
      paramContext.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramString2.addView(paramContext, LayoutHelper.createFrame(-2, -1, 51));
      paramContext.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          Browser.openUrl(EmbedBottomSheet.this.parentActivity, EmbedBottomSheet.this.openUrl);
          EmbedBottomSheet.this.dismiss();
        }
      });
      setDelegate(new BottomSheet.BottomSheetDelegate()
      {
        public boolean canDismiss()
        {
          if (EmbedBottomSheet.this.videoView.isInFullscreen())
          {
            EmbedBottomSheet.this.videoView.exitFullscreen();
            return false;
          }
          try
          {
            EmbedBottomSheet.this.parentActivity.getWindow().clearFlags(128);
            return true;
          }
          catch (Exception localException)
          {
            while (true)
              FileLog.e(localException);
          }
        }

        public void onOpenAnimationEnd()
        {
          if (EmbedBottomSheet.this.videoView.loadVideo(EmbedBottomSheet.this.embedUrl, null, EmbedBottomSheet.this.openUrl, true))
          {
            EmbedBottomSheet.this.progressBar.setVisibility(4);
            EmbedBottomSheet.this.webView.setVisibility(4);
            EmbedBottomSheet.this.videoView.setVisibility(0);
            return;
          }
          EmbedBottomSheet.this.progressBar.setVisibility(0);
          EmbedBottomSheet.this.webView.setVisibility(0);
          EmbedBottomSheet.this.videoView.setVisibility(4);
          EmbedBottomSheet.this.videoView.getControlsView().setVisibility(4);
          EmbedBottomSheet.this.videoView.getTextureView().setVisibility(4);
          if (EmbedBottomSheet.this.videoView.getTextureImageView() != null)
            EmbedBottomSheet.this.videoView.getTextureImageView().setVisibility(4);
          EmbedBottomSheet.this.videoView.loadVideo(null, null, null, false);
          HashMap localHashMap = new HashMap();
          localHashMap.put("Referer", "http://youtube.com");
          try
          {
            EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, localHashMap);
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      });
      this.orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext)
      {
        public void onOrientationChanged(int paramInt)
        {
          if ((EmbedBottomSheet.this.orientationEventListener == null) || (EmbedBottomSheet.this.videoView.getVisibility() != 0));
          do
          {
            do
              return;
            while ((EmbedBottomSheet.this.parentActivity == null) || (!EmbedBottomSheet.this.videoView.isInFullscreen()) || (!EmbedBottomSheet.this.fullscreenedByButton));
            if ((paramInt < 240) || (paramInt > 300))
              continue;
            EmbedBottomSheet.access$1202(EmbedBottomSheet.this, true);
            return;
          }
          while ((!EmbedBottomSheet.this.wasInLandscape) || ((paramInt < 330) && (paramInt > 30)));
          EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
          EmbedBottomSheet.access$1302(EmbedBottomSheet.this, false);
          EmbedBottomSheet.access$1202(EmbedBottomSheet.this, false);
        }
      };
      if (!this.orientationEventListener.canDetectOrientation())
        break label1271;
      this.orientationEventListener.enable();
    }
    while (true)
    {
      instance = this;
      return;
      bool = false;
      break;
      label1253: paramInt1 = 0;
      break label383;
      label1259: paramInt1 = 0;
      break label454;
      label1265: paramInt1 = 0;
      break label518;
      label1271: this.orientationEventListener.disable();
      this.orientationEventListener = null;
    }
  }

  public static EmbedBottomSheet getInstance()
  {
    return instance;
  }

  public static void show(Context paramContext, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2)
  {
    if (instance != null)
      instance.destroy();
    new EmbedBottomSheet(paramContext, paramString1, paramString2, paramString3, paramString4, paramInt1, paramInt2).show();
  }

  protected boolean canDismissWithSwipe()
  {
    return (this.videoView.getVisibility() != 0) || (!this.videoView.isInFullscreen());
  }

  public void destroy()
  {
    if (this.pipVideoView != null)
    {
      this.pipVideoView.close();
      this.pipVideoView = null;
    }
    if (this.videoView != null)
      this.videoView.destroy();
    instance = null;
    dismissInternal();
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if ((this.videoView.getVisibility() == 0) && (this.videoView.isInitied()) && (!this.videoView.isInline()))
    {
      if (paramConfiguration.orientation != 2)
        break label70;
      if (!this.videoView.isInFullscreen())
        this.videoView.enterFullscreen();
    }
    while (true)
    {
      if (this.pipVideoView != null)
        this.pipVideoView.onConfigurationChanged();
      return;
      label70: if (!this.videoView.isInFullscreen())
        continue;
      this.videoView.exitFullscreen();
    }
  }

  public void onContainerDraw(Canvas paramCanvas)
  {
    if (this.waitingForDraw != 0)
    {
      this.waitingForDraw -= 1;
      if (this.waitingForDraw == 0)
      {
        this.videoView.updateTextureImageView();
        this.pipVideoView.close();
        this.pipVideoView = null;
      }
    }
    else
    {
      return;
    }
    this.container.invalidate();
  }

  protected void onContainerTranslationYChanged(float paramFloat)
  {
    updateTextureViewPosition();
  }

  protected boolean onCustomLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramView == this.videoView.getControlsView())
      updateTextureViewPosition();
    return false;
  }

  protected boolean onCustomMeasure(View paramView, int paramInt1, int paramInt2)
  {
    if (paramView == this.videoView.getControlsView())
    {
      paramView = paramView.getLayoutParams();
      paramView.width = this.videoView.getMeasuredWidth();
      paramInt2 = this.videoView.getAspectRatioView().getMeasuredHeight();
      if (!this.videoView.isInFullscreen())
        break label59;
    }
    label59: for (paramInt1 = 0; ; paramInt1 = AndroidUtilities.dp(10.0F))
    {
      paramView.height = (paramInt1 + paramInt2);
      return false;
    }
  }

  public void pause()
  {
    if ((this.videoView != null) && (this.videoView.isInitied()))
      this.videoView.pause();
  }

  public void updateTextureViewPosition()
  {
    this.videoView.getAspectRatioView().getLocationInWindow(this.position);
    Object localObject = this.position;
    localObject[0] -= getLeftInset();
    if ((!this.videoView.isInline()) && (!this.animationInProgress))
    {
      localObject = this.videoView.getTextureView();
      ((TextureView)localObject).setTranslationX(this.position[0]);
      ((TextureView)localObject).setTranslationY(this.position[1]);
      localObject = this.videoView.getTextureImageView();
      if (localObject != null)
      {
        ((View)localObject).setTranslationX(this.position[0]);
        ((View)localObject).setTranslationY(this.position[1]);
      }
    }
    localObject = this.videoView.getControlsView();
    if (((View)localObject).getParent() == this.container)
    {
      ((View)localObject).setTranslationY(this.position[1]);
      return;
    }
    ((View)localObject).setTranslationY(0.0F);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.EmbedBottomSheet
 * JD-Core Version:    0.6.0
 */