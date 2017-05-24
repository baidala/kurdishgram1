package org.vidogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.voip.VoIPService;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.AudioPlayerActivity;
import org.vidogram.ui.VoIPActivity;

public class FragmentContextView extends FrameLayout
  implements NotificationCenter.NotificationCenterDelegate
{
  private AnimatorSet animatorSet;
  private ImageView closeButton;
  private int currentStyle = -1;
  private BaseFragment fragment;
  private FrameLayout frameLayout;
  private MessageObject lastMessageObject;
  private ImageView playButton;
  private TextView titleTextView;
  private float topPadding;
  private boolean visible;
  private float yPosition;

  public FragmentContextView(Context paramContext, BaseFragment paramBaseFragment)
  {
    super(paramContext);
    this.fragment = paramBaseFragment;
    this.visible = true;
    ((ViewGroup)this.fragment.getFragmentView()).setClipToPadding(false);
    setTag(Integer.valueOf(1));
    this.frameLayout = new FrameLayout(paramContext);
    addView(this.frameLayout, LayoutHelper.createFrame(-1, 36.0F, 51, 0.0F, 0.0F, 0.0F, 0.0F));
    paramBaseFragment = new View(paramContext);
    paramBaseFragment.setBackgroundResource(2130837728);
    addView(paramBaseFragment, LayoutHelper.createFrame(-1, 3.0F, 51, 0.0F, 36.0F, 0.0F, 0.0F));
    this.playButton = new ImageView(paramContext);
    this.playButton.setScaleType(ImageView.ScaleType.CENTER);
    this.playButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerPlayPause"), PorterDuff.Mode.MULTIPLY));
    addView(this.playButton, LayoutHelper.createFrame(36, 36.0F, 51, 0.0F, 0.0F, 0.0F, 0.0F));
    this.playButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        if (MediaController.getInstance().isAudioPaused())
        {
          MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingMessageObject());
          return;
        }
        MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingMessageObject());
      }
    });
    this.titleTextView = new TextView(paramContext);
    this.titleTextView.setMaxLines(1);
    this.titleTextView.setLines(1);
    this.titleTextView.setSingleLine(true);
    this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.titleTextView.setTextSize(1, 15.0F);
    this.titleTextView.setGravity(19);
    addView(this.titleTextView, LayoutHelper.createFrame(-1, 36.0F, 51, 35.0F, 0.0F, 36.0F, 0.0F));
    this.closeButton = new ImageView(paramContext);
    this.closeButton.setImageResource(2130837923);
    this.closeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerClose"), PorterDuff.Mode.MULTIPLY));
    this.closeButton.setScaleType(ImageView.ScaleType.CENTER);
    addView(this.closeButton, LayoutHelper.createFrame(36, 36, 53));
    this.closeButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        MediaController.getInstance().cleanupPlayer(true, true);
      }
    });
    setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        if (FragmentContextView.this.currentStyle == 0)
        {
          paramView = MediaController.getInstance().getPlayingMessageObject();
          if ((paramView != null) && (paramView.isMusic()) && (FragmentContextView.this.fragment != null))
            FragmentContextView.this.fragment.presentFragment(new AudioPlayerActivity());
        }
        do
          return;
        while (FragmentContextView.this.currentStyle != 1);
        paramView = new Intent(FragmentContextView.this.getContext(), VoIPActivity.class);
        paramView.addFlags(805306368);
        FragmentContextView.this.getContext().startActivity(paramView);
      }
    });
  }

  private void checkCall(boolean paramBoolean)
  {
    View localView = this.fragment.getFragmentView();
    boolean bool = paramBoolean;
    if (!paramBoolean)
    {
      bool = paramBoolean;
      if (localView != null)
        if (localView.getParent() != null)
        {
          bool = paramBoolean;
          if (((View)localView.getParent()).getVisibility() == 0);
        }
        else
        {
          bool = true;
        }
    }
    int i;
    if ((VoIPService.getSharedInstance() != null) && (VoIPService.getSharedInstance().getCallState() != 10))
    {
      i = 1;
      if (i != 0)
        break label226;
      if (this.visible)
      {
        this.visible = false;
        if (!bool)
          break label113;
        if (getVisibility() != 8)
          setVisibility(8);
        setTopPadding(0.0F);
      }
    }
    label113: 
    do
    {
      return;
      i = 0;
      break;
      if (this.animatorSet != null)
      {
        this.animatorSet.cancel();
        this.animatorSet = null;
      }
      this.animatorSet = new AnimatorSet();
      this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { -AndroidUtilities.dp2(36.0F) }), ObjectAnimator.ofFloat(this, "topPadding", new float[] { 0.0F }) });
      this.animatorSet.setDuration(200L);
      this.animatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((FragmentContextView.this.animatorSet != null) && (FragmentContextView.this.animatorSet.equals(paramAnimator)))
          {
            FragmentContextView.this.setVisibility(8);
            FragmentContextView.access$202(FragmentContextView.this, null);
          }
        }
      });
      this.animatorSet.start();
      return;
      updateStyle(1);
      if ((!bool) || (this.topPadding != 0.0F))
        continue;
      setTopPadding(AndroidUtilities.dp2(36.0F));
      setTranslationY(0.0F);
      this.yPosition = 0.0F;
    }
    while (this.visible);
    label226: if (!bool)
    {
      if (this.animatorSet != null)
      {
        this.animatorSet.cancel();
        this.animatorSet = null;
      }
      this.animatorSet = new AnimatorSet();
      this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { -AndroidUtilities.dp2(36.0F), 0.0F }), ObjectAnimator.ofFloat(this, "topPadding", new float[] { AndroidUtilities.dp2(36.0F) }) });
      this.animatorSet.setDuration(200L);
      this.animatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((FragmentContextView.this.animatorSet != null) && (FragmentContextView.this.animatorSet.equals(paramAnimator)))
            FragmentContextView.access$202(FragmentContextView.this, null);
        }
      });
      this.animatorSet.start();
    }
    this.visible = true;
    setVisibility(0);
  }

  private void checkPlayer(boolean paramBoolean)
  {
    MessageObject localMessageObject = MediaController.getInstance().getPlayingMessageObject();
    Object localObject = this.fragment.getFragmentView();
    boolean bool = paramBoolean;
    if (!paramBoolean)
    {
      bool = paramBoolean;
      if (localObject != null)
        if (((View)localObject).getParent() != null)
        {
          bool = paramBoolean;
          if (((View)((View)localObject).getParent()).getVisibility() == 0);
        }
        else
        {
          bool = true;
        }
    }
    if ((localMessageObject == null) || (localMessageObject.getId() == 0))
    {
      this.lastMessageObject = null;
      if (this.visible)
      {
        this.visible = false;
        if (bool)
        {
          if (getVisibility() != 8)
            setVisibility(8);
          setTopPadding(0.0F);
        }
      }
      else
      {
        return;
      }
      if (this.animatorSet != null)
      {
        this.animatorSet.cancel();
        this.animatorSet = null;
      }
      this.animatorSet = new AnimatorSet();
      this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { -AndroidUtilities.dp2(36.0F) }), ObjectAnimator.ofFloat(this, "topPadding", new float[] { 0.0F }) });
      this.animatorSet.setDuration(200L);
      this.animatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((FragmentContextView.this.animatorSet != null) && (FragmentContextView.this.animatorSet.equals(paramAnimator)))
          {
            FragmentContextView.this.setVisibility(8);
            FragmentContextView.access$202(FragmentContextView.this, null);
          }
        }
      });
      this.animatorSet.start();
      return;
    }
    int i = this.currentStyle;
    updateStyle(0);
    if ((bool) && (this.topPadding == 0.0F))
    {
      setTopPadding(AndroidUtilities.dp2(36.0F));
      setTranslationY(0.0F);
      this.yPosition = 0.0F;
    }
    if (!this.visible)
    {
      if (!bool)
      {
        if (this.animatorSet != null)
        {
          this.animatorSet.cancel();
          this.animatorSet = null;
        }
        this.animatorSet = new AnimatorSet();
        this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { -AndroidUtilities.dp2(36.0F), 0.0F }), ObjectAnimator.ofFloat(this, "topPadding", new float[] { AndroidUtilities.dp2(36.0F) }) });
        this.animatorSet.setDuration(200L);
        this.animatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            if ((FragmentContextView.this.animatorSet != null) && (FragmentContextView.this.animatorSet.equals(paramAnimator)))
              FragmentContextView.access$202(FragmentContextView.this, null);
          }
        });
        this.animatorSet.start();
      }
      this.visible = true;
      setVisibility(0);
    }
    if (MediaController.getInstance().isAudioPaused())
    {
      this.playButton.setImageResource(2130837925);
      label428: if ((this.lastMessageObject == localMessageObject) && (i == 0))
        break label559;
      this.lastMessageObject = localMessageObject;
      if (!this.lastMessageObject.isVoice())
        break label561;
      localObject = new SpannableStringBuilder(String.format("%s %s", new Object[] { localMessageObject.getMusicAuthor(), localMessageObject.getMusicTitle() }));
      this.titleTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
    }
    while (true)
    {
      ((SpannableStringBuilder)localObject).setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), 0, localMessageObject.getMusicAuthor().length(), 18);
      this.titleTextView.setText((CharSequence)localObject);
      return;
      this.playButton.setImageResource(2130837924);
      break label428;
      label559: break;
      label561: localObject = new SpannableStringBuilder(String.format("%s - %s", new Object[] { localMessageObject.getMusicAuthor(), localMessageObject.getMusicTitle() }));
      this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
    }
  }

  private void updateStyle(int paramInt)
  {
    if (this.currentStyle == paramInt);
    do
    {
      return;
      this.currentStyle = paramInt;
      if (paramInt != 0)
        continue;
      this.frameLayout.setBackgroundColor(Theme.getColor("inappPlayerBackground"));
      this.titleTextView.setTextColor(Theme.getColor("inappPlayerTitle"));
      this.closeButton.setVisibility(0);
      this.playButton.setVisibility(0);
      this.titleTextView.setTypeface(Typeface.DEFAULT);
      this.titleTextView.setTextSize(1, 15.0F);
      this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0F, 51, 35.0F, 0.0F, 36.0F, 0.0F));
      return;
    }
    while (paramInt != 1);
    this.titleTextView.setText(LocaleController.getString("ReturnToCall", 2131166360));
    this.frameLayout.setBackgroundColor(Theme.getColor("returnToCallBackground"));
    this.titleTextView.setTextColor(Theme.getColor("returnToCallText"));
    this.closeButton.setVisibility(8);
    this.playButton.setVisibility(8);
    this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.titleTextView.setTextSize(1, 14.0F);
    this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0F, 17, 0.0F, 0.0F, 0.0F, 2.0F));
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if ((paramInt == NotificationCenter.audioDidStarted) || (paramInt == NotificationCenter.audioPlayStateChanged) || (paramInt == NotificationCenter.audioDidReset) || (paramInt == NotificationCenter.didEndedCall))
    {
      checkPlayer(false);
      return;
    }
    if (paramInt == NotificationCenter.didStartedCall)
    {
      checkCall(false);
      return;
    }
    checkPlayer(false);
  }

  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    int i = paramCanvas.save();
    if (this.yPosition < 0.0F)
      paramCanvas.clipRect(0, (int)(-this.yPosition), paramView.getMeasuredWidth(), AndroidUtilities.dp2(39.0F));
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    paramCanvas.restoreToCount(i);
    return bool;
  }

  public float getTopPadding()
  {
    return this.topPadding;
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didStartedCall);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didEndedCall);
    if ((VoIPService.getSharedInstance() != null) && (VoIPService.getSharedInstance().getCallState() != 10));
    for (int i = 1; i != 0; i = 0)
    {
      checkCall(true);
      return;
    }
    checkPlayer(true);
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.topPadding = 0.0F;
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didStartedCall);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didEndedCall);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, AndroidUtilities.dp2(39.0F));
  }

  public void setTopPadding(float paramFloat)
  {
    this.topPadding = paramFloat;
    if (this.fragment != null)
    {
      View localView = this.fragment.getFragmentView();
      if (localView != null)
        localView.setPadding(0, (int)this.topPadding, 0, 0);
    }
  }

  public void setTranslationY(float paramFloat)
  {
    super.setTranslationY(paramFloat);
    this.yPosition = paramFloat;
    invalidate();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.FragmentContextView
 * JD-Core Version:    0.6.0
 */