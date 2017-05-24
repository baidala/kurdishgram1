package org.vidogram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.y;
import android.support.v4.view.z;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.Components.LayoutHelper;

public class BottomSheet extends Dialog
{
  protected static int backgroundPaddingLeft;
  protected static int backgroundPaddingTop;
  private AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
  private boolean allowCustomAnimation = true;
  private boolean allowDrawContent = true;
  private boolean applyBottomPadding = true;
  private boolean applyTopPadding = true;
  protected ColorDrawable backDrawable = new ColorDrawable(-16777216);
  protected ContainerView container;
  protected ViewGroup containerView;
  protected AnimatorSet currentSheetAnimation;
  private View customView;
  private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
  private BottomSheetDelegateInterface delegate;
  private boolean dismissed;
  private boolean focusable;
  protected boolean fullWidth;
  private int[] itemIcons;
  private ArrayList<BottomSheetCell> itemViews = new ArrayList();
  private CharSequence[] items;
  private WindowInsets lastInsets;
  private int layoutCount;
  private DialogInterface.OnClickListener onClickListener;
  private Drawable shadowDrawable;
  private boolean showWithoutAnimation;
  private Runnable startAnimationRunnable;
  private int tag;
  private CharSequence title;
  private int touchSlop;
  private boolean useFastDismiss;

  public BottomSheet(Context paramContext, boolean paramBoolean)
  {
    super(paramContext, 2131361953);
    if (Build.VERSION.SDK_INT >= 21)
      getWindow().addFlags(-2147417856);
    this.touchSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
    Rect localRect = new Rect();
    this.shadowDrawable = paramContext.getResources().getDrawable(2130838062).mutate();
    this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
    this.shadowDrawable.getPadding(localRect);
    backgroundPaddingLeft = localRect.left;
    backgroundPaddingTop = localRect.top;
    this.container = new ContainerView(getContext())
    {
      public boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
      {
        try
        {
          if (BottomSheet.this.allowDrawContent)
          {
            boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
            if (bool)
              return true;
          }
          return false;
        }
        catch (Exception paramCanvas)
        {
          FileLog.e(paramCanvas);
        }
        return true;
      }
    };
    this.container.setBackgroundDrawable(this.backDrawable);
    this.focusable = paramBoolean;
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.container.setFitsSystemWindows(true);
      this.container.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
      {
        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View paramView, WindowInsets paramWindowInsets)
        {
          BottomSheet.access$502(BottomSheet.this, paramWindowInsets);
          paramView.requestLayout();
          return paramWindowInsets.consumeSystemWindowInsets();
        }
      });
      this.container.setSystemUiVisibility(1280);
    }
    this.backDrawable.setAlpha(0);
  }

  private void cancelSheetAnimation()
  {
    if (this.currentSheetAnimation != null)
    {
      this.currentSheetAnimation.cancel();
      this.currentSheetAnimation = null;
    }
  }

  private void startOpenAnimation()
  {
    if (this.dismissed);
    do
    {
      return;
      this.containerView.setVisibility(0);
    }
    while (onCustomOpenAnimation());
    if (Build.VERSION.SDK_INT >= 20)
      this.container.setLayerType(2, null);
    this.containerView.setTranslationY(this.containerView.getMeasuredHeight());
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.containerView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[] { 51 }) });
    localAnimatorSet.setDuration(200L);
    localAnimatorSet.setStartDelay(20L);
    localAnimatorSet.setInterpolator(new DecelerateInterpolator());
    localAnimatorSet.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationCancel(Animator paramAnimator)
      {
        if ((BottomSheet.this.currentSheetAnimation != null) && (BottomSheet.this.currentSheetAnimation.equals(paramAnimator)))
          BottomSheet.this.currentSheetAnimation = null;
      }

      public void onAnimationEnd(Animator paramAnimator)
      {
        if ((BottomSheet.this.currentSheetAnimation != null) && (BottomSheet.this.currentSheetAnimation.equals(paramAnimator)))
        {
          BottomSheet.this.currentSheetAnimation = null;
          if (BottomSheet.this.delegate != null)
            BottomSheet.this.delegate.onOpenAnimationEnd();
          BottomSheet.this.container.setLayerType(0, null);
        }
      }
    });
    localAnimatorSet.start();
    this.currentSheetAnimation = localAnimatorSet;
  }

  protected boolean canDismissWithSwipe()
  {
    return true;
  }

  protected boolean canDismissWithTouchOutside()
  {
    return true;
  }

  public void dismiss()
  {
    if ((this.delegate != null) && (!this.delegate.canDismiss()));
    do
    {
      do
        return;
      while (this.dismissed);
      this.dismissed = true;
      cancelSheetAnimation();
    }
    while ((this.allowCustomAnimation) && (onCustomCloseAnimation()));
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.containerView, "translationY", new float[] { this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0F) }), ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[] { 0 }) });
    if (this.useFastDismiss)
    {
      int i = this.containerView.getMeasuredHeight();
      localAnimatorSet.setDuration(Math.max(60, (int)(180.0F * (i - this.containerView.getTranslationY()) / i)));
      this.useFastDismiss = false;
    }
    while (true)
    {
      localAnimatorSet.setInterpolator(new AccelerateInterpolator());
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnimator)
        {
          if ((BottomSheet.this.currentSheetAnimation != null) && (BottomSheet.this.currentSheetAnimation.equals(paramAnimator)))
            BottomSheet.this.currentSheetAnimation = null;
        }

        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((BottomSheet.this.currentSheetAnimation != null) && (BottomSheet.this.currentSheetAnimation.equals(paramAnimator)))
          {
            BottomSheet.this.currentSheetAnimation = null;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                try
                {
                  BottomSheet.this.dismissInternal();
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
      });
      localAnimatorSet.start();
      this.currentSheetAnimation = localAnimatorSet;
      return;
      localAnimatorSet.setDuration(180L);
    }
  }

  public void dismissInternal()
  {
    try
    {
      super.dismiss();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public void dismissWithButtonClick(int paramInt)
  {
    if (this.dismissed)
      return;
    this.dismissed = true;
    cancelSheetAnimation();
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.containerView, "translationY", new float[] { this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0F) }), ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[] { 0 }) });
    localAnimatorSet.setDuration(180L);
    localAnimatorSet.setInterpolator(new AccelerateInterpolator());
    localAnimatorSet.addListener(new AnimatorListenerAdapter(paramInt)
    {
      public void onAnimationCancel(Animator paramAnimator)
      {
        if ((BottomSheet.this.currentSheetAnimation != null) && (BottomSheet.this.currentSheetAnimation.equals(paramAnimator)))
          BottomSheet.this.currentSheetAnimation = null;
      }

      public void onAnimationEnd(Animator paramAnimator)
      {
        if ((BottomSheet.this.currentSheetAnimation != null) && (BottomSheet.this.currentSheetAnimation.equals(paramAnimator)))
        {
          BottomSheet.this.currentSheetAnimation = null;
          if (BottomSheet.this.onClickListener != null)
            BottomSheet.this.onClickListener.onClick(BottomSheet.this, this.val$item);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              try
              {
                BottomSheet.this.dismiss();
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
    });
    localAnimatorSet.start();
    this.currentSheetAnimation = localAnimatorSet;
  }

  public FrameLayout getContainer()
  {
    return this.container;
  }

  protected int getLeftInset()
  {
    if ((this.lastInsets != null) && (Build.VERSION.SDK_INT >= 21))
      return this.lastInsets.getSystemWindowInsetLeft();
    return 0;
  }

  public ViewGroup getSheetContainer()
  {
    return this.containerView;
  }

  public int getTag()
  {
    return this.tag;
  }

  public boolean isDismissed()
  {
    return this.dismissed;
  }

  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
  }

  public void onContainerDraw(Canvas paramCanvas)
  {
  }

  protected boolean onContainerTouchEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }

  protected void onContainerTranslationYChanged(float paramFloat)
  {
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getWindow();
    paramBundle.setWindowAnimations(2131361930);
    setContentView(this.container, new ViewGroup.LayoutParams(-1, -1));
    Object localObject;
    int m;
    int i;
    int k;
    if (this.containerView == null)
    {
      this.containerView = new FrameLayout(getContext())
      {
        public boolean hasOverlappingRendering()
        {
          return false;
        }

        public void setTranslationY(float paramFloat)
        {
          super.setTranslationY(paramFloat);
          BottomSheet.this.onContainerTranslationYChanged(paramFloat);
        }
      };
      this.containerView.setBackgroundDrawable(this.shadowDrawable);
      localObject = this.containerView;
      m = backgroundPaddingLeft;
      if (this.applyTopPadding)
      {
        i = AndroidUtilities.dp(8.0F);
        int n = backgroundPaddingTop;
        int i1 = backgroundPaddingLeft;
        if (!this.applyBottomPadding)
          break label307;
        k = AndroidUtilities.dp(8.0F);
        label117: ((ViewGroup)localObject).setPadding(m, i + n - 1, i1, k);
      }
    }
    else
    {
      if (Build.VERSION.SDK_INT >= 21)
        this.containerView.setFitsSystemWindows(true);
      this.containerView.setVisibility(4);
      this.container.addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
      if (this.customView == null)
        break label312;
      if (this.customView.getParent() != null)
        ((ViewGroup)this.customView.getParent()).removeView(this.customView);
      this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2, 51));
    }
    label307: label312: label596: 
    while (true)
    {
      localObject = paramBundle.getAttributes();
      ((WindowManager.LayoutParams)localObject).width = -1;
      ((WindowManager.LayoutParams)localObject).gravity = 51;
      ((WindowManager.LayoutParams)localObject).dimAmount = 0.0F;
      ((WindowManager.LayoutParams)localObject).flags &= -3;
      if (!this.focusable)
        ((WindowManager.LayoutParams)localObject).flags |= 131072;
      ((WindowManager.LayoutParams)localObject).height = -1;
      paramBundle.setAttributes((WindowManager.LayoutParams)localObject);
      return;
      i = 0;
      break;
      k = 0;
      break label117;
      if (this.title != null)
      {
        localObject = new TextView(getContext());
        ((TextView)localObject).setLines(1);
        ((TextView)localObject).setSingleLine(true);
        ((TextView)localObject).setText(this.title);
        ((TextView)localObject).setTextColor(Theme.getColor("dialogTextGray2"));
        ((TextView)localObject).setTextSize(1, 16.0F);
        ((TextView)localObject).setEllipsize(TextUtils.TruncateAt.MIDDLE);
        ((TextView)localObject).setPadding(AndroidUtilities.dp(16.0F), 0, AndroidUtilities.dp(16.0F), AndroidUtilities.dp(8.0F));
        ((TextView)localObject).setGravity(16);
        this.containerView.addView((View)localObject, LayoutHelper.createFrame(-1, 48.0F));
        ((TextView)localObject).setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
          {
            return true;
          }
        });
      }
      int j;
      for (i = 48; ; j = 0)
      {
        if (this.items == null)
          break label596;
        k = 0;
        while (true)
          if (k < this.items.length)
          {
            if (this.items[k] == null)
            {
              k += 1;
              continue;
            }
          }
          else
            break;
        localObject = new BottomSheetCell(getContext(), 0);
        CharSequence localCharSequence = this.items[k];
        if (this.itemIcons != null);
        for (m = this.itemIcons[k]; ; m = 0)
        {
          ((BottomSheetCell)localObject).setTextAndIcon(localCharSequence, m);
          this.containerView.addView((View)localObject, LayoutHelper.createFrame(-1, 48.0F, 51, 0.0F, i, 0.0F, 0.0F));
          i += 48;
          ((BottomSheetCell)localObject).setTag(Integer.valueOf(k));
          ((BottomSheetCell)localObject).setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramView)
            {
              BottomSheet.this.dismissWithButtonClick(((Integer)paramView.getTag()).intValue());
            }
          });
          this.itemViews.add(localObject);
          break;
        }
      }
    }
  }

  protected boolean onCustomCloseAnimation()
  {
    return false;
  }

  protected boolean onCustomLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return false;
  }

  protected boolean onCustomMeasure(View paramView, int paramInt1, int paramInt2)
  {
    return false;
  }

  protected boolean onCustomOpenAnimation()
  {
    return false;
  }

  public void setAllowDrawContent(boolean paramBoolean)
  {
    ContainerView localContainerView;
    if (this.allowDrawContent != paramBoolean)
    {
      this.allowDrawContent = paramBoolean;
      localContainerView = this.container;
      if (!this.allowDrawContent)
        break label43;
    }
    label43: for (ColorDrawable localColorDrawable = this.backDrawable; ; localColorDrawable = null)
    {
      localContainerView.setBackgroundDrawable(localColorDrawable);
      this.container.invalidate();
      return;
    }
  }

  public void setApplyBottomPadding(boolean paramBoolean)
  {
    this.applyBottomPadding = paramBoolean;
  }

  public void setApplyTopPadding(boolean paramBoolean)
  {
    this.applyTopPadding = paramBoolean;
  }

  public void setCustomView(View paramView)
  {
    this.customView = paramView;
  }

  public void setDelegate(BottomSheetDelegateInterface paramBottomSheetDelegateInterface)
  {
    this.delegate = paramBottomSheetDelegateInterface;
  }

  public void setItemText(int paramInt, CharSequence paramCharSequence)
  {
    if ((paramInt < 0) || (paramInt >= this.itemViews.size()))
      return;
    ((BottomSheetCell)this.itemViews.get(paramInt)).textView.setText(paramCharSequence);
  }

  public void setShowWithoutAnimation(boolean paramBoolean)
  {
    this.showWithoutAnimation = paramBoolean;
  }

  public void setTitle(CharSequence paramCharSequence)
  {
    this.title = paramCharSequence;
  }

  public void show()
  {
    super.show();
    if (this.focusable)
      getWindow().setSoftInputMode(16);
    this.dismissed = false;
    cancelSheetAnimation();
    this.containerView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x + backgroundPaddingLeft * 2, -2147483648), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, -2147483648));
    if (this.showWithoutAnimation)
    {
      this.backDrawable.setAlpha(51);
      this.containerView.setTranslationY(0.0F);
      return;
    }
    this.backDrawable.setAlpha(0);
    if (Build.VERSION.SDK_INT >= 18)
    {
      this.layoutCount = 2;
      this.containerView.setTranslationY(this.containerView.getMeasuredHeight());
      6 local6 = new Runnable()
      {
        public void run()
        {
          if ((BottomSheet.this.startAnimationRunnable != this) || (BottomSheet.this.dismissed))
            return;
          BottomSheet.access$702(BottomSheet.this, null);
          BottomSheet.this.startOpenAnimation();
        }
      };
      this.startAnimationRunnable = local6;
      AndroidUtilities.runOnUIThread(local6, 150L);
      return;
    }
    startOpenAnimation();
  }

  public static class BottomSheetCell extends FrameLayout
  {
    private ImageView imageView;
    private TextView textView;

    public BottomSheetCell(Context paramContext, int paramInt)
    {
      super();
      setBackgroundDrawable(Theme.getSelectorDrawable(false));
      setPadding(AndroidUtilities.dp(16.0F), 0, AndroidUtilities.dp(16.0F), 0);
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
      ImageView localImageView = this.imageView;
      int i;
      if (LocaleController.isRTL)
      {
        i = 5;
        addView(localImageView, LayoutHelper.createFrame(24, 24, i | 0x10));
        this.textView = new TextView(paramContext);
        this.textView.setLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(1);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        if (paramInt != 0)
          break label217;
        this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.textView.setTextSize(1, 16.0F);
        paramContext = this.textView;
        paramInt = j;
        if (LocaleController.isRTL)
          paramInt = 5;
        addView(paramContext, LayoutHelper.createFrame(-2, -2, paramInt | 0x10));
      }
      label217: 
      do
      {
        return;
        i = 3;
        break;
      }
      while (paramInt != 1);
      this.textView.setGravity(17);
      this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
      this.textView.setTextSize(1, 14.0F);
      this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      addView(this.textView, LayoutHelper.createFrame(-1, -1.0F));
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0F), 1073741824));
    }

    public void setGravity(int paramInt)
    {
      this.textView.setGravity(paramInt);
    }

    public void setTextAndIcon(CharSequence paramCharSequence, int paramInt)
    {
      this.textView.setText(paramCharSequence);
      if (paramInt != 0)
      {
        this.imageView.setImageResource(paramInt);
        this.imageView.setVisibility(0);
        paramCharSequence = this.textView;
        if (LocaleController.isRTL)
        {
          paramInt = 0;
          if (!LocaleController.isRTL)
            break label71;
        }
        label71: for (int i = AndroidUtilities.dp(56.0F); ; i = 0)
        {
          paramCharSequence.setPadding(paramInt, 0, i, 0);
          return;
          paramInt = AndroidUtilities.dp(56.0F);
          break;
        }
      }
      this.imageView.setVisibility(4);
      this.textView.setPadding(0, 0, 0, 0);
    }

    public void setTextColor(int paramInt)
    {
      this.textView.setTextColor(paramInt);
    }
  }

  public static class BottomSheetDelegate
    implements BottomSheet.BottomSheetDelegateInterface
  {
    public boolean canDismiss()
    {
      return true;
    }

    public void onOpenAnimationEnd()
    {
    }

    public void onOpenAnimationStart()
    {
    }
  }

  public static abstract interface BottomSheetDelegateInterface
  {
    public abstract boolean canDismiss();

    public abstract void onOpenAnimationEnd();

    public abstract void onOpenAnimationStart();
  }

  public static class Builder
  {
    private BottomSheet bottomSheet;

    public Builder(Context paramContext)
    {
      this.bottomSheet = new BottomSheet(paramContext, false);
    }

    public Builder(Context paramContext, boolean paramBoolean)
    {
      this.bottomSheet = new BottomSheet(paramContext, paramBoolean);
    }

    public BottomSheet create()
    {
      return this.bottomSheet;
    }

    public Builder setApplyBottomPadding(boolean paramBoolean)
    {
      BottomSheet.access$2002(this.bottomSheet, paramBoolean);
      return this;
    }

    public Builder setApplyTopPadding(boolean paramBoolean)
    {
      BottomSheet.access$1902(this.bottomSheet, paramBoolean);
      return this;
    }

    public Builder setCustomView(View paramView)
    {
      BottomSheet.access$1602(this.bottomSheet, paramView);
      return this;
    }

    public Builder setDelegate(BottomSheet.BottomSheetDelegate paramBottomSheetDelegate)
    {
      this.bottomSheet.setDelegate(paramBottomSheetDelegate);
      return this;
    }

    public Builder setItems(CharSequence[] paramArrayOfCharSequence, DialogInterface.OnClickListener paramOnClickListener)
    {
      BottomSheet.access$1402(this.bottomSheet, paramArrayOfCharSequence);
      BottomSheet.access$1202(this.bottomSheet, paramOnClickListener);
      return this;
    }

    public Builder setItems(CharSequence[] paramArrayOfCharSequence, int[] paramArrayOfInt, DialogInterface.OnClickListener paramOnClickListener)
    {
      BottomSheet.access$1402(this.bottomSheet, paramArrayOfCharSequence);
      BottomSheet.access$1502(this.bottomSheet, paramArrayOfInt);
      BottomSheet.access$1202(this.bottomSheet, paramOnClickListener);
      return this;
    }

    public Builder setTag(int paramInt)
    {
      BottomSheet.access$1802(this.bottomSheet, paramInt);
      return this;
    }

    public Builder setTitle(CharSequence paramCharSequence)
    {
      BottomSheet.access$1702(this.bottomSheet, paramCharSequence);
      return this;
    }

    public BottomSheet setUseFullWidth(boolean paramBoolean)
    {
      this.bottomSheet.fullWidth = paramBoolean;
      return this.bottomSheet;
    }

    public BottomSheet show()
    {
      this.bottomSheet.show();
      return this.bottomSheet;
    }
  }

  protected class ContainerView extends FrameLayout
    implements y
  {
    private AnimatorSet currentAnimation = null;
    private boolean maybeStartTracking = false;
    private z nestedScrollingParentHelper = new z(this);
    private boolean startedTracking = false;
    private int startedTrackingPointerId = -1;
    private int startedTrackingX;
    private int startedTrackingY;
    private VelocityTracker velocityTracker = null;

    public ContainerView(Context arg2)
    {
      super();
    }

    private void cancelCurrentAnimation()
    {
      if (this.currentAnimation != null)
      {
        this.currentAnimation.cancel();
        this.currentAnimation = null;
      }
    }

    private void checkDismiss(float paramFloat1, float paramFloat2)
    {
      float f = BottomSheet.this.containerView.getTranslationY();
      if (((f < AndroidUtilities.getPixelsInCM(0.8F, false)) && ((paramFloat2 < 3500.0F) || (Math.abs(paramFloat2) < Math.abs(paramFloat1)))) || ((paramFloat2 < 0.0F) && (Math.abs(paramFloat2) >= 3500.0F)));
      for (int i = 1; i == 0; i = 0)
      {
        boolean bool = BottomSheet.this.allowCustomAnimation;
        BottomSheet.access$102(BottomSheet.this, false);
        BottomSheet.access$202(BottomSheet.this, true);
        BottomSheet.this.dismiss();
        BottomSheet.access$102(BottomSheet.this, bool);
        return;
      }
      this.currentAnimation = new AnimatorSet();
      this.currentAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(BottomSheet.this.containerView, "translationY", new float[] { 0.0F }) });
      this.currentAnimation.setDuration((int)(150.0F * (f / AndroidUtilities.getPixelsInCM(0.8F, false))));
      this.currentAnimation.setInterpolator(new DecelerateInterpolator());
      this.currentAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((BottomSheet.ContainerView.this.currentAnimation != null) && (BottomSheet.ContainerView.this.currentAnimation.equals(paramAnimator)))
            BottomSheet.ContainerView.access$302(BottomSheet.ContainerView.this, null);
        }
      });
      this.currentAnimation.start();
    }

    public int getNestedScrollAxes()
    {
      return this.nestedScrollingParentHelper.a();
    }

    public boolean hasOverlappingRendering()
    {
      return false;
    }

    protected void onDraw(Canvas paramCanvas)
    {
      super.onDraw(paramCanvas);
      BottomSheet.this.onContainerDraw(paramCanvas);
    }

    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
    {
      if (BottomSheet.this.canDismissWithSwipe())
        return onTouchEvent(paramMotionEvent);
      return super.onInterceptTouchEvent(paramMotionEvent);
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      BottomSheet.access$610(BottomSheet.this);
      int i = paramInt1;
      int j = paramInt3;
      if (BottomSheet.this.containerView != null)
      {
        i = paramInt1;
        j = paramInt3;
        if (BottomSheet.this.lastInsets != null)
        {
          i = paramInt1;
          j = paramInt3;
          if (Build.VERSION.SDK_INT >= 21)
          {
            i = paramInt1 + BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
            j = paramInt3 - BottomSheet.this.lastInsets.getSystemWindowInsetRight();
          }
        }
        k = paramInt4 - paramInt2 - BottomSheet.this.containerView.getMeasuredHeight();
        paramInt3 = (j - i - BottomSheet.this.containerView.getMeasuredWidth()) / 2;
        paramInt1 = paramInt3;
        if (BottomSheet.this.lastInsets != null)
        {
          paramInt1 = paramInt3;
          if (Build.VERSION.SDK_INT >= 21)
            paramInt1 = paramInt3 + BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
        }
        BottomSheet.this.containerView.layout(paramInt1, k, BottomSheet.this.containerView.getMeasuredWidth() + paramInt1, BottomSheet.this.containerView.getMeasuredHeight() + k);
      }
      int n = getChildCount();
      int k = 0;
      if (k < n)
      {
        View localView = getChildAt(k);
        if ((localView.getVisibility() == 8) || (localView == BottomSheet.this.containerView));
        do
        {
          k += 1;
          break;
        }
        while (BottomSheet.this.onCustomLayout(localView, i, paramInt2, j, paramInt4));
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
        int i1 = localView.getMeasuredWidth();
        int i2 = localView.getMeasuredHeight();
        paramInt1 = localLayoutParams.gravity;
        paramInt3 = paramInt1;
        if (paramInt1 == -1)
          paramInt3 = 51;
        switch (paramInt3 & 0x7 & 0x7)
        {
        default:
          paramInt1 = localLayoutParams.leftMargin;
          label354: switch (paramInt3 & 0x70)
          {
          default:
            paramInt3 = localLayoutParams.topMargin;
          case 48:
          case 16:
          case 80:
          }
        case 1:
        case 5:
        }
        while (true)
        {
          int m = paramInt1;
          if (BottomSheet.this.lastInsets != null)
          {
            m = paramInt1;
            if (Build.VERSION.SDK_INT >= 21)
              m = paramInt1 + BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
          }
          localView.layout(m, paramInt3, i1 + m, i2 + paramInt3);
          break;
          paramInt1 = (j - i - i1) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
          break label354;
          paramInt1 = j - i1 - localLayoutParams.rightMargin;
          break label354;
          paramInt3 = localLayoutParams.topMargin;
          continue;
          paramInt3 = (paramInt4 - paramInt2 - i2) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
          continue;
          paramInt3 = paramInt4 - paramInt2 - i2 - localLayoutParams.bottomMargin;
        }
      }
      if ((BottomSheet.this.layoutCount == 0) && (BottomSheet.this.startAnimationRunnable != null))
      {
        AndroidUtilities.cancelRunOnUIThread(BottomSheet.this.startAnimationRunnable);
        BottomSheet.this.startAnimationRunnable.run();
        BottomSheet.access$702(BottomSheet.this, null);
      }
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      paramInt1 = View.MeasureSpec.getSize(paramInt2);
      if ((BottomSheet.this.lastInsets != null) && (Build.VERSION.SDK_INT >= 21))
        paramInt1 -= BottomSheet.this.lastInsets.getSystemWindowInsetBottom();
      while (true)
      {
        setMeasuredDimension(i, paramInt1);
        if ((BottomSheet.this.lastInsets != null) && (Build.VERSION.SDK_INT >= 21));
        for (paramInt2 = i - (BottomSheet.this.lastInsets.getSystemWindowInsetRight() + BottomSheet.this.lastInsets.getSystemWindowInsetLeft()); ; paramInt2 = i)
        {
          label173: label181: View localView;
          if (paramInt2 < paramInt1)
          {
            i = 1;
            if (BottomSheet.this.containerView != null)
            {
              if (BottomSheet.this.fullWidth)
                break label281;
              if (!AndroidUtilities.isTablet())
                break label228;
              i = View.MeasureSpec.makeMeasureSpec((int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.8F) + BottomSheet.backgroundPaddingLeft * 2, 1073741824);
              BottomSheet.this.containerView.measure(i, View.MeasureSpec.makeMeasureSpec(paramInt1, -2147483648));
            }
            int j = getChildCount();
            i = 0;
            if (i >= j)
              break label353;
            localView = getChildAt(i);
            if ((localView.getVisibility() != 8) && (localView != BottomSheet.this.containerView))
              break label314;
          }
          while (true)
          {
            i += 1;
            break label181;
            i = 0;
            break;
            label228: if (i != 0);
            for (i = BottomSheet.backgroundPaddingLeft * 2 + paramInt2; ; i = (int)Math.max(paramInt2 * 0.8F, Math.min(AndroidUtilities.dp(480.0F), paramInt2)) + BottomSheet.backgroundPaddingLeft * 2)
            {
              i = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
              break;
            }
            label281: BottomSheet.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(BottomSheet.backgroundPaddingLeft * 2 + paramInt2, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt1, -2147483648));
            break label173;
            label314: if (BottomSheet.this.onCustomMeasure(localView, paramInt2, paramInt1))
              continue;
            measureChildWithMargins(localView, View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824), 0, View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), 0);
          }
          label353: return;
        }
      }
    }

    public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
    {
      return false;
    }

    public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2)
    {
      return false;
    }

    public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt)
    {
      float f1 = 0.0F;
      if (BottomSheet.this.dismissed);
      float f2;
      do
      {
        return;
        cancelCurrentAnimation();
        f2 = BottomSheet.this.containerView.getTranslationY();
      }
      while ((f2 <= 0.0F) || (paramInt2 <= 0));
      f2 -= paramInt2;
      paramArrayOfInt[1] = paramInt2;
      if (f2 < 0.0F)
        paramArrayOfInt[1] = (int)(paramArrayOfInt[1] + 0.0F);
      while (true)
      {
        BottomSheet.this.containerView.setTranslationY(f1);
        return;
        f1 = f2;
      }
    }

    public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      float f1 = 0.0F;
      if (BottomSheet.this.dismissed);
      do
      {
        return;
        cancelCurrentAnimation();
      }
      while (paramInt4 == 0);
      float f2 = BottomSheet.this.containerView.getTranslationY() - paramInt4;
      if (f2 < 0.0F);
      while (true)
      {
        BottomSheet.this.containerView.setTranslationY(f1);
        return;
        f1 = f2;
      }
    }

    public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt)
    {
      this.nestedScrollingParentHelper.a(paramView1, paramView2, paramInt);
      if (BottomSheet.this.dismissed)
        return;
      cancelCurrentAnimation();
    }

    public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt)
    {
      return (!BottomSheet.this.dismissed) && (paramInt == 2) && (!BottomSheet.this.canDismissWithSwipe());
    }

    public void onStopNestedScroll(View paramView)
    {
      this.nestedScrollingParentHelper.a(paramView);
      if (BottomSheet.this.dismissed)
        return;
      BottomSheet.this.containerView.getTranslationY();
      checkDismiss(0.0F, 0.0F);
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      float f1 = 0.0F;
      int i = 1;
      if (BottomSheet.this.dismissed)
        i = 0;
      do
        return i;
      while (BottomSheet.this.onContainerTouchEvent(paramMotionEvent));
      if ((BottomSheet.this.canDismissWithTouchOutside()) && (paramMotionEvent != null) && ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2)) && (!this.startedTracking) && (!this.maybeStartTracking))
      {
        this.startedTrackingX = (int)paramMotionEvent.getX();
        this.startedTrackingY = (int)paramMotionEvent.getY();
        if ((this.startedTrackingY < BottomSheet.this.containerView.getTop()) || (this.startedTrackingX < BottomSheet.this.containerView.getLeft()) || (this.startedTrackingX > BottomSheet.this.containerView.getRight()))
        {
          BottomSheet.this.dismiss();
          return true;
        }
        this.startedTrackingPointerId = paramMotionEvent.getPointerId(0);
        this.maybeStartTracking = true;
        cancelCurrentAnimation();
        if (this.velocityTracker != null)
          this.velocityTracker.clear();
      }
      label205: float f2;
      while (true)
      {
        if ((!this.startedTracking) && (BottomSheet.this.canDismissWithSwipe()))
          break label568;
        i = 1;
        return i;
        if ((paramMotionEvent == null) || (paramMotionEvent.getAction() != 2) || (paramMotionEvent.getPointerId(0) != this.startedTrackingPointerId))
          break;
        if (this.velocityTracker == null)
          this.velocityTracker = VelocityTracker.obtain();
        f2 = Math.abs((int)(paramMotionEvent.getX() - this.startedTrackingX));
        float f3 = (int)paramMotionEvent.getY() - this.startedTrackingY;
        this.velocityTracker.addMovement(paramMotionEvent);
        if ((this.maybeStartTracking) && (!this.startedTracking) && (f3 > 0.0F) && (f3 / 3.0F > Math.abs(f2)) && (Math.abs(f3) >= BottomSheet.this.touchSlop))
        {
          this.startedTrackingY = (int)paramMotionEvent.getY();
          this.maybeStartTracking = false;
          this.startedTracking = true;
          requestDisallowInterceptTouchEvent(true);
          continue;
        }
        if (!this.startedTracking)
          continue;
        f2 = BottomSheet.this.containerView.getTranslationY() + f3;
        if (f2 >= 0.0F)
          break label574;
      }
      while (true)
      {
        BottomSheet.this.containerView.setTranslationY(f1);
        this.startedTrackingY = (int)paramMotionEvent.getY();
        break;
        if ((paramMotionEvent != null) && ((paramMotionEvent == null) || (paramMotionEvent.getPointerId(0) != this.startedTrackingPointerId) || ((paramMotionEvent.getAction() != 3) && (paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 6))))
          break;
        if (this.velocityTracker == null)
          this.velocityTracker = VelocityTracker.obtain();
        this.velocityTracker.computeCurrentVelocity(1000);
        f1 = BottomSheet.this.containerView.getTranslationY();
        if ((this.startedTracking) || (f1 != 0.0F))
          checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
        for (this.startedTracking = false; ; this.startedTracking = false)
        {
          if (this.velocityTracker != null)
          {
            this.velocityTracker.recycle();
            this.velocityTracker = null;
          }
          this.startedTrackingPointerId = -1;
          break;
          this.maybeStartTracking = false;
        }
        label568: i = 0;
        break label205;
        label574: f1 = f2;
      }
    }

    public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
    {
      if ((this.maybeStartTracking) && (!this.startedTracking))
        onTouchEvent(null);
      super.requestDisallowInterceptTouchEvent(paramBoolean);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ActionBar.BottomSheet
 * JD-Core Version:    0.6.0
 */