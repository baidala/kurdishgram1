package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.Utilities;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Components.IdenticonDrawable;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.URLSpanReplacement;

public class IdenticonActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private AnimatorSet animatorSet;
  private int chat_id;
  private TextView codeTextView;
  private FrameLayout container;
  private boolean emojiSelected;
  private String emojiText;
  private TextView emojiTextView;
  private AnimatorSet hintAnimatorSet;
  private TextView hintTextView;
  private LinearLayout linearLayout;
  private LinearLayout linearLayout1;
  private TextView textView;
  private int textWidth;

  public IdenticonActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }

  private void fixLayout()
  {
    this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        if (IdenticonActivity.this.fragmentView == null)
          return true;
        IdenticonActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
        int i = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        if ((i == 3) || (i == 1))
          IdenticonActivity.this.linearLayout.setOrientation(0);
        while (true)
        {
          IdenticonActivity.this.fragmentView.setPadding(IdenticonActivity.this.fragmentView.getPaddingLeft(), 0, IdenticonActivity.this.fragmentView.getPaddingRight(), IdenticonActivity.this.fragmentView.getPaddingBottom());
          return true;
          IdenticonActivity.this.linearLayout.setOrientation(1);
        }
      }
    });
  }

  private void showHint(boolean paramBoolean)
  {
  }

  private void updateEmojiButton(boolean paramBoolean)
  {
    float f3 = 0.0F;
    float f2 = 0.0F;
    if (this.animatorSet != null)
    {
      this.animatorSet.cancel();
      this.animatorSet = null;
    }
    Object localObject2;
    float f1;
    if (paramBoolean)
    {
      this.animatorSet = new AnimatorSet();
      localObject1 = this.animatorSet;
      localObject2 = this.emojiTextView;
      if (this.emojiSelected)
      {
        f1 = 1.0F;
        localObject2 = ObjectAnimator.ofFloat(localObject2, "alpha", new float[] { f1 });
        Object localObject3 = this.codeTextView;
        if (!this.emojiSelected)
          break label344;
        f1 = 0.0F;
        label91: localObject3 = ObjectAnimator.ofFloat(localObject3, "alpha", new float[] { f1 });
        Object localObject4 = this.emojiTextView;
        if (!this.emojiSelected)
          break label349;
        f1 = 1.0F;
        label122: localObject4 = ObjectAnimator.ofFloat(localObject4, "scaleX", new float[] { f1 });
        Object localObject5 = this.emojiTextView;
        if (!this.emojiSelected)
          break label354;
        f1 = 1.0F;
        label153: localObject5 = ObjectAnimator.ofFloat(localObject5, "scaleY", new float[] { f1 });
        Object localObject6 = this.codeTextView;
        if (!this.emojiSelected)
          break label359;
        f1 = 0.0F;
        label184: localObject6 = ObjectAnimator.ofFloat(localObject6, "scaleX", new float[] { f1 });
        TextView localTextView = this.codeTextView;
        if (!this.emojiSelected)
          break label364;
        f1 = f2;
        label215: ((AnimatorSet)localObject1).playTogether(new Animator[] { localObject2, localObject3, localObject4, localObject5, localObject6, ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { f1 }) });
        this.animatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            if (paramAnimator.equals(IdenticonActivity.this.animatorSet))
              IdenticonActivity.access$602(IdenticonActivity.this, null);
          }
        });
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.setDuration(150L);
        this.animatorSet.start();
        localObject2 = this.emojiTextView;
        if (this.emojiSelected)
          break label529;
      }
    }
    label384: label514: label519: label524: label529: for (Object localObject1 = "chat_emojiPanelIcon"; ; localObject1 = "chat_emojiPanelIconSelected")
    {
      ((TextView)localObject2).setTag(localObject1);
      return;
      f1 = 0.0F;
      break;
      label344: f1 = 1.0F;
      break label91;
      label349: f1 = 0.0F;
      break label122;
      label354: f1 = 0.0F;
      break label153;
      label359: f1 = 1.0F;
      break label184;
      label364: f1 = 1.0F;
      break label215;
      localObject1 = this.emojiTextView;
      if (this.emojiSelected)
      {
        f1 = 1.0F;
        ((TextView)localObject1).setAlpha(f1);
        localObject1 = this.codeTextView;
        if (!this.emojiSelected)
          break label504;
        f1 = 0.0F;
        label405: ((TextView)localObject1).setAlpha(f1);
        localObject1 = this.emojiTextView;
        if (!this.emojiSelected)
          break label509;
        f1 = 1.0F;
        label426: ((TextView)localObject1).setScaleX(f1);
        localObject1 = this.emojiTextView;
        if (!this.emojiSelected)
          break label514;
        f1 = 1.0F;
        label447: ((TextView)localObject1).setScaleY(f1);
        localObject1 = this.codeTextView;
        if (!this.emojiSelected)
          break label519;
        f1 = 0.0F;
        label468: ((TextView)localObject1).setScaleX(f1);
        localObject1 = this.codeTextView;
        if (!this.emojiSelected)
          break label524;
        f1 = f3;
      }
      while (true)
      {
        ((TextView)localObject1).setScaleY(f1);
        break;
        f1 = 0.0F;
        break label384;
        label504: f1 = 1.0F;
        break label405;
        label509: f1 = 0.0F;
        break label426;
        f1 = 0.0F;
        break label447;
        f1 = 1.0F;
        break label468;
        f1 = 1.0F;
      }
    }
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("EncryptionKey", 2131165687));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          IdenticonActivity.this.finishFragment();
      }
    });
    this.fragmentView = new FrameLayout(paramContext)
    {
      protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        paramInt1 = IdenticonActivity.this.container.getLeft() + IdenticonActivity.this.codeTextView.getLeft() + IdenticonActivity.this.codeTextView.getMeasuredWidth() / 2 - IdenticonActivity.this.hintTextView.getMeasuredWidth() / 2;
        paramInt2 = Math.max(AndroidUtilities.dp(5.0F), IdenticonActivity.this.container.getTop() + IdenticonActivity.this.codeTextView.getTop() - AndroidUtilities.dp(10.0F));
        IdenticonActivity.this.hintTextView.layout(paramInt1, paramInt2, IdenticonActivity.this.hintTextView.getMeasuredWidth() + paramInt1, IdenticonActivity.this.hintTextView.getMeasuredHeight() + paramInt2);
      }
    };
    Object localObject2 = (FrameLayout)this.fragmentView;
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.fragmentView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        return true;
      }
    });
    this.linearLayout = new LinearLayout(paramContext);
    this.linearLayout.setOrientation(1);
    this.linearLayout.setWeightSum(100.0F);
    ((FrameLayout)localObject2).addView(this.linearLayout, LayoutHelper.createFrame(-1, -1.0F));
    Object localObject3 = new FrameLayout(paramContext);
    ((FrameLayout)localObject3).setPadding(AndroidUtilities.dp(20.0F), AndroidUtilities.dp(20.0F), AndroidUtilities.dp(20.0F), AndroidUtilities.dp(20.0F));
    this.linearLayout.addView((View)localObject3, LayoutHelper.createLinear(-1, -1, 50.0F));
    Object localObject1 = new ImageView(paramContext);
    ((ImageView)localObject1).setScaleType(ImageView.ScaleType.FIT_XY);
    ((FrameLayout)localObject3).addView((View)localObject1, LayoutHelper.createFrame(-1, -1.0F));
    this.container = new FrameLayout(paramContext)
    {
      protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        if (IdenticonActivity.this.codeTextView != null)
        {
          paramInt1 = IdenticonActivity.this.codeTextView.getLeft() + IdenticonActivity.this.codeTextView.getMeasuredWidth() / 2 - IdenticonActivity.this.emojiTextView.getMeasuredWidth() / 2;
          paramInt2 = (IdenticonActivity.this.codeTextView.getMeasuredHeight() - IdenticonActivity.this.emojiTextView.getMeasuredHeight()) / 2 + IdenticonActivity.this.linearLayout1.getTop() - AndroidUtilities.dp(16.0F);
          IdenticonActivity.this.emojiTextView.layout(paramInt1, paramInt2, IdenticonActivity.this.emojiTextView.getMeasuredWidth() + paramInt1, IdenticonActivity.this.emojiTextView.getMeasuredHeight() + paramInt2);
        }
      }
    };
    this.container.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    this.linearLayout.addView(this.container, LayoutHelper.createLinear(-1, -1, 50.0F));
    this.linearLayout1 = new LinearLayout(paramContext);
    this.linearLayout1.setOrientation(1);
    this.linearLayout1.setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
    this.container.addView(this.linearLayout1, LayoutHelper.createFrame(-2, -2, 17));
    this.codeTextView = new TextView(paramContext);
    this.codeTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
    this.codeTextView.setGravity(17);
    this.codeTextView.setTypeface(Typeface.MONOSPACE);
    this.codeTextView.setTextSize(1, 16.0F);
    this.linearLayout1.addView(this.codeTextView, LayoutHelper.createLinear(-2, -2, 1));
    this.hintTextView = new TextView(getParentActivity());
    this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0F), Theme.getColor("chat_gifSaveHintBackground")));
    this.hintTextView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
    this.hintTextView.setTextSize(1, 14.0F);
    this.hintTextView.setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
    this.hintTextView.setText(LocaleController.getString("TapToEmojify", 2131166501));
    this.hintTextView.setGravity(16);
    this.hintTextView.setAlpha(0.0F);
    ((FrameLayout)localObject2).addView(this.hintTextView, LayoutHelper.createFrame(-2, 32.0F));
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
    this.textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLinksClickable(true);
    this.textView.setClickable(true);
    this.textView.setGravity(17);
    this.textView.setMovementMethod(new LinkMovementMethodMy(null));
    this.linearLayout1.addView(this.textView, LayoutHelper.createFrame(-2, -2, 1));
    this.emojiTextView = new TextView(paramContext);
    this.emojiTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
    this.emojiTextView.setGravity(17);
    this.emojiTextView.setTextSize(1, 32.0F);
    this.container.addView(this.emojiTextView, LayoutHelper.createFrame(-2, -2.0F));
    paramContext = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(this.chat_id));
    if (paramContext != null)
    {
      localObject2 = new IdenticonDrawable();
      ((ImageView)localObject1).setImageDrawable((Drawable)localObject2);
      ((IdenticonDrawable)localObject2).setEncryptedChat(paramContext);
      localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(paramContext.user_id));
      localObject2 = new SpannableStringBuilder();
      localObject3 = new StringBuilder();
      if (paramContext.key_hash.length > 16)
      {
        String str = Utilities.bytesToHex(paramContext.key_hash);
        i = 0;
        if (i < 32)
        {
          if (i != 0)
          {
            if (i % 8 != 0)
              break label859;
            ((SpannableStringBuilder)localObject2).append('\n');
          }
          while (true)
          {
            ((SpannableStringBuilder)localObject2).append(str.substring(i * 2, i * 2 + 2));
            ((SpannableStringBuilder)localObject2).append(' ');
            i += 1;
            break;
            label859: if (i % 4 != 0)
              continue;
            ((SpannableStringBuilder)localObject2).append(' ');
          }
        }
        ((SpannableStringBuilder)localObject2).append("\n");
        i = 0;
        while (i < 5)
        {
          int j = paramContext.key_hash[(i * 4 + 16)];
          int k = paramContext.key_hash[(i * 4 + 16 + 1)];
          int m = paramContext.key_hash[(i * 4 + 16 + 2)];
          int n = paramContext.key_hash[(i * 4 + 16 + 3)];
          if (i != 0)
            ((StringBuilder)localObject3).append(" ");
          ((StringBuilder)localObject3).append(org.vidogram.messenger.EmojiData.emojiSecret[(((j & 0x7F) << 24 | (k & 0xFF) << 16 | (m & 0xFF) << 8 | n & 0xFF) % org.vidogram.messenger.EmojiData.emojiSecret.length)]);
          i += 1;
        }
        this.emojiText = ((StringBuilder)localObject3).toString();
      }
      this.codeTextView.setText(((SpannableStringBuilder)localObject2).toString());
      ((SpannableStringBuilder)localObject2).clear();
      ((SpannableStringBuilder)localObject2).append(AndroidUtilities.replaceTags(LocaleController.formatString("EncryptionKeyDescription", 2131165688, new Object[] { ((TLRPC.User)localObject1).first_name, ((TLRPC.User)localObject1).first_name })));
      int i = ((SpannableStringBuilder)localObject2).toString().indexOf("telegram.org");
      if (i != -1)
        ((SpannableStringBuilder)localObject2).setSpan(new URLSpanReplacement(LocaleController.getString("EncryptionKeyLink", 2131165689)), i, "telegram.org".length() + i, 33);
      this.textView.setText((CharSequence)localObject2);
    }
    updateEmojiButton(false);
    return (View)(View)(View)this.fragmentView;
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if ((paramInt == NotificationCenter.emojiDidLoaded) && (this.emojiTextView != null))
      this.emojiTextView.invalidate();
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.container, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.codeTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.textView, ThemeDescription.FLAG_LINKCOLOR, null, null, null, null, "windowBackgroundWhiteLinkText"), new ThemeDescription(this.hintTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chat_gifSaveHintBackground"), new ThemeDescription(this.hintTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_gifSaveHintText") };
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    fixLayout();
  }

  public boolean onFragmentCreate()
  {
    this.chat_id = getArguments().getInt("chat_id");
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    return super.onFragmentCreate();
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
  }

  public void onResume()
  {
    super.onResume();
    fixLayout();
  }

  protected void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (!paramBoolean2) && (this.emojiText != null))
    {
      this.emojiTextView.setText(Emoji.replaceEmoji(this.emojiText, this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(32.0F), false));
      showHint(true);
    }
  }

  private static class LinkMovementMethodMy extends LinkMovementMethod
  {
    public boolean onTouchEvent(TextView paramTextView, Spannable paramSpannable, MotionEvent paramMotionEvent)
    {
      try
      {
        boolean bool = super.onTouchEvent(paramTextView, paramSpannable, paramMotionEvent);
        return bool;
      }
      catch (java.lang.Exception paramTextView)
      {
        FileLog.e(paramTextView);
      }
      return false;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.IdenticonActivity
 * JD-Core Version:    0.6.0
 */