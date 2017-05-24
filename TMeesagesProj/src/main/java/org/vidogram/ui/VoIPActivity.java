package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v7.c.b;
import android.support.v7.c.b.a;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.text.style.CharacterStyle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.BuildVars;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.voip.EncryptionKeyEmojifier;
import org.vidogram.messenger.voip.VoIPController;
import org.vidogram.messenger.voip.VoIPService;
import org.vidogram.messenger.voip.VoIPService.StateListener;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BottomSheet.Builder;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.CorrectlyMeasuringTextView;
import org.vidogram.ui.Components.CubicBezierInterpolator;
import org.vidogram.ui.Components.IdenticonDrawable;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.voip.CallSwipeView;
import org.vidogram.ui.Components.voip.CallSwipeView.Listener;
import org.vidogram.ui.Components.voip.CheckableImageView;
import org.vidogram.ui.Components.voip.FabBackgroundDrawable;
import org.vidogram.ui.Components.voip.VoIPHelper;

public class VoIPActivity extends Activity
  implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener
{
  private static final String TAG = "tg-voip-ui";
  private View acceptBtn;
  private CallSwipeView acceptSwipe;
  private int audioBitrate = 25;
  private ImageView blurOverlayView1;
  private ImageView blurOverlayView2;
  private Bitmap blurredPhoto1;
  private Bitmap blurredPhoto2;
  private int callState;
  private View cancelBtn;
  private ImageView chatBtn;
  private FrameLayout content;
  private Animator currentAcceptAnim;
  private Animator currentDeclineAnim;
  private View declineBtn;
  private CallSwipeView declineSwipe;
  private boolean didAcceptFromHere = false;
  private TextView durationText;
  private AnimatorSet ellAnimator;
  private TextAlphaSpan[] ellSpans;
  private AnimatorSet emojiAnimator;
  boolean emojiExpanded;
  private TextView emojiExpandedText;
  boolean emojiTooltipVisible;
  private LinearLayout emojiWrap;
  private View endBtn;
  private FabBackgroundDrawable endBtnBg;
  private View endBtnIcon;
  private boolean firstStateChange = true;
  private TextView hintTextView;
  private boolean isIncomingWaiting;
  private ImageView[] keyEmojiViews = new ImageView[4];
  private boolean keyEmojiVisible;
  private String lastStateText;
  private CheckableImageView micToggle;
  private TextView nameText;
  private int packetLossPercent = 5;
  private BackupImageView photoView;
  private AnimatorSet retryAnim;
  private boolean retrying;
  private CheckableImageView spkToggle;
  private TextView stateText;
  private TextView stateText2;
  private LinearLayout swipeViewsWrap;
  private Animator textChangingAnim;
  private Animator tooltipAnim;
  private Runnable tooltipHider;
  private TLRPC.User user;

  private void callAccepted()
  {
    this.endBtn.setVisibility(0);
    this.micToggle.setVisibility(0);
    if (VoIPService.getSharedInstance().hasEarpiece())
      this.spkToggle.setVisibility(0);
    this.chatBtn.setVisibility(0);
    if (this.didAcceptFromHere)
    {
      this.acceptBtn.setVisibility(8);
      if (Build.VERSION.SDK_INT >= 21)
        localObject = ObjectAnimator.ofArgb(this.endBtnBg, "color", new int[] { -12207027, -1696188 });
      while (true)
      {
        localAnimatorSet1 = new AnimatorSet();
        localAnimatorSet2 = new AnimatorSet();
        localAnimatorSet2.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.micToggle, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.spkToggle, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.chatBtn, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[] { -135.0F, 0.0F }), localObject });
        localAnimatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        localAnimatorSet2.setDuration(500L);
        localObject = new AnimatorSet();
        ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.swipeViewsWrap, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.declineBtn, "alpha", new float[] { 0.0F }) });
        ((AnimatorSet)localObject).setInterpolator(CubicBezierInterpolator.EASE_IN);
        ((AnimatorSet)localObject).setDuration(125L);
        localAnimatorSet1.playTogether(new Animator[] { localAnimatorSet2, localObject });
        localAnimatorSet1.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            VoIPActivity.this.swipeViewsWrap.setVisibility(8);
            VoIPActivity.this.declineBtn.setVisibility(8);
          }
        });
        localAnimatorSet1.start();
        return;
        localObject = ObjectAnimator.ofInt(this.endBtnBg, "color", new int[] { -12207027, -1696188 });
        ((ObjectAnimator)localObject).setEvaluator(new ArgbEvaluator());
      }
    }
    Object localObject = new AnimatorSet();
    AnimatorSet localAnimatorSet1 = new AnimatorSet();
    localAnimatorSet1.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.micToggle, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.spkToggle, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.chatBtn, "alpha", new float[] { 0.0F, 1.0F }) });
    localAnimatorSet1.setInterpolator(CubicBezierInterpolator.EASE_OUT);
    localAnimatorSet1.setDuration(500L);
    AnimatorSet localAnimatorSet2 = new AnimatorSet();
    localAnimatorSet2.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.swipeViewsWrap, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.declineBtn, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.acceptBtn, "alpha", new float[] { 0.0F }) });
    localAnimatorSet2.setInterpolator(CubicBezierInterpolator.EASE_IN);
    localAnimatorSet2.setDuration(125L);
    ((AnimatorSet)localObject).playTogether(new Animator[] { localAnimatorSet1, localAnimatorSet2 });
    ((AnimatorSet)localObject).addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        VoIPActivity.this.swipeViewsWrap.setVisibility(8);
        VoIPActivity.this.declineBtn.setVisibility(8);
        VoIPActivity.this.acceptBtn.setVisibility(8);
      }
    });
    ((AnimatorSet)localObject).start();
  }

  @SuppressLint({"ObjectAnimatorBinding"})
  private ObjectAnimator createAlphaAnimator(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramObject = ObjectAnimator.ofInt(paramObject, "alpha", new int[] { paramInt1, paramInt2 });
    paramObject.setDuration(paramInt4);
    paramObject.setStartDelay(paramInt3);
    paramObject.setInterpolator(CubicBezierInterpolator.DEFAULT);
    return paramObject;
  }

  private View createContentView()
  {
    FrameLayout localFrameLayout = new FrameLayout(this);
    localFrameLayout.setBackgroundColor(0);
    Object localObject1 = new BackupImageView(this)
    {
      private Drawable bottomGradient = getResources().getDrawable(2130837723);
      private Paint paint = new Paint();
      private Drawable topGradient = getResources().getDrawable(2130837724);

      protected void onDraw(Canvas paramCanvas)
      {
        super.onDraw(paramCanvas);
        this.paint.setColor(1275068416);
        paramCanvas.drawRect(0.0F, 0.0F, getWidth(), getHeight(), this.paint);
        this.topGradient.setBounds(0, 0, getWidth(), AndroidUtilities.dp(170.0F));
        this.topGradient.setAlpha(128);
        this.topGradient.draw(paramCanvas);
        this.bottomGradient.setBounds(0, getHeight() - AndroidUtilities.dp(220.0F), getWidth(), getHeight());
        this.bottomGradient.setAlpha(178);
        this.bottomGradient.draw(paramCanvas);
      }
    };
    this.photoView = ((BackupImageView)localObject1);
    localFrameLayout.addView((View)localObject1);
    this.blurOverlayView1 = new ImageView(this);
    this.blurOverlayView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
    this.blurOverlayView1.setAlpha(0.0F);
    localFrameLayout.addView(this.blurOverlayView1);
    this.blurOverlayView2 = new ImageView(this);
    this.blurOverlayView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
    this.blurOverlayView2.setAlpha(0.0F);
    localFrameLayout.addView(this.blurOverlayView2);
    Object localObject3 = new TextView(this);
    ((TextView)localObject3).setTextColor(-855638017);
    ((TextView)localObject3).setText(LocaleController.getString("VoipInCallBranding", 2131166588));
    localObject1 = getResources().getDrawable(2130837965).mutate();
    ((Drawable)localObject1).setAlpha(204);
    ((Drawable)localObject1).setBounds(0, 0, AndroidUtilities.dp(15.0F), AndroidUtilities.dp(15.0F));
    Object localObject2;
    label201: label230: float f;
    if (LocaleController.isRTL)
    {
      localObject2 = null;
      if (!LocaleController.isRTL)
        break label1620;
      ((TextView)localObject3).setCompoundDrawables((Drawable)localObject2, null, (Drawable)localObject1, null);
      ((TextView)localObject3).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      if (!LocaleController.isRTL)
        break label1625;
      i = 5;
      ((TextView)localObject3).setGravity(i);
      ((TextView)localObject3).setCompoundDrawablePadding(AndroidUtilities.dp(5.0F));
      ((TextView)localObject3).setTextSize(1, 14.0F);
      localFrameLayout.addView((View)localObject3, LayoutHelper.createFrame(-1, -2.0F, 51, 18.0F, 18.0F, 18.0F, 0.0F));
      localObject1 = new TextView(this);
      ((TextView)localObject1).setSingleLine();
      ((TextView)localObject1).setTextColor(-1);
      ((TextView)localObject1).setTextSize(1, 40.0F);
      ((TextView)localObject1).setEllipsize(TextUtils.TruncateAt.END);
      if (!LocaleController.isRTL)
        break label1630;
      i = 5;
      label323: ((TextView)localObject1).setGravity(i);
      ((TextView)localObject1).setShadowLayer(AndroidUtilities.dp(3.0F), 0.0F, AndroidUtilities.dp(0.6666667F), 1275068416);
      ((TextView)localObject1).setTypeface(Typeface.create("sans-serif-light", 0));
      this.nameText = ((TextView)localObject1);
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-1, -2.0F, 51, 18.0F, 43.0F, 18.0F, 0.0F));
      localObject1 = new TextView(this);
      ((TextView)localObject1).setTextColor(-855638017);
      ((TextView)localObject1).setSingleLine();
      ((TextView)localObject1).setEllipsize(TextUtils.TruncateAt.END);
      ((TextView)localObject1).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((TextView)localObject1).setShadowLayer(AndroidUtilities.dp(3.0F), 0.0F, AndroidUtilities.dp(0.6666667F), 1275068416);
      ((TextView)localObject1).setTextSize(1, 15.0F);
      if (!LocaleController.isRTL)
        break label1635;
      i = 5;
      label466: ((TextView)localObject1).setGravity(i);
      this.stateText = ((TextView)localObject1);
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-1, -2.0F, 51, 18.0F, 98.0F, 18.0F, 0.0F));
      this.durationText = ((TextView)localObject1);
      localObject1 = new TextView(this);
      ((TextView)localObject1).setTextColor(-855638017);
      ((TextView)localObject1).setSingleLine();
      ((TextView)localObject1).setEllipsize(TextUtils.TruncateAt.END);
      ((TextView)localObject1).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((TextView)localObject1).setShadowLayer(AndroidUtilities.dp(3.0F), 0.0F, AndroidUtilities.dp(0.6666667F), 1275068416);
      ((TextView)localObject1).setTextSize(1, 15.0F);
      if (!LocaleController.isRTL)
        break label1640;
      i = 5;
      label581: ((TextView)localObject1).setGravity(i);
      ((TextView)localObject1).setVisibility(8);
      this.stateText2 = ((TextView)localObject1);
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-1, -2.0F, 51, 18.0F, 98.0F, 18.0F, 0.0F));
      this.ellSpans = new TextAlphaSpan[] { new TextAlphaSpan(), new TextAlphaSpan(), new TextAlphaSpan() };
      localObject1 = new CheckableImageView(this);
      ((CheckableImageView)localObject1).setBackgroundResource(2130837627);
      localObject2 = getResources().getDrawable(2130837814).mutate();
      ((Drawable)localObject2).setAlpha(204);
      ((CheckableImageView)localObject1).setImageDrawable((Drawable)localObject2);
      ((CheckableImageView)localObject1).setScaleType(ImageView.ScaleType.CENTER);
      this.micToggle = ((CheckableImageView)localObject1);
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(38, 38.0F, 83, 16.0F, 0.0F, 0.0F, 10.0F));
      localObject1 = new CheckableImageView(this);
      ((CheckableImageView)localObject1).setBackgroundResource(2130837627);
      localObject2 = getResources().getDrawable(2130837869).mutate();
      ((Drawable)localObject2).setAlpha(204);
      ((CheckableImageView)localObject1).setImageDrawable((Drawable)localObject2);
      ((CheckableImageView)localObject1).setScaleType(ImageView.ScaleType.CENTER);
      this.spkToggle = ((CheckableImageView)localObject1);
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(38, 38.0F, 85, 0.0F, 0.0F, 16.0F, 10.0F));
      localObject1 = new ImageView(this);
      localObject2 = getResources().getDrawable(2130837765).mutate();
      ((Drawable)localObject2).setAlpha(204);
      ((ImageView)localObject1).setImageDrawable((Drawable)localObject2);
      ((ImageView)localObject1).setScaleType(ImageView.ScaleType.CENTER);
      this.chatBtn = ((ImageView)localObject1);
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(38, 38.0F, 81, 0.0F, 0.0F, 0.0F, 10.0F));
      localObject3 = new LinearLayout(this);
      ((LinearLayout)localObject3).setOrientation(0);
      localObject1 = new CallSwipeView(this);
      ((CallSwipeView)localObject1).setColor(-12207027);
      this.acceptSwipe = ((CallSwipeView)localObject1);
      ((LinearLayout)localObject3).addView((View)localObject1, LayoutHelper.createLinear(-1, 70, 1.0F, 4, 4, -35, 4));
      localObject2 = new CallSwipeView(this);
      ((CallSwipeView)localObject2).setColor(-1696188);
      this.declineSwipe = ((CallSwipeView)localObject2);
      ((LinearLayout)localObject3).addView((View)localObject2, LayoutHelper.createLinear(-1, 70, 1.0F, -35, 4, 4, 4));
      this.swipeViewsWrap = ((LinearLayout)localObject3);
      localFrameLayout.addView((View)localObject3, LayoutHelper.createFrame(-1, -2.0F, 80, 20.0F, 0.0F, 20.0F, 68.0F));
      localObject3 = new ImageView(this);
      Object localObject4 = new FabBackgroundDrawable();
      ((FabBackgroundDrawable)localObject4).setColor(-12207027);
      ((ImageView)localObject3).setBackgroundDrawable((Drawable)localObject4);
      ((ImageView)localObject3).setImageResource(2130837753);
      ((ImageView)localObject3).setScaleType(ImageView.ScaleType.MATRIX);
      localObject4 = new Matrix();
      ((Matrix)localObject4).setTranslate(AndroidUtilities.dp(17.0F), AndroidUtilities.dp(17.0F));
      ((Matrix)localObject4).postRotate(-135.0F, AndroidUtilities.dp(35.0F), AndroidUtilities.dp(35.0F));
      ((ImageView)localObject3).setImageMatrix((Matrix)localObject4);
      this.acceptBtn = ((View)localObject3);
      localFrameLayout.addView((View)localObject3, LayoutHelper.createFrame(78, 78.0F, 83, 20.0F, 0.0F, 0.0F, 68.0F));
      localObject4 = new ImageView(this);
      FabBackgroundDrawable localFabBackgroundDrawable = new FabBackgroundDrawable();
      localFabBackgroundDrawable.setColor(-1696188);
      ((ImageView)localObject4).setBackgroundDrawable(localFabBackgroundDrawable);
      ((ImageView)localObject4).setImageResource(2130837753);
      ((ImageView)localObject4).setScaleType(ImageView.ScaleType.CENTER);
      this.declineBtn = ((View)localObject4);
      localFrameLayout.addView((View)localObject4, LayoutHelper.createFrame(78, 78.0F, 85, 0.0F, 0.0F, 20.0F, 68.0F));
      ((CallSwipeView)localObject1).setViewToDrag((View)localObject3, false);
      ((CallSwipeView)localObject2).setViewToDrag((View)localObject4, true);
      localObject1 = new FrameLayout(this);
      localObject2 = new FabBackgroundDrawable();
      ((FabBackgroundDrawable)localObject2).setColor(-1696188);
      this.endBtnBg = ((FabBackgroundDrawable)localObject2);
      ((FrameLayout)localObject1).setBackgroundDrawable((Drawable)localObject2);
      localObject2 = new ImageView(this);
      ((ImageView)localObject2).setImageResource(2130837753);
      ((ImageView)localObject2).setScaleType(ImageView.ScaleType.CENTER);
      this.endBtnIcon = ((View)localObject2);
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(70, 70.0F));
      ((FrameLayout)localObject1).setForeground(getResources().getDrawable(2130837708));
      this.endBtn = ((View)localObject1);
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(78, 78.0F, 81, 0.0F, 0.0F, 0.0F, 68.0F));
      localObject1 = new ImageView(this);
      localObject2 = new FabBackgroundDrawable();
      ((FabBackgroundDrawable)localObject2).setColor(-1);
      ((ImageView)localObject1).setBackgroundDrawable((Drawable)localObject2);
      ((ImageView)localObject1).setImageResource(2130837706);
      ((ImageView)localObject1).setColorFilter(-1996488704);
      ((ImageView)localObject1).setScaleType(ImageView.ScaleType.CENTER);
      ((ImageView)localObject1).setVisibility(8);
      this.cancelBtn = ((View)localObject1);
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(78, 78.0F, 83, 52.0F, 0.0F, 0.0F, 68.0F));
      this.emojiWrap = new LinearLayout(this);
      this.emojiWrap.setOrientation(0);
      this.emojiWrap.setClipToPadding(false);
      this.emojiWrap.setPivotX(0.0F);
      this.emojiWrap.setPivotY(0.0F);
      this.emojiWrap.setPadding(AndroidUtilities.dp(14.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(14.0F), AndroidUtilities.dp(10.0F));
      i = 0;
      label1550: if (i >= 4)
        break label1652;
      localObject1 = new ImageView(this);
      ((ImageView)localObject1).setScaleType(ImageView.ScaleType.FIT_XY);
      localObject2 = this.emojiWrap;
      if (i != 0)
        break label1645;
      f = 0.0F;
    }
    while (true)
    {
      ((LinearLayout)localObject2).addView((View)localObject1, LayoutHelper.createLinear(22, 22, f, 0.0F, 0.0F, 0.0F));
      this.keyEmojiViews[i] = localObject1;
      i += 1;
      break label1550;
      localObject2 = localObject1;
      break;
      label1620: localObject1 = null;
      break label201;
      label1625: i = 3;
      break label230;
      label1630: i = 3;
      break label323;
      label1635: i = 3;
      break label466;
      label1640: i = 3;
      break label581;
      label1645: f = 4.0F;
    }
    label1652: this.emojiWrap.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        boolean bool = false;
        if (VoIPActivity.this.emojiTooltipVisible)
        {
          VoIPActivity.this.setEmojiTooltipVisible(false);
          if (VoIPActivity.this.tooltipHider != null)
          {
            VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
            VoIPActivity.access$1702(VoIPActivity.this, null);
          }
        }
        paramView = VoIPActivity.this;
        if (!VoIPActivity.this.emojiExpanded)
          bool = true;
        paramView.setEmojiExpanded(bool);
      }
    });
    localFrameLayout.addView(this.emojiWrap, LayoutHelper.createFrame(-2, -2, 53));
    this.emojiWrap.setOnLongClickListener(new View.OnLongClickListener()
    {
      public boolean onLongClick(View paramView)
      {
        boolean bool = false;
        if (VoIPActivity.this.emojiExpanded)
          return false;
        if (VoIPActivity.this.tooltipHider != null)
        {
          VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
          VoIPActivity.access$1702(VoIPActivity.this, null);
        }
        paramView = VoIPActivity.this;
        if (!VoIPActivity.this.emojiTooltipVisible)
          bool = true;
        paramView.setEmojiTooltipVisible(bool);
        if (VoIPActivity.this.emojiTooltipVisible)
          VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.access$1702(VoIPActivity.this, new Runnable()
          {
            public void run()
            {
              VoIPActivity.access$1702(VoIPActivity.this, null);
              VoIPActivity.this.setEmojiTooltipVisible(false);
            }
          }), 5000L);
        return true;
      }
    });
    this.emojiExpandedText = new TextView(this);
    this.emojiExpandedText.setTextSize(1, 16.0F);
    this.emojiExpandedText.setTextColor(-1);
    this.emojiExpandedText.setGravity(17);
    this.emojiExpandedText.setAlpha(0.0F);
    localFrameLayout.addView(this.emojiExpandedText, LayoutHelper.createFrame(-1, -2.0F, 17, 10.0F, 32.0F, 10.0F, 0.0F));
    this.hintTextView = new CorrectlyMeasuringTextView(this);
    this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0F), -231525581));
    this.hintTextView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
    this.hintTextView.setTextSize(1, 14.0F);
    this.hintTextView.setPadding(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F));
    this.hintTextView.setGravity(17);
    this.hintTextView.setMaxWidth(AndroidUtilities.dp(300.0F));
    this.hintTextView.setAlpha(0.0F);
    localFrameLayout.addView(this.hintTextView, LayoutHelper.createFrame(-2, -2.0F, 53, 0.0F, 42.0F, 10.0F, 0.0F));
    int i = this.stateText.getPaint().getAlpha();
    this.ellAnimator = new AnimatorSet();
    this.ellAnimator.playTogether(new Animator[] { createAlphaAnimator(this.ellSpans[0], 0, i, 0, 300), createAlphaAnimator(this.ellSpans[1], 0, i, 150, 300), createAlphaAnimator(this.ellSpans[2], 0, i, 300, 300), createAlphaAnimator(this.ellSpans[0], i, 0, 1000, 400), createAlphaAnimator(this.ellSpans[1], i, 0, 1000, 400), createAlphaAnimator(this.ellSpans[2], i, 0, 1000, 400) });
    this.ellAnimator.addListener(new AnimatorListenerAdapter()
    {
      private Runnable restarter = new Runnable()
      {
        public void run()
        {
          if (!VoIPActivity.this.isFinishing())
            VoIPActivity.this.ellAnimator.start();
        }
      };

      public void onAnimationEnd(Animator paramAnimator)
      {
        if (!VoIPActivity.this.isFinishing())
          VoIPActivity.this.content.postDelayed(this.restarter, 300L);
      }
    });
    localFrameLayout.setClipChildren(false);
    this.content = localFrameLayout;
    return (View)(View)(View)(View)localFrameLayout;
  }

  private void hideRetry()
  {
    if (this.retryAnim != null)
      this.retryAnim.cancel();
    this.retrying = false;
    this.spkToggle.setVisibility(0);
    this.micToggle.setVisibility(0);
    this.chatBtn.setVisibility(0);
    ObjectAnimator localObjectAnimator;
    if (Build.VERSION.SDK_INT >= 21)
      localObjectAnimator = ObjectAnimator.ofArgb(this.endBtnBg, "color", new int[] { -12207027, -1696188 });
    while (true)
    {
      AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.playTogether(new Animator[] { localObjectAnimator, ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[] { -135.0F, 0.0F }), ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.cancelBtn, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.spkToggle, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.micToggle, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.chatBtn, "alpha", new float[] { 1.0F }) });
      localAnimatorSet.setStartDelay(200L);
      localAnimatorSet.setDuration(300L);
      localAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          VoIPActivity.this.cancelBtn.setVisibility(8);
          VoIPActivity.this.endBtn.setEnabled(true);
          VoIPActivity.access$2902(VoIPActivity.this, null);
        }
      });
      this.retryAnim = localAnimatorSet;
      localAnimatorSet.start();
      return;
      localObjectAnimator = ObjectAnimator.ofInt(this.endBtnBg, "color", new int[] { -12207027, -1696188 });
      localObjectAnimator.setEvaluator(new ArgbEvaluator());
    }
  }

  private void setEmojiExpanded(boolean paramBoolean)
  {
    if (this.emojiExpanded == paramBoolean)
      return;
    this.emojiExpanded = paramBoolean;
    if (this.emojiAnimator != null)
      this.emojiAnimator.cancel();
    if (paramBoolean)
    {
      localObject = new int[2];
      Object tmp39_37 = localObject;
      tmp39_37[0] = 0;
      Object tmp43_39 = tmp39_37;
      tmp43_39[1] = 0;
      tmp43_39;
      int[] arrayOfInt = new int[2];
      int[] tmp55_53 = arrayOfInt;
      tmp55_53[0] = 0;
      int[] tmp59_55 = tmp55_53;
      tmp59_55[1] = 0;
      tmp59_55;
      this.emojiWrap.getLocationInWindow(localObject);
      this.emojiExpandedText.getLocationInWindow(arrayOfInt);
      Rect localRect = new Rect();
      getWindow().getDecorView().getGlobalVisibleRect(localRect);
      int i = arrayOfInt[1];
      int j = localObject[1];
      int k = this.emojiWrap.getHeight();
      int m = AndroidUtilities.dp(32.0F);
      int n = this.emojiWrap.getHeight();
      int i1 = localRect.width() / 2;
      int i2 = Math.round(this.emojiWrap.getWidth() * 2.5F) / 2;
      int i3 = localObject[0];
      localObject = new AnimatorSet();
      ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.emojiWrap, "translationY", new float[] { i - (j + k) - m - n }), ObjectAnimator.ofFloat(this.emojiWrap, "translationX", new float[] { i1 - i2 - i3 }), ObjectAnimator.ofFloat(this.emojiWrap, "scaleX", new float[] { 2.5F }), ObjectAnimator.ofFloat(this.emojiWrap, "scaleY", new float[] { 2.5F }), ObjectAnimator.ofFloat(this.blurOverlayView1, "alpha", new float[] { this.blurOverlayView1.getAlpha(), 1.0F, 1.0F }), ObjectAnimator.ofFloat(this.blurOverlayView2, "alpha", new float[] { this.blurOverlayView2.getAlpha(), this.blurOverlayView2.getAlpha(), 1.0F }), ObjectAnimator.ofFloat(this.emojiExpandedText, "alpha", new float[] { 1.0F }) });
      ((AnimatorSet)localObject).setDuration(300L);
      ((AnimatorSet)localObject).setInterpolator(CubicBezierInterpolator.DEFAULT);
      this.emojiAnimator = ((AnimatorSet)localObject);
      ((AnimatorSet)localObject).addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          VoIPActivity.access$4302(VoIPActivity.this, null);
        }
      });
      ((AnimatorSet)localObject).start();
      return;
    }
    Object localObject = new AnimatorSet();
    ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.emojiWrap, "translationX", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.emojiWrap, "translationY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.emojiWrap, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.emojiWrap, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.blurOverlayView1, "alpha", new float[] { this.blurOverlayView1.getAlpha(), this.blurOverlayView1.getAlpha(), 0.0F }), ObjectAnimator.ofFloat(this.blurOverlayView2, "alpha", new float[] { this.blurOverlayView2.getAlpha(), 0.0F, 0.0F }), ObjectAnimator.ofFloat(this.emojiExpandedText, "alpha", new float[] { 0.0F }) });
    ((AnimatorSet)localObject).setDuration(300L);
    ((AnimatorSet)localObject).setInterpolator(CubicBezierInterpolator.DEFAULT);
    this.emojiAnimator = ((AnimatorSet)localObject);
    ((AnimatorSet)localObject).addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        VoIPActivity.access$4302(VoIPActivity.this, null);
      }
    });
    ((AnimatorSet)localObject).start();
  }

  private void setEmojiTooltipVisible(boolean paramBoolean)
  {
    this.emojiTooltipVisible = paramBoolean;
    if (this.tooltipAnim != null)
      this.tooltipAnim.cancel();
    this.hintTextView.setVisibility(0);
    Object localObject = this.hintTextView;
    float f;
    if (paramBoolean)
      f = 1.0F;
    while (true)
    {
      localObject = ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f });
      ((ObjectAnimator)localObject).setDuration(300L);
      ((ObjectAnimator)localObject).setInterpolator(CubicBezierInterpolator.DEFAULT);
      ((ObjectAnimator)localObject).addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          VoIPActivity.access$4202(VoIPActivity.this, null);
        }
      });
      this.tooltipAnim = ((Animator)localObject);
      ((ObjectAnimator)localObject).start();
      return;
      f = 0.0F;
    }
  }

  private void setStateTextAnimated(String paramString, boolean paramBoolean)
  {
    if (paramString.equals(this.lastStateText))
      return;
    this.lastStateText = paramString;
    if (this.textChangingAnim != null)
      this.textChangingAnim.cancel();
    float f;
    if (paramBoolean)
    {
      if (!this.ellAnimator.isRunning())
        this.ellAnimator.start();
      paramString = new SpannableStringBuilder(paramString.toUpperCase());
      Object localObject = this.ellSpans;
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        localObject[i].setAlpha(0);
        i += 1;
      }
      localObject = new SpannableString("...");
      ((SpannableString)localObject).setSpan(this.ellSpans[0], 0, 1, 0);
      ((SpannableString)localObject).setSpan(this.ellSpans[1], 1, 2, 0);
      ((SpannableString)localObject).setSpan(this.ellSpans[2], 2, 3, 0);
      paramString.append((CharSequence)localObject);
      this.stateText2.setText(paramString);
      this.stateText2.setVisibility(0);
      paramString = this.stateText;
      if (!LocaleController.isRTL)
        break label572;
      f = this.stateText.getWidth();
      label200: paramString.setPivotX(f);
      this.stateText.setPivotY(this.stateText.getHeight() / 2);
      paramString = this.stateText2;
      if (!LocaleController.isRTL)
        break label577;
      f = this.stateText.getWidth();
    }
    while (true)
    {
      paramString.setPivotX(f);
      this.stateText2.setPivotY(this.stateText.getHeight() / 2);
      this.durationText = this.stateText2;
      paramString = new AnimatorSet();
      paramString.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.stateText2, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.stateText2, "translationY", new float[] { this.stateText.getHeight() / 2, 0.0F }), ObjectAnimator.ofFloat(this.stateText2, "scaleX", new float[] { 0.7F, 1.0F }), ObjectAnimator.ofFloat(this.stateText2, "scaleY", new float[] { 0.7F, 1.0F }), ObjectAnimator.ofFloat(this.stateText, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.stateText, "translationY", new float[] { 0.0F, -this.stateText.getHeight() / 2 }), ObjectAnimator.ofFloat(this.stateText, "scaleX", new float[] { 1.0F, 0.7F }), ObjectAnimator.ofFloat(this.stateText, "scaleY", new float[] { 1.0F, 0.7F }) });
      paramString.setDuration(200L);
      paramString.setInterpolator(CubicBezierInterpolator.DEFAULT);
      paramString.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          VoIPActivity.access$4002(VoIPActivity.this, null);
          VoIPActivity.this.stateText2.setVisibility(8);
          VoIPActivity.access$2602(VoIPActivity.this, VoIPActivity.this.stateText);
          VoIPActivity.this.stateText.setTranslationY(0.0F);
          VoIPActivity.this.stateText.setScaleX(1.0F);
          VoIPActivity.this.stateText.setScaleY(1.0F);
          VoIPActivity.this.stateText.setAlpha(1.0F);
          VoIPActivity.this.stateText.setText(VoIPActivity.this.stateText2.getText());
        }
      });
      this.textChangingAnim = paramString;
      paramString.start();
      return;
      if (this.ellAnimator.isRunning())
        this.ellAnimator.cancel();
      paramString = paramString.toUpperCase();
      break;
      label572: f = 0.0F;
      break label200;
      label577: f = 0.0F;
    }
  }

  private void showDebugAlert()
  {
    if (VoIPService.getSharedInstance() == null)
      return;
    AlertDialog localAlertDialog = new AlertDialog.Builder(this).setTitle("libtgvoip v" + VoIPController.getVersion() + " debug").setMessage(VoIPService.getSharedInstance().getDebugString()).setPositiveButton("Close", null).create();
    15 local15 = new Runnable(localAlertDialog)
    {
      public void run()
      {
        if ((!this.val$dlg.isShowing()) || (VoIPActivity.this.isFinishing()) || (VoIPService.getSharedInstance() == null))
          return;
        this.val$dlg.setMessage(VoIPService.getSharedInstance().getDebugString());
        this.val$dlg.getWindow().getDecorView().postDelayed(this, 500L);
      }
    };
    localAlertDialog.show();
    localAlertDialog.getWindow().getDecorView().postDelayed(local15, 500L);
  }

  private void showDebugCtlAlert()
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    16 local16 = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        switch (paramInt)
        {
        default:
          return;
        case 0:
          VoIPActivity.this.showNumberPickerDialog(8, 32, VoIPActivity.this.audioBitrate, "Audio bitrate (kbit/s)", new NumberPicker.OnValueChangeListener()
          {
            public void onValueChange(NumberPicker paramNumberPicker, int paramInt1, int paramInt2)
            {
              VoIPActivity.access$2202(VoIPActivity.this, paramInt2);
              VoIPService.getSharedInstance().debugCtl(1, paramInt2 * 1000);
            }
          });
          return;
        case 1:
          VoIPActivity.this.showNumberPickerDialog(0, 100, VoIPActivity.this.packetLossPercent, "Expected packet loss %", new NumberPicker.OnValueChangeListener()
          {
            public void onValueChange(NumberPicker paramNumberPicker, int paramInt1, int paramInt2)
            {
              VoIPActivity.access$2402(VoIPActivity.this, paramInt2);
              VoIPService.getSharedInstance().debugCtl(2, paramInt2);
            }
          });
          return;
        case 2:
          VoIPService.getSharedInstance().debugCtl(3, 0);
          return;
        case 3:
          VoIPService.getSharedInstance().debugCtl(3, 1);
          return;
        case 4:
          VoIPService.getSharedInstance().debugCtl(4, 0);
          return;
        case 5:
        }
        VoIPService.getSharedInstance().debugCtl(4, 1);
      }
    };
    localBuilder.setItems(new String[] { "Set audio bitrate", "Set expect packet loss %", "Disable p2p", "Enable p2p", "Disable AEC", "Enable AEC" }, local16).show();
  }

  private void showErrorDialog(CharSequence paramCharSequence)
  {
    paramCharSequence = new AlertDialog.Builder(this).setTitle(LocaleController.getString("VoipFailed", 2131166584)).setMessage(paramCharSequence).setPositiveButton(LocaleController.getString("OK", 2131166153), null).show();
    paramCharSequence.setCanceledOnTouchOutside(true);
    paramCharSequence.setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramDialogInterface)
      {
        VoIPActivity.this.finish();
      }
    });
  }

  private void showNumberPickerDialog(int paramInt1, int paramInt2, int paramInt3, String paramString, NumberPicker.OnValueChangeListener paramOnValueChangeListener)
  {
    NumberPicker localNumberPicker = new NumberPicker(this);
    localNumberPicker.setMinValue(paramInt1);
    localNumberPicker.setMaxValue(paramInt2);
    localNumberPicker.setValue(paramInt3);
    localNumberPicker.setOnValueChangedListener(paramOnValueChangeListener);
    new AlertDialog.Builder(this).setTitle(paramString).setView(localNumberPicker).setPositiveButton("Done", null).show();
  }

  private void showRetry()
  {
    if (this.retryAnim != null)
      this.retryAnim.cancel();
    this.endBtn.setEnabled(false);
    this.retrying = true;
    this.cancelBtn.setVisibility(0);
    this.cancelBtn.setAlpha(0.0F);
    AnimatorSet localAnimatorSet = new AnimatorSet();
    ObjectAnimator localObjectAnimator;
    if (Build.VERSION.SDK_INT >= 21)
      localObjectAnimator = ObjectAnimator.ofArgb(this.endBtnBg, "color", new int[] { -1696188, -12207027 });
    while (true)
    {
      localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.cancelBtn, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[] { 0.0F, this.content.getWidth() / 2 - AndroidUtilities.dp(52.0F) - this.endBtn.getWidth() / 2 }), localObjectAnimator, ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[] { 0.0F, -135.0F }), ObjectAnimator.ofFloat(this.spkToggle, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.micToggle, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.chatBtn, "alpha", new float[] { 0.0F }) });
      localAnimatorSet.setStartDelay(200L);
      localAnimatorSet.setDuration(300L);
      localAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          VoIPActivity.this.spkToggle.setVisibility(8);
          VoIPActivity.this.micToggle.setVisibility(8);
          VoIPActivity.this.chatBtn.setVisibility(8);
          VoIPActivity.access$2902(VoIPActivity.this, null);
          VoIPActivity.this.endBtn.setEnabled(true);
        }
      });
      this.retryAnim = localAnimatorSet;
      localAnimatorSet.start();
      return;
      localObjectAnimator = ObjectAnimator.ofInt(this.endBtnBg, "color", new int[] { -1696188, -12207027 });
      localObjectAnimator.setEvaluator(new ArgbEvaluator());
    }
  }

  private void startUpdatingCallDuration()
  {
    new Runnable()
    {
      public void run()
      {
        if ((VoIPActivity.this.isFinishing()) || (VoIPService.getSharedInstance() == null));
        do
          return;
        while (VoIPActivity.this.callState != 3);
        long l = VoIPService.getSharedInstance().getCallDuration() / 1000L;
        TextView localTextView = VoIPActivity.this.durationText;
        if (l > 3600L);
        for (String str = String.format("%d:%02d:%02d", new Object[] { Long.valueOf(l / 3600L), Long.valueOf(l % 3600L / 60L), Long.valueOf(l % 60L) }); ; str = String.format("%d:%02d", new Object[] { Long.valueOf(l / 60L), Long.valueOf(l % 60L) }))
        {
          localTextView.setText(str);
          VoIPActivity.this.durationText.postDelayed(this, 500L);
          return;
        }
      }
    }
    .run();
  }

  private void updateBlurredPhotos(Bitmap paramBitmap)
  {
    new Thread(new Runnable(paramBitmap)
    {
      public void run()
      {
        Bitmap localBitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
        Object localObject1 = new Canvas(localBitmap);
        ((Canvas)localObject1).drawBitmap(this.val$src, null, new Rect(0, 0, 150, 150), new Paint(2));
        Utilities.blurBitmap(localBitmap, 3, 0, localBitmap.getWidth(), localBitmap.getHeight(), localBitmap.getRowBytes());
        Object localObject2 = b.a(this.val$src).a();
        Paint localPaint = new Paint();
        localPaint.setColor(((b)localObject2).a(-11242343) & 0xFFFFFF | 0x44000000);
        ((Canvas)localObject1).drawColor(637534208);
        ((Canvas)localObject1).drawRect(0.0F, 0.0F, ((Canvas)localObject1).getWidth(), ((Canvas)localObject1).getHeight(), localPaint);
        localObject1 = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        localObject2 = new Canvas((Bitmap)localObject1);
        ((Canvas)localObject2).drawBitmap(this.val$src, null, new Rect(0, 0, 50, 50), new Paint(2));
        Utilities.blurBitmap(localObject1, 3, 0, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight(), ((Bitmap)localObject1).getRowBytes());
        localPaint.setAlpha(102);
        ((Canvas)localObject2).drawRect(0.0F, 0.0F, ((Canvas)localObject2).getWidth(), ((Canvas)localObject2).getHeight(), localPaint);
        VoIPActivity.access$4402(VoIPActivity.this, localBitmap);
        VoIPActivity.access$4502(VoIPActivity.this, (Bitmap)localObject1);
        VoIPActivity.this.runOnUiThread(new Runnable()
        {
          public void run()
          {
            VoIPActivity.this.blurOverlayView1.setImageBitmap(VoIPActivity.this.blurredPhoto1);
            VoIPActivity.this.blurOverlayView2.setImageBitmap(VoIPActivity.this.blurredPhoto2);
          }
        });
      }
    }).start();
  }

  private void updateKeyView()
  {
    if (VoIPService.getSharedInstance() == null);
    while (true)
    {
      return;
      new IdenticonDrawable().setColors(new int[] { 16777215, -1, -1711276033, 872415231 });
      Object localObject1 = new TLRPC.EncryptedChat();
      try
      {
        Object localObject2 = new ByteArrayOutputStream();
        ((ByteArrayOutputStream)localObject2).write(VoIPService.getSharedInstance().getEncryptionKey());
        ((ByteArrayOutputStream)localObject2).write(VoIPService.getSharedInstance().getGA());
        ((TLRPC.EncryptedChat)localObject1).auth_key = ((ByteArrayOutputStream)localObject2).toByteArray();
        label86: localObject1 = EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(((TLRPC.EncryptedChat)localObject1).auth_key, 0, ((TLRPC.EncryptedChat)localObject1).auth_key.length));
        int i = 0;
        while (i < 4)
        {
          localObject2 = Emoji.getEmojiDrawable(localObject1[i]);
          ((Drawable)localObject2).setBounds(0, 0, AndroidUtilities.dp(22.0F), AndroidUtilities.dp(22.0F));
          this.keyEmojiViews[i].setImageDrawable((Drawable)localObject2);
          i += 1;
        }
      }
      catch (Exception localException)
      {
        break label86;
      }
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.emojiDidLoaded)
    {
      paramArrayOfObject = this.keyEmojiViews;
      int j = paramArrayOfObject.length;
      int i = 0;
      while (i < j)
      {
        paramArrayOfObject[i].invalidate();
        i += 1;
      }
    }
    if (paramInt == NotificationCenter.closeInCallActivity)
      finish();
  }

  public void onAudioSettingsChanged()
  {
    if (VoIPService.getSharedInstance() == null)
      return;
    this.micToggle.setChecked(VoIPService.getSharedInstance().isMicMute());
    if ((!VoIPService.getSharedInstance().hasEarpiece()) && (!VoIPService.getSharedInstance().isBluetoothHeadsetConnected()))
    {
      this.spkToggle.setVisibility(4);
      return;
    }
    this.spkToggle.setVisibility(0);
    AudioManager localAudioManager = (AudioManager)getSystemService("audio");
    if (!VoIPService.getSharedInstance().hasEarpiece())
    {
      this.spkToggle.setImageResource(2130837750);
      this.spkToggle.setChecked(localAudioManager.isBluetoothScoOn());
      return;
    }
    if (VoIPService.getSharedInstance().isBluetoothHeadsetConnected())
    {
      if (localAudioManager.isBluetoothScoOn())
        this.spkToggle.setImageResource(2130837750);
      while (true)
      {
        this.spkToggle.setChecked(false);
        return;
        if (localAudioManager.isSpeakerphoneOn())
        {
          this.spkToggle.setImageResource(2130837869);
          continue;
        }
        this.spkToggle.setImageResource(2130837828);
      }
    }
    this.spkToggle.setImageResource(2130837869);
    this.spkToggle.setChecked(localAudioManager.isSpeakerphoneOn());
  }

  public void onBackPressed()
  {
    if (this.emojiExpanded)
      setEmojiExpanded(false);
    do
      return;
    while (this.isIncomingWaiting);
    super.onBackPressed();
  }

  protected void onCreate(Bundle paramBundle)
  {
    requestWindowFeature(1);
    getWindow().addFlags(524288);
    super.onCreate(paramBundle);
    if (VoIPService.getSharedInstance() == null)
    {
      finish();
      return;
    }
    if ((getResources().getConfiguration().screenLayout & 0xF) < 3)
      setRequestedOrientation(1);
    paramBundle = createContentView();
    setContentView(paramBundle);
    if (Build.VERSION.SDK_INT >= 21)
    {
      getWindow().addFlags(-2147483648);
      getWindow().setStatusBarColor(-16777216);
    }
    this.user = VoIPService.getSharedInstance().getUser();
    if (this.user.photo != null)
    {
      this.photoView.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate()
      {
        public void didSetImage(ImageReceiver paramImageReceiver, boolean paramBoolean1, boolean paramBoolean2)
        {
          paramImageReceiver = paramImageReceiver.getBitmap();
          if (paramImageReceiver != null)
            VoIPActivity.this.updateBlurredPhotos(paramImageReceiver);
        }
      });
      this.photoView.setImage(this.user.photo.photo_big, null, new ColorDrawable(-16777216));
    }
    while (true)
    {
      setVolumeControlStream(0);
      this.nameText.setOnClickListener(new View.OnClickListener()
      {
        private int tapCount = 0;

        public void onClick(View paramView)
        {
          if ((BuildVars.DEBUG_VERSION) || (this.tapCount == 9))
          {
            VoIPActivity.this.showDebugAlert();
            this.tapCount = 0;
            return;
          }
          this.tapCount += 1;
        }
      });
      this.endBtn.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          VoIPActivity.this.endBtn.setEnabled(false);
          if (VoIPActivity.this.retrying)
          {
            paramView = new Intent(VoIPActivity.this, VoIPService.class);
            paramView.putExtra("user_id", VoIPActivity.this.user.id);
            paramView.putExtra("is_outgoing", true);
            paramView.putExtra("start_incall_activity", false);
            VoIPActivity.this.startService(paramView);
            VoIPActivity.this.hideRetry();
            VoIPActivity.this.endBtn.postDelayed(new Runnable()
            {
              public void run()
              {
                if ((VoIPService.getSharedInstance() == null) && (!VoIPActivity.this.isFinishing()))
                  VoIPActivity.this.endBtn.postDelayed(this, 100L);
                do
                  return;
                while (VoIPService.getSharedInstance() == null);
                VoIPService.getSharedInstance().registerStateListener(VoIPActivity.this);
              }
            }
            , 100L);
          }
          do
            return;
          while (VoIPService.getSharedInstance() == null);
          VoIPService.getSharedInstance().hangUp();
        }
      });
      this.spkToggle.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          boolean bool = false;
          paramView = VoIPService.getSharedInstance();
          if (paramView == null)
            return;
          if ((paramView.isBluetoothHeadsetConnected()) && (paramView.hasEarpiece()))
          {
            paramView = new BottomSheet.Builder(VoIPActivity.this);
            localObject = LocaleController.getString("VoipAudioRoutingBluetooth", 2131166575);
            String str1 = LocaleController.getString("VoipAudioRoutingEarpiece", 2131166576);
            String str2 = LocaleController.getString("VoipAudioRoutingSpeaker", 2131166577);
            1 local1 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                paramDialogInterface = (AudioManager)VoIPActivity.this.getSystemService("audio");
                if (VoIPService.getSharedInstance() == null)
                  return;
                switch (paramInt)
                {
                default:
                case 0:
                case 1:
                case 2:
                }
                while (true)
                {
                  VoIPActivity.this.onAudioSettingsChanged();
                  return;
                  paramDialogInterface.setBluetoothScoOn(true);
                  paramDialogInterface.setSpeakerphoneOn(false);
                  continue;
                  paramDialogInterface.setBluetoothScoOn(false);
                  paramDialogInterface.setSpeakerphoneOn(false);
                  continue;
                  paramDialogInterface.setBluetoothScoOn(false);
                  paramDialogInterface.setSpeakerphoneOn(true);
                }
              }
            };
            paramView.setItems(new CharSequence[] { localObject, str1, str2 }, new int[] { 2130837750, 2130837828, 2130837869 }, local1).show();
            return;
          }
          if (!VoIPActivity.this.spkToggle.isChecked())
            bool = true;
          VoIPActivity.this.spkToggle.setChecked(bool);
          Object localObject = (AudioManager)VoIPActivity.this.getSystemService("audio");
          if (paramView.hasEarpiece())
          {
            ((AudioManager)localObject).setSpeakerphoneOn(bool);
            return;
          }
          ((AudioManager)localObject).setBluetoothScoOn(bool);
        }
      });
      this.micToggle.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (VoIPService.getSharedInstance() == null)
          {
            VoIPActivity.this.finish();
            return;
          }
          if (!VoIPActivity.this.micToggle.isChecked());
          for (boolean bool = true; ; bool = false)
          {
            VoIPActivity.this.micToggle.setChecked(bool);
            VoIPService.getSharedInstance().setMicMute(bool);
            return;
          }
        }
      });
      this.chatBtn.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          paramView = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
          paramView.setAction("com.tmessages.openchat" + Math.random() + 2147483647);
          paramView.setFlags(32768);
          paramView.putExtra("userId", VoIPActivity.this.user.id);
          VoIPActivity.this.startActivity(paramView);
          VoIPActivity.this.finish();
        }
      });
      this.spkToggle.setChecked(((AudioManager)getSystemService("audio")).isSpeakerphoneOn());
      this.micToggle.setChecked(VoIPService.getSharedInstance().isMicMute());
      onAudioSettingsChanged();
      this.nameText.setText(ContactsController.formatName(this.user.first_name, this.user.last_name));
      VoIPService.getSharedInstance().registerStateListener(this);
      this.acceptSwipe.setListener(new CallSwipeView.Listener()
      {
        public void onDragCancel()
        {
          if (VoIPActivity.this.currentDeclineAnim != null)
            VoIPActivity.this.currentDeclineAnim.cancel();
          AnimatorSet localAnimatorSet = new AnimatorSet();
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(VoIPActivity.access$900(VoIPActivity.this), "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(VoIPActivity.access$1300(VoIPActivity.this), "alpha", new float[] { 1.0F }) });
          localAnimatorSet.setDuration(200L);
          localAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
          localAnimatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              VoIPActivity.access$1202(VoIPActivity.this, null);
            }
          });
          VoIPActivity.access$1202(VoIPActivity.this, localAnimatorSet);
          localAnimatorSet.start();
          VoIPActivity.this.declineSwipe.startAnimatingArrows();
        }

        public void onDragComplete()
        {
          VoIPActivity.this.acceptSwipe.setEnabled(false);
          VoIPActivity.this.declineSwipe.setEnabled(false);
          if (VoIPService.getSharedInstance() == null)
          {
            VoIPActivity.this.finish();
            return;
          }
          VoIPActivity.access$1002(VoIPActivity.this, true);
          if ((Build.VERSION.SDK_INT >= 23) && (VoIPActivity.this.checkSelfPermission("android.permission.RECORD_AUDIO") != 0))
          {
            VoIPActivity.this.requestPermissions(new String[] { "android.permission.RECORD_AUDIO" }, 101);
            return;
          }
          VoIPService.getSharedInstance().acceptIncomingCall();
          VoIPActivity.this.callAccepted();
        }

        public void onDragStart()
        {
          if (VoIPActivity.this.currentDeclineAnim != null)
            VoIPActivity.this.currentDeclineAnim.cancel();
          AnimatorSet localAnimatorSet = new AnimatorSet();
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(VoIPActivity.access$900(VoIPActivity.this), "alpha", new float[] { 0.2F }), ObjectAnimator.ofFloat(VoIPActivity.access$1300(VoIPActivity.this), "alpha", new float[] { 0.2F }) });
          localAnimatorSet.setDuration(200L);
          localAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
          localAnimatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              VoIPActivity.access$1202(VoIPActivity.this, null);
            }
          });
          VoIPActivity.access$1202(VoIPActivity.this, localAnimatorSet);
          localAnimatorSet.start();
          VoIPActivity.this.declineSwipe.stopAnimatingArrows();
        }
      });
      this.declineSwipe.setListener(new CallSwipeView.Listener()
      {
        public void onDragCancel()
        {
          if (VoIPActivity.this.currentAcceptAnim != null)
            VoIPActivity.this.currentAcceptAnim.cancel();
          AnimatorSet localAnimatorSet = new AnimatorSet();
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(VoIPActivity.access$800(VoIPActivity.this), "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(VoIPActivity.access$1500(VoIPActivity.this), "alpha", new float[] { 1.0F }) });
          localAnimatorSet.setDuration(200L);
          localAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
          localAnimatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              VoIPActivity.access$1402(VoIPActivity.this, null);
            }
          });
          VoIPActivity.access$1402(VoIPActivity.this, localAnimatorSet);
          localAnimatorSet.start();
          VoIPActivity.this.acceptSwipe.startAnimatingArrows();
        }

        public void onDragComplete()
        {
          VoIPActivity.this.acceptSwipe.setEnabled(false);
          VoIPActivity.this.declineSwipe.setEnabled(false);
          if (VoIPService.getSharedInstance() != null)
          {
            VoIPService.getSharedInstance().declineIncomingCall(4, null);
            return;
          }
          VoIPActivity.this.finish();
        }

        public void onDragStart()
        {
          if (VoIPActivity.this.currentAcceptAnim != null)
            VoIPActivity.this.currentAcceptAnim.cancel();
          AnimatorSet localAnimatorSet = new AnimatorSet();
          localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(VoIPActivity.access$800(VoIPActivity.this), "alpha", new float[] { 0.2F }), ObjectAnimator.ofFloat(VoIPActivity.access$1500(VoIPActivity.this), "alpha", new float[] { 0.2F }) });
          localAnimatorSet.setDuration(200L);
          localAnimatorSet.setInterpolator(new DecelerateInterpolator());
          localAnimatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              VoIPActivity.access$1402(VoIPActivity.this, null);
            }
          });
          VoIPActivity.access$1402(VoIPActivity.this, localAnimatorSet);
          localAnimatorSet.start();
          VoIPActivity.this.acceptSwipe.stopAnimatingArrows();
        }
      });
      this.cancelBtn.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          VoIPActivity.this.finish();
        }
      });
      getWindow().getDecorView().setKeepScreenOn(true);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeInCallActivity);
      this.hintTextView.setText(LocaleController.formatString("CallEmojiKeyTooltip", 2131165412, new Object[] { this.user.first_name }));
      this.emojiExpandedText.setText(LocaleController.formatString("CallEmojiKeyTooltip", 2131165412, new Object[] { this.user.first_name }));
      return;
      this.photoView.setVisibility(8);
      paramBundle.setBackgroundDrawable(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] { -14994098, -14328963 }));
    }
  }

  protected void onDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeInCallActivity);
    if (VoIPService.getSharedInstance() != null)
      VoIPService.getSharedInstance().unregisterStateListener(this);
    super.onDestroy();
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((this.isIncomingWaiting) && ((paramInt == 25) || (paramInt == 24)))
    {
      if (VoIPService.getSharedInstance() != null)
        VoIPService.getSharedInstance().stopRinging();
      while (true)
      {
        return true;
        finish();
      }
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }

  protected void onPause()
  {
    super.onPause();
    if (this.retrying)
      finish();
    if (VoIPService.getSharedInstance() != null)
      VoIPService.getSharedInstance().onUIForegroundStateChanged(false);
  }

  @TargetApi(23)
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 101)
    {
      if ((paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0))
      {
        VoIPService.getSharedInstance().acceptIncomingCall();
        callAccepted();
      }
    }
    else
      return;
    if (!shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO"))
    {
      VoIPService.getSharedInstance().declineIncomingCall();
      VoIPHelper.permissionDenied(this, new Runnable()
      {
        public void run()
        {
          VoIPActivity.this.finish();
        }
      });
      return;
    }
    this.acceptSwipe.reset();
  }

  protected void onResume()
  {
    super.onResume();
    if (VoIPService.getSharedInstance() != null)
      VoIPService.getSharedInstance().onUIForegroundStateChanged(true);
  }

  public void onStateChanged(int paramInt)
  {
    this.callState = paramInt;
    runOnUiThread(new Runnable(paramInt)
    {
      public void run()
      {
        boolean bool2 = VoIPActivity.this.firstStateChange;
        boolean bool1;
        if (VoIPActivity.this.firstStateChange)
        {
          VoIPActivity localVoIPActivity = VoIPActivity.this;
          if (this.val$state == 10)
          {
            bool1 = true;
            if (!VoIPActivity.access$3202(localVoIPActivity, bool1))
              break label262;
            VoIPActivity.this.swipeViewsWrap.setVisibility(0);
            VoIPActivity.this.endBtn.setVisibility(8);
            VoIPActivity.this.micToggle.setVisibility(8);
            VoIPActivity.this.spkToggle.setVisibility(8);
            VoIPActivity.this.chatBtn.setVisibility(8);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                VoIPActivity.this.acceptSwipe.startAnimatingArrows();
                VoIPActivity.this.declineSwipe.startAnimatingArrows();
              }
            }
            , 500L);
            VoIPActivity.this.getWindow().addFlags(2097152);
            label129: if (this.val$state != 3)
              VoIPActivity.this.emojiWrap.setVisibility(8);
            VoIPActivity.access$3102(VoIPActivity.this, false);
          }
        }
        else
        {
          if ((VoIPActivity.this.isIncomingWaiting) && (this.val$state != 10) && (this.val$state != 6) && (this.val$state != 5))
          {
            VoIPActivity.access$3202(VoIPActivity.this, false);
            if (!VoIPActivity.this.didAcceptFromHere)
              VoIPActivity.this.callAccepted();
          }
          if (this.val$state != 10)
            break label313;
          VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipIncoming", 2131166589), false);
          VoIPActivity.this.getWindow().addFlags(2097152);
        }
        label262: label313: label744: 
        do
        {
          do
          {
            return;
            bool1 = false;
            break;
            VoIPActivity.this.swipeViewsWrap.setVisibility(8);
            VoIPActivity.this.acceptBtn.setVisibility(8);
            VoIPActivity.this.declineBtn.setVisibility(8);
            VoIPActivity.this.getWindow().clearFlags(2097152);
            break label129;
            if ((this.val$state == 1) || (this.val$state == 2))
            {
              VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipConnecting", 2131166580), true);
              return;
            }
            if (this.val$state == 7)
            {
              VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipExchangingKeys", 2131166583), true);
              return;
            }
            if (this.val$state == 8)
            {
              VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipWaiting", 2131166608), true);
              return;
            }
            if (this.val$state == 11)
            {
              VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRinging", 2131166604), true);
              return;
            }
            if (this.val$state == 9)
            {
              VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRequesting", 2131166603), true);
              return;
            }
            if (this.val$state == 5)
            {
              VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipHangingUp", 2131166587), true);
              return;
            }
            if (this.val$state == 6)
            {
              VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipCallEnded", 2131166579), false);
              VoIPActivity.this.stateText.postDelayed(new Runnable()
              {
                public void run()
                {
                  VoIPActivity.this.finish();
                }
              }
              , 200L);
              return;
            }
            if (this.val$state == 12)
            {
              VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipBusy", 2131166578), false);
              VoIPActivity.this.showRetry();
              return;
            }
            if (this.val$state != 3)
              break label744;
            if (!bool2)
            {
              i = VoIPActivity.this.getSharedPreferences("mainconfig", 0).getInt("call_emoji_tooltip_count", 0);
              if (i < 3)
              {
                VoIPActivity.this.setEmojiTooltipVisible(true);
                VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.access$1702(VoIPActivity.this, new Runnable()
                {
                  public void run()
                  {
                    VoIPActivity.access$1702(VoIPActivity.this, null);
                    VoIPActivity.this.setEmojiTooltipVisible(false);
                  }
                }), 5000L);
                VoIPActivity.this.getSharedPreferences("mainconfig", 0).edit().putInt("call_emoji_tooltip_count", i + 1).apply();
              }
            }
            VoIPActivity.this.setStateTextAnimated("0:00", false);
            VoIPActivity.this.startUpdatingCallDuration();
            VoIPActivity.this.updateKeyView();
          }
          while (VoIPActivity.this.emojiWrap.getVisibility() == 0);
          VoIPActivity.this.emojiWrap.setVisibility(0);
          VoIPActivity.this.emojiWrap.setAlpha(0.0F);
          VoIPActivity.this.emojiWrap.animate().alpha(1.0F).setDuration(200L).setInterpolator(new DecelerateInterpolator()).start();
          return;
        }
        while (this.val$state != 4);
        VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipFailed", 2131166584), false);
        if (VoIPService.getSharedInstance() != null);
        for (int i = VoIPService.getSharedInstance().getLastError(); i == 1; i = 0)
        {
          VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerIncompatible", 2131166600, new Object[] { ContactsController.formatName(VoIPActivity.access$400(VoIPActivity.this).first_name, VoIPActivity.access$400(VoIPActivity.this).last_name) })));
          return;
        }
        if (i == -1)
        {
          VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerOutdated", 2131166601, new Object[] { ContactsController.formatName(VoIPActivity.access$400(VoIPActivity.this).first_name, VoIPActivity.access$400(VoIPActivity.this).last_name) })));
          return;
        }
        if (i == -2)
        {
          VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", 2131165420, new Object[] { ContactsController.formatName(VoIPActivity.access$400(VoIPActivity.this).first_name, VoIPActivity.access$400(VoIPActivity.this).last_name) })));
          return;
        }
        if (i == 3)
        {
          VoIPActivity.this.showErrorDialog("Error initializing audio hardware");
          return;
        }
        if (i == -3)
        {
          VoIPActivity.this.finish();
          return;
        }
        VoIPActivity.this.stateText.postDelayed(new Runnable()
        {
          public void run()
          {
            VoIPActivity.this.finish();
          }
        }
        , 1000L);
      }
    });
  }

  private class TextAlphaSpan extends CharacterStyle
  {
    private int alpha = 0;

    public TextAlphaSpan()
    {
    }

    public int getAlpha()
    {
      return this.alpha;
    }

    public void setAlpha(int paramInt)
    {
      this.alpha = paramInt;
      VoIPActivity.this.stateText.invalidate();
      VoIPActivity.this.stateText2.invalidate();
    }

    public void updateDrawState(TextPaint paramTextPaint)
    {
      paramTextPaint.setAlpha(this.alpha);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.VoIPActivity
 * JD-Core Version:    0.6.0
 */