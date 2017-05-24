package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.browser.Browser;
import org.vidogram.tgnet.AbstractSerializedData;
import org.vidogram.tgnet.SerializedData;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Components.ContextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.ShareAlert;

public class WebviewActivity extends BaseFragment
{
  private static final int open_in = 2;
  private static final int share = 1;
  private String currentBot;
  private String currentGame;
  private MessageObject currentMessageObject;
  private String currentUrl;
  private String linkToCopy;
  private ActionBarMenuItem progressItem;
  private ContextProgressView progressView;
  private String short_param;
  public Runnable typingRunnable = new Runnable()
  {
    public void run()
    {
      if ((WebviewActivity.this.currentMessageObject == null) || (WebviewActivity.this.getParentActivity() == null) || (WebviewActivity.this.typingRunnable == null))
        return;
      MessagesController.getInstance().sendTyping(WebviewActivity.this.currentMessageObject.getDialogId(), 6, 0);
      AndroidUtilities.runOnUIThread(WebviewActivity.this.typingRunnable, 25000L);
    }
  };
  private WebView webView;

  public WebviewActivity(String paramString1, String paramString2, String paramString3, String paramString4, MessageObject paramMessageObject)
  {
    this.currentUrl = paramString1;
    this.currentBot = paramString2;
    this.currentGame = paramString3;
    this.currentMessageObject = paramMessageObject;
    this.short_param = paramString4;
    paramString2 = new StringBuilder().append("https://").append(MessagesController.getInstance().linkPrefix).append("/").append(this.currentBot);
    if (TextUtils.isEmpty(paramString4));
    for (paramString1 = ""; ; paramString1 = "?game=" + paramString4)
    {
      this.linkToCopy = paramString1;
      return;
    }
  }

  public static void openGameInBrowser(String paramString1, MessageObject paramMessageObject, Activity paramActivity, String paramString2, String paramString3)
  {
    int i = 0;
    while (true)
    {
      try
      {
        Object localObject3 = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
        Object localObject2 = ((SharedPreferences)localObject3).getString("" + paramMessageObject.getId(), null);
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          localObject1 = new StringBuilder((String)localObject1);
          StringBuilder localStringBuilder = new StringBuilder("tgShareScoreUrl=" + URLEncoder.encode("tgb://share_game_score?hash=", "UTF-8"));
          if (localObject2 != null)
            continue;
          localObject2 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
          if (i >= 20)
            continue;
          ((StringBuilder)localObject1).append(localObject2[Utilities.random.nextInt(localObject2.length)]);
          i += 1;
          continue;
          localStringBuilder.append((CharSequence)localObject1);
          i = paramString1.indexOf('#');
          if (i >= 0)
            continue;
          paramString1 = paramString1 + "#" + localStringBuilder;
          localObject2 = ((SharedPreferences)localObject3).edit();
          ((SharedPreferences.Editor)localObject2).putInt(localObject1 + "_date", (int)(System.currentTimeMillis() / 1000L));
          localObject3 = new SerializedData(paramMessageObject.messageOwner.getObjectSize());
          paramMessageObject.messageOwner.serializeToStream((AbstractSerializedData)localObject3);
          ((SharedPreferences.Editor)localObject2).putString(localObject1 + "_m", Utilities.bytesToHex(((SerializedData)localObject3).toByteArray()));
          localObject1 = localObject1 + "_link";
          paramString3 = new StringBuilder().append("https://").append(MessagesController.getInstance().linkPrefix).append("/").append(paramString3);
          if (!TextUtils.isEmpty(paramString2))
            continue;
          paramMessageObject = "";
          ((SharedPreferences.Editor)localObject2).putString((String)localObject1, paramMessageObject);
          ((SharedPreferences.Editor)localObject2).commit();
          Browser.openUrl(paramActivity, paramString1, false);
          return;
          localObject2 = paramString1.substring(i + 1);
          if ((((String)localObject2).indexOf('=') < 0) && (((String)localObject2).indexOf('?') < 0))
            continue;
          paramString1 = paramString1 + "&" + localStringBuilder;
          continue;
          if (((String)localObject2).length() <= 0)
            continue;
          paramString1 = paramString1 + "?" + localStringBuilder;
          continue;
          paramString1 = paramString1 + localStringBuilder;
          continue;
          paramMessageObject = "?game=" + paramString2;
          continue;
        }
      }
      catch (Exception paramString1)
      {
        FileLog.e(paramString1);
        return;
      }
      Object localObject1 = "";
    }
  }

  public static boolean supportWebview()
  {
    String str1 = Build.MANUFACTURER;
    String str2 = Build.MODEL;
    return (!"samsung".equals(str1)) || (!"GT-I9500".equals(str2));
  }

  @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
  public View createView(Context paramContext)
  {
    this.swipeBackEnabled = false;
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(this.currentGame);
    this.actionBar.setSubtitle("@" + this.currentBot);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          WebviewActivity.this.finishFragment();
        do
        {
          return;
          if (paramInt != 1)
            continue;
          WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
          ArrayList localArrayList = new ArrayList();
          localArrayList.add(WebviewActivity.this.currentMessageObject);
          WebviewActivity.this.showDialog(new ShareAlert(WebviewActivity.this.getParentActivity(), localArrayList, null, false, WebviewActivity.this.linkToCopy, true));
          return;
        }
        while (paramInt != 2);
        WebviewActivity.openGameInBrowser(WebviewActivity.this.currentUrl, WebviewActivity.this.currentMessageObject, WebviewActivity.this.getParentActivity(), WebviewActivity.this.short_param, WebviewActivity.this.currentBot);
      }
    });
    ActionBarMenu localActionBarMenu = this.actionBar.createMenu();
    this.progressItem = localActionBarMenu.addItemWithWidth(1, 2130838058, AndroidUtilities.dp(54.0F));
    this.progressView = new ContextProgressView(paramContext, 1);
    this.progressItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0F));
    this.progressItem.getImageView().setVisibility(4);
    localActionBarMenu.addItem(0, 2130837738).addSubItem(2, LocaleController.getString("OpenInExternalApp", 2131166167));
    this.webView = new WebView(paramContext);
    this.webView.getSettings().setJavaScriptEnabled(true);
    this.webView.getSettings().setDomStorageEnabled(true);
    this.fragmentView = new FrameLayout(paramContext);
    paramContext = (FrameLayout)this.fragmentView;
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.webView.getSettings().setMixedContentMode(0);
      CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
      this.webView.addJavascriptInterface(new TelegramWebviewProxy(null), "TelegramWebviewProxy");
    }
    this.webView.setWebViewClient(new WebViewClient()
    {
      public void onLoadResource(WebView paramWebView, String paramString)
      {
        super.onLoadResource(paramWebView, paramString);
      }

      public void onPageFinished(WebView paramWebView, String paramString)
      {
        super.onPageFinished(paramWebView, paramString);
        WebviewActivity.this.progressItem.getImageView().setVisibility(0);
        WebviewActivity.this.progressItem.setEnabled(true);
        paramWebView = new AnimatorSet();
        paramWebView.playTogether(new Animator[] { ObjectAnimator.ofFloat(WebviewActivity.access$700(WebviewActivity.this), "scaleX", new float[] { 1.0F, 0.1F }), ObjectAnimator.ofFloat(WebviewActivity.access$700(WebviewActivity.this), "scaleY", new float[] { 1.0F, 0.1F }), ObjectAnimator.ofFloat(WebviewActivity.access$700(WebviewActivity.this), "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(WebviewActivity.access$600(WebviewActivity.this).getImageView(), "scaleX", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(WebviewActivity.access$600(WebviewActivity.this).getImageView(), "scaleY", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(WebviewActivity.access$600(WebviewActivity.this).getImageView(), "alpha", new float[] { 0.0F, 1.0F }) });
        paramWebView.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            WebviewActivity.this.progressView.setVisibility(4);
          }
        });
        paramWebView.setDuration(150L);
        paramWebView.start();
      }
    });
    paramContext.addView(this.webView, LayoutHelper.createFrame(-1, -1.0F));
    return this.fragmentView;
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem"), new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressInner2"), new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressOuter2") };
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
    this.typingRunnable = null;
    try
    {
      ViewParent localViewParent = this.webView.getParent();
      if (localViewParent != null)
        ((FrameLayout)localViewParent).removeView(this.webView);
      this.webView.stopLoading();
      this.webView.loadUrl("about:blank");
      this.webView.destroy();
      this.webView = null;
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public void onResume()
  {
    super.onResume();
    AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
    this.typingRunnable.run();
  }

  protected void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (!paramBoolean2) && (this.webView != null))
      this.webView.loadUrl(this.currentUrl);
  }

  private class TelegramWebviewProxy
  {
    private TelegramWebviewProxy()
    {
    }

    @JavascriptInterface
    public void postEvent(String paramString1, String paramString2)
    {
      AndroidUtilities.runOnUIThread(new Runnable(paramString1)
      {
        public void run()
        {
          if (WebviewActivity.this.getParentActivity() == null)
            return;
          FileLog.e(this.val$eventName);
          Object localObject = this.val$eventName;
          int i = -1;
          switch (((String)localObject).hashCode())
          {
          default:
            switch (i)
            {
            default:
            case 0:
            case 1:
            }
          case -1788360622:
          case 406539826:
          }
          while (true)
          {
            localObject = new ArrayList();
            ((ArrayList)localObject).add(WebviewActivity.this.currentMessageObject);
            WebviewActivity.this.showDialog(new ShareAlert(WebviewActivity.this.getParentActivity(), (ArrayList)localObject, null, false, WebviewActivity.this.linkToCopy, true));
            return;
            if (!((String)localObject).equals("share_game"))
              break;
            i = 0;
            break;
            if (!((String)localObject).equals("share_score"))
              break;
            i = 1;
            break;
            WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
            continue;
            WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = true;
          }
        }
      });
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.WebviewActivity
 * JD-Core Version:    0.6.0
 */