package org.vidogram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.LineProgressView;
import org.vidogram.ui.Components.RadialProgressView;

public class AlertDialog extends Dialog
  implements Drawable.Callback
{
  private Rect backgroundPaddings = new Rect();
  private FrameLayout buttonsLayout;
  private ScrollView contentScrollView;
  private int currentProgress;
  private View customView;
  private int[] itemIcons;
  private CharSequence[] items;
  private int lastScreenWidth;
  private LineProgressView lineProgressView;
  private TextView lineProgressViewPercent;
  private CharSequence message;
  private TextView messageTextView;
  private DialogInterface.OnClickListener negativeButtonListener;
  private CharSequence negativeButtonText;
  private DialogInterface.OnClickListener neutralButtonListener;
  private CharSequence neutralButtonText;
  private DialogInterface.OnClickListener onClickListener;
  private DialogInterface.OnDismissListener onDismissListener;
  private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;
  private DialogInterface.OnClickListener positiveButtonListener;
  private CharSequence positiveButtonText;
  private FrameLayout progressViewContainer;
  private int progressViewStyle;
  private TextView progressViewTextView;
  private LinearLayout scrollContainer;
  private BitmapDrawable[] shadow = new BitmapDrawable[2];
  private AnimatorSet[] shadowAnimation = new AnimatorSet[2];
  private Drawable shadowDrawable;
  private boolean[] shadowVisibility = new boolean[2];
  private CharSequence title;
  private TextView titleTextView;

  public AlertDialog(Context paramContext, int paramInt)
  {
    super(paramContext, 2131361953);
    this.shadowDrawable = paramContext.getResources().getDrawable(2130838032).mutate();
    this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
    this.shadowDrawable.getPadding(this.backgroundPaddings);
    this.progressViewStyle = paramInt;
  }

  private boolean canTextInput(View paramView)
  {
    if (paramView.onCheckIsTextEditor())
      return true;
    if (!(paramView instanceof ViewGroup))
      return false;
    paramView = (ViewGroup)paramView;
    int i = paramView.getChildCount();
    while (i > 0)
    {
      int j = i - 1;
      i = j;
      if (canTextInput(paramView.getChildAt(j)))
        return true;
    }
    return false;
  }

  private void runShadowAnimation(int paramInt, boolean paramBoolean)
  {
    AnimatorSet localAnimatorSet;
    BitmapDrawable localBitmapDrawable;
    int i;
    if (((paramBoolean) && (this.shadowVisibility[paramInt] == 0)) || ((!paramBoolean) && (this.shadowVisibility[paramInt] != 0)))
    {
      this.shadowVisibility[paramInt] = paramBoolean;
      if (this.shadowAnimation[paramInt] != null)
        this.shadowAnimation[paramInt].cancel();
      this.shadowAnimation[paramInt] = new AnimatorSet();
      if (this.shadow[paramInt] != null)
      {
        localAnimatorSet = this.shadowAnimation[paramInt];
        localBitmapDrawable = this.shadow[paramInt];
        if (!paramBoolean)
          break label165;
        i = 255;
      }
    }
    while (true)
    {
      localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofInt(localBitmapDrawable, "alpha", new int[] { i }) });
      this.shadowAnimation[paramInt].setDuration(150L);
      this.shadowAnimation[paramInt].addListener(new AnimatorListenerAdapter(paramInt)
      {
        public void onAnimationCancel(Animator paramAnimator)
        {
          if ((AlertDialog.this.shadowAnimation[this.val$num] != null) && (AlertDialog.this.shadowAnimation[this.val$num].equals(paramAnimator)))
            AlertDialog.this.shadowAnimation[this.val$num] = null;
        }

        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((AlertDialog.this.shadowAnimation[this.val$num] != null) && (AlertDialog.this.shadowAnimation[this.val$num].equals(paramAnimator)))
            AlertDialog.this.shadowAnimation[this.val$num] = null;
        }
      });
      try
      {
        this.shadowAnimation[paramInt].start();
        return;
        label165: i = 0;
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    }
  }

  private void updateLineProgressTextView()
  {
    this.lineProgressViewPercent.setText(String.format("%d%%", new Object[] { Integer.valueOf(this.currentProgress) }));
  }

  public void dismiss()
  {
    super.dismiss();
  }

  public View getButton(int paramInt)
  {
    return this.buttonsLayout.findViewWithTag(Integer.valueOf(paramInt));
  }

  public void invalidateDrawable(Drawable paramDrawable)
  {
    this.contentScrollView.invalidate();
    this.scrollContainer.invalidate();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = new LinearLayout(getContext())
    {
      private boolean inLayout;

      public boolean hasOverlappingRendering()
      {
        return false;
      }

      protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        if (AlertDialog.this.contentScrollView != null)
        {
          if (AlertDialog.this.onScrollChangedListener == null)
          {
            AlertDialog.access$1202(AlertDialog.this, new ViewTreeObserver.OnScrollChangedListener()
            {
              public void onScrollChanged()
              {
                boolean bool2 = false;
                AlertDialog localAlertDialog = AlertDialog.this;
                if ((AlertDialog.this.titleTextView != null) && (AlertDialog.this.contentScrollView.getScrollY() > AlertDialog.this.scrollContainer.getTop()));
                for (boolean bool1 = true; ; bool1 = false)
                {
                  localAlertDialog.runShadowAnimation(0, bool1);
                  localAlertDialog = AlertDialog.this;
                  bool1 = bool2;
                  if (AlertDialog.this.buttonsLayout != null)
                  {
                    bool1 = bool2;
                    if (AlertDialog.this.contentScrollView.getScrollY() + AlertDialog.this.contentScrollView.getHeight() < AlertDialog.this.scrollContainer.getBottom())
                      bool1 = true;
                  }
                  localAlertDialog.runShadowAnimation(1, bool1);
                  AlertDialog.this.contentScrollView.invalidate();
                  return;
                }
              }
            });
            AlertDialog.this.contentScrollView.getViewTreeObserver().addOnScrollChangedListener(AlertDialog.this.onScrollChangedListener);
          }
          AlertDialog.this.onScrollChangedListener.onScrollChanged();
        }
      }

      protected void onMeasure(int paramInt1, int paramInt2)
      {
        this.inLayout = true;
        int k = View.MeasureSpec.getSize(paramInt1);
        int j = View.MeasureSpec.getSize(paramInt2) - getPaddingTop() - getPaddingBottom();
        int i = k - getPaddingLeft() - getPaddingRight();
        int n = View.MeasureSpec.makeMeasureSpec(i - AndroidUtilities.dp(48.0F), 1073741824);
        int m = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
        LinearLayout.LayoutParams localLayoutParams;
        if (AlertDialog.this.buttonsLayout != null)
        {
          int i1 = AlertDialog.this.buttonsLayout.getChildCount();
          paramInt1 = 0;
          while (paramInt1 < i1)
          {
            ((TextView)AlertDialog.this.buttonsLayout.getChildAt(paramInt1)).setMaxWidth(AndroidUtilities.dp((i - AndroidUtilities.dp(24.0F)) / 2));
            paramInt1 += 1;
          }
          AlertDialog.this.buttonsLayout.measure(m, paramInt2);
          localLayoutParams = (LinearLayout.LayoutParams)AlertDialog.this.buttonsLayout.getLayoutParams();
          paramInt1 = AlertDialog.this.buttonsLayout.getMeasuredHeight();
          i = localLayoutParams.bottomMargin;
        }
        for (paramInt1 = j - (localLayoutParams.topMargin + (paramInt1 + i)); ; paramInt1 = j)
        {
          i = paramInt1;
          if (AlertDialog.this.titleTextView != null)
          {
            AlertDialog.this.titleTextView.measure(n, paramInt2);
            localLayoutParams = (LinearLayout.LayoutParams)AlertDialog.this.titleTextView.getLayoutParams();
            paramInt2 = AlertDialog.this.titleTextView.getMeasuredHeight();
            i = localLayoutParams.bottomMargin;
            i = paramInt1 - (localLayoutParams.topMargin + (paramInt2 + i));
          }
          if (AlertDialog.this.progressViewStyle == 0)
          {
            localLayoutParams = (LinearLayout.LayoutParams)AlertDialog.this.contentScrollView.getLayoutParams();
            if (AlertDialog.this.customView != null)
              if ((AlertDialog.this.titleTextView == null) && (AlertDialog.this.messageTextView.getVisibility() == 8) && (AlertDialog.this.items == null))
              {
                paramInt1 = AndroidUtilities.dp(16.0F);
                localLayoutParams.topMargin = paramInt1;
                if (AlertDialog.this.buttonsLayout != null)
                  break label465;
                paramInt1 = AndroidUtilities.dp(8.0F);
                label354: localLayoutParams.bottomMargin = paramInt1;
              }
            label465: 
            do
            {
              paramInt1 = localLayoutParams.bottomMargin;
              paramInt1 = i - (localLayoutParams.topMargin + paramInt1);
              AlertDialog.this.contentScrollView.measure(m, View.MeasureSpec.makeMeasureSpec(paramInt1, -2147483648));
              paramInt2 = paramInt1 - AlertDialog.this.contentScrollView.getMeasuredHeight();
              setMeasuredDimension(k, j - paramInt2 + getPaddingTop() + getPaddingBottom());
              this.inLayout = false;
              if (AlertDialog.this.lastScreenWidth != AndroidUtilities.displaySize.x)
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    AlertDialog.access$1002(AlertDialog.this, AndroidUtilities.displaySize.x);
                    int j = AndroidUtilities.displaySize.x;
                    int k = AndroidUtilities.dp(56.0F);
                    int i;
                    if (AndroidUtilities.isTablet())
                      if (AndroidUtilities.isSmallTablet())
                        i = AndroidUtilities.dp(446.0F);
                    while (true)
                    {
                      Window localWindow = AlertDialog.this.getWindow();
                      WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
                      localLayoutParams.copyFrom(localWindow.getAttributes());
                      localLayoutParams.width = (Math.min(i, j - k) + AlertDialog.this.backgroundPaddings.left + AlertDialog.this.backgroundPaddings.right);
                      localWindow.setAttributes(localLayoutParams);
                      return;
                      i = AndroidUtilities.dp(496.0F);
                      continue;
                      i = AndroidUtilities.dp(356.0F);
                    }
                  }
                });
              return;
              paramInt1 = 0;
              break;
              paramInt1 = 0;
              break label354;
              if (AlertDialog.this.items == null)
                continue;
              if ((AlertDialog.this.titleTextView == null) && (AlertDialog.this.messageTextView.getVisibility() == 8));
              for (paramInt1 = AndroidUtilities.dp(8.0F); ; paramInt1 = 0)
              {
                localLayoutParams.topMargin = paramInt1;
                localLayoutParams.bottomMargin = AndroidUtilities.dp(8.0F);
                break;
              }
            }
            while (AlertDialog.this.messageTextView.getVisibility() != 0);
            if (AlertDialog.this.titleTextView == null);
            for (paramInt1 = AndroidUtilities.dp(19.0F); ; paramInt1 = 0)
            {
              localLayoutParams.topMargin = paramInt1;
              localLayoutParams.bottomMargin = AndroidUtilities.dp(20.0F);
              break;
            }
          }
          if (AlertDialog.this.progressViewContainer != null)
          {
            AlertDialog.this.progressViewContainer.measure(n, View.MeasureSpec.makeMeasureSpec(i, -2147483648));
            localLayoutParams = (LinearLayout.LayoutParams)AlertDialog.this.progressViewContainer.getLayoutParams();
            paramInt1 = AlertDialog.this.progressViewContainer.getMeasuredHeight();
            paramInt2 = localLayoutParams.bottomMargin;
            paramInt1 = i - (localLayoutParams.topMargin + (paramInt1 + paramInt2));
          }
          while (true)
          {
            paramInt2 = paramInt1;
            if (AlertDialog.this.lineProgressView == null)
              break;
            AlertDialog.this.lineProgressView.measure(n, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0F), 1073741824));
            localLayoutParams = (LinearLayout.LayoutParams)AlertDialog.this.lineProgressView.getLayoutParams();
            paramInt2 = AlertDialog.this.lineProgressView.getMeasuredHeight();
            i = localLayoutParams.bottomMargin;
            paramInt1 -= localLayoutParams.topMargin + (paramInt2 + i);
            AlertDialog.this.lineProgressViewPercent.measure(n, View.MeasureSpec.makeMeasureSpec(paramInt1, -2147483648));
            localLayoutParams = (LinearLayout.LayoutParams)AlertDialog.this.lineProgressViewPercent.getLayoutParams();
            paramInt2 = AlertDialog.this.lineProgressViewPercent.getMeasuredHeight();
            i = localLayoutParams.bottomMargin;
            paramInt2 = paramInt1 - (localLayoutParams.topMargin + (paramInt2 + i));
            break;
            paramInt1 = i;
            if (AlertDialog.this.messageTextView == null)
              continue;
            AlertDialog.this.messageTextView.measure(n, View.MeasureSpec.makeMeasureSpec(i, -2147483648));
            paramInt1 = i;
            if (AlertDialog.this.messageTextView.getVisibility() == 8)
              continue;
            localLayoutParams = (LinearLayout.LayoutParams)AlertDialog.this.messageTextView.getLayoutParams();
            paramInt1 = AlertDialog.this.messageTextView.getMeasuredHeight();
            paramInt2 = localLayoutParams.bottomMargin;
            paramInt1 = i - (localLayoutParams.topMargin + (paramInt1 + paramInt2));
          }
        }
      }

      public void requestLayout()
      {
        if (this.inLayout)
          return;
        super.requestLayout();
      }
    };
    paramBundle.setOrientation(1);
    paramBundle.setBackgroundDrawable(this.shadowDrawable);
    boolean bool;
    int i;
    if (Build.VERSION.SDK_INT >= 21)
    {
      bool = true;
      paramBundle.setFitsSystemWindows(bool);
      setContentView(paramBundle);
      if ((this.positiveButtonText == null) && (this.negativeButtonText == null) && (this.neutralButtonText == null))
        break label773;
      i = 1;
      label76: if (this.title != null)
      {
        this.titleTextView = new TextView(getContext());
        this.titleTextView.setText(this.title);
        this.titleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.titleTextView.setTextSize(1, 20.0F);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        localObject1 = this.titleTextView;
        if (!LocaleController.isRTL)
          break label778;
        j = 5;
        ((TextView)localObject1).setGravity(j | 0x30);
        localObject1 = this.titleTextView;
        if (!LocaleController.isRTL)
          break label784;
        j = 5;
        if (this.items == null)
          break label790;
        k = 14;
        paramBundle.addView((View)localObject1, LayoutHelper.createLinear(-2, -2, j | 0x30, 24, 19, 24, k));
      }
      label161: label186: label197: if (this.progressViewStyle == 0)
      {
        this.shadow[0] = ((BitmapDrawable)getContext().getResources().getDrawable(2130837728).mutate());
        this.shadow[1] = ((BitmapDrawable)getContext().getResources().getDrawable(2130837729).mutate());
        this.shadow[0].setAlpha(0);
        this.shadow[1].setAlpha(0);
        this.shadow[0].setCallback(this);
        this.shadow[1].setCallback(this);
        this.contentScrollView = new ScrollView(getContext())
        {
          protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
          {
            boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
            if (AlertDialog.this.shadow[0].getPaint().getAlpha() != 0)
            {
              AlertDialog.this.shadow[0].setBounds(0, getScrollY(), getMeasuredWidth(), getScrollY() + AndroidUtilities.dp(3.0F));
              AlertDialog.this.shadow[0].draw(paramCanvas);
            }
            if (AlertDialog.this.shadow[1].getPaint().getAlpha() != 0)
            {
              AlertDialog.this.shadow[1].setBounds(0, getScrollY() + getMeasuredHeight() - AndroidUtilities.dp(3.0F), getMeasuredWidth(), getScrollY() + getMeasuredHeight());
              AlertDialog.this.shadow[1].draw(paramCanvas);
            }
            return bool;
          }
        };
        this.contentScrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.contentScrollView, Theme.getColor("dialogScrollGlow"));
        paramBundle.addView(this.contentScrollView, LayoutHelper.createLinear(-1, -2, 0.0F, 0.0F, 0.0F, 0.0F));
        this.scrollContainer = new LinearLayout(getContext());
        this.scrollContainer.setOrientation(1);
        this.contentScrollView.addView(this.scrollContainer, new FrameLayout.LayoutParams(-1, -2));
      }
      this.messageTextView = new TextView(getContext());
      this.messageTextView.setTextColor(Theme.getColor("dialogTextBlack"));
      this.messageTextView.setTextSize(1, 16.0F);
      localObject1 = this.messageTextView;
      if (!LocaleController.isRTL)
        break label797;
      j = 5;
      label473: ((TextView)localObject1).setGravity(j | 0x30);
      if (this.progressViewStyle != 1)
        break label834;
      this.progressViewContainer = new FrameLayout(getContext());
      localObject1 = this.progressViewContainer;
      if (this.title != null)
        break label803;
      j = 24;
      label523: paramBundle.addView((View)localObject1, LayoutHelper.createLinear(-1, 44, 51, 23, j, 23, 24));
      localObject1 = new RadialProgressView(getContext());
      ((RadialProgressView)localObject1).setProgressColor(Theme.getColor("dialogProgressCircle"));
      localObject2 = this.progressViewContainer;
      if (!LocaleController.isRTL)
        break label809;
      j = 5;
      label584: ((FrameLayout)localObject2).addView((View)localObject1, LayoutHelper.createFrame(44, 44, j | 0x30));
      this.messageTextView.setLines(1);
      this.messageTextView.setSingleLine(true);
      this.messageTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject1 = this.progressViewContainer;
      localObject2 = this.messageTextView;
      if (!LocaleController.isRTL)
        break label815;
      j = 5;
      label650: if (!LocaleController.isRTL)
        break label821;
      int k = 0;
      label659: float f = k;
      if (!LocaleController.isRTL)
        break label828;
      k = 62;
      label673: ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-2, -2.0F, j | 0x10, f, 0.0F, k, 0.0F));
      if (TextUtils.isEmpty(this.message))
        break label1201;
      this.messageTextView.setText(this.message);
      this.messageTextView.setVisibility(0);
    }
    while (true)
    {
      if (this.items == null)
        break label1308;
      j = 0;
      while (true)
      {
        if (j >= this.items.length)
          break label1308;
        if (this.items[j] != null)
          break;
        j += 1;
      }
      bool = false;
      break;
      label773: i = 0;
      break label76;
      label778: j = 3;
      break label161;
      label784: j = 3;
      break label186;
      label790: m = 10;
      break label197;
      label797: j = 3;
      break label473;
      label803: j = 0;
      break label523;
      label809: j = 3;
      break label584;
      label815: j = 3;
      break label650;
      label821: m = 62;
      break label659;
      label828: m = 0;
      break label673;
      label834: if (this.progressViewStyle == 2)
      {
        localObject1 = this.messageTextView;
        if (LocaleController.isRTL)
        {
          j = 5;
          label857: if (this.title != null)
            break label1103;
          m = 19;
          label868: paramBundle.addView((View)localObject1, LayoutHelper.createLinear(-2, -2, j | 0x30, 24, m, 24, 20));
          this.lineProgressView = new LineProgressView(getContext());
          this.lineProgressView.setProgress(this.currentProgress / 100.0F, false);
          this.lineProgressView.setProgressColor(Theme.getColor("dialogLineProgress"));
          this.lineProgressView.setBackColor(Theme.getColor("dialogLineProgressBackground"));
          paramBundle.addView(this.lineProgressView, LayoutHelper.createLinear(-1, 4, 19, 24, 0, 24, 0));
          this.lineProgressViewPercent = new TextView(getContext());
          this.lineProgressViewPercent.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          localObject1 = this.lineProgressViewPercent;
          if (!LocaleController.isRTL)
            break label1109;
          j = 5;
          label1016: ((TextView)localObject1).setGravity(j | 0x30);
          this.lineProgressViewPercent.setTextColor(Theme.getColor("dialogTextGray2"));
          this.lineProgressViewPercent.setTextSize(1, 14.0F);
          localObject1 = this.lineProgressViewPercent;
          if (!LocaleController.isRTL)
            break label1115;
        }
        label1103: label1109: label1115: for (j = 5; ; j = 3)
        {
          paramBundle.addView((View)localObject1, LayoutHelper.createLinear(-2, -2, j | 0x30, 23, 4, 23, 24));
          updateLineProgressTextView();
          break;
          j = 3;
          break label857;
          m = 0;
          break label868;
          j = 3;
          break label1016;
        }
      }
      localObject1 = this.scrollContainer;
      localObject2 = this.messageTextView;
      if (LocaleController.isRTL)
      {
        j = 5;
        label1142: if ((this.customView == null) && (this.items == null))
          break label1195;
      }
      label1195: for (m = 20; ; m = 0)
      {
        ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-2, -2, j | 0x30, 24, 0, 24, m));
        break;
        j = 3;
        break label1142;
      }
      label1201: this.messageTextView.setVisibility(8);
    }
    Object localObject1 = new AlertDialogCell(getContext());
    Object localObject2 = this.items[j];
    if (this.itemIcons != null);
    for (int m = this.itemIcons[j]; ; m = 0)
    {
      ((AlertDialogCell)localObject1).setTextAndIcon((CharSequence)localObject2, m);
      this.scrollContainer.addView((View)localObject1, LayoutHelper.createLinear(-1, 48));
      ((AlertDialogCell)localObject1).setTag(Integer.valueOf(j));
      ((AlertDialogCell)localObject1).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (AlertDialog.this.onClickListener != null)
            AlertDialog.this.onClickListener.onClick(AlertDialog.this, ((Integer)paramView.getTag()).intValue());
          AlertDialog.this.dismiss();
        }
      });
      break;
    }
    label1308: if (this.customView != null)
    {
      if (this.customView.getParent() != null)
        ((ViewGroup)this.customView.getParent()).removeView(this.customView);
      this.scrollContainer.addView(this.customView, LayoutHelper.createLinear(-1, -2));
    }
    if (i != 0)
    {
      this.buttonsLayout = new FrameLayout(getContext())
      {
        protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
        {
          paramInt4 = getChildCount();
          Object localObject = null;
          int i = paramInt3 - paramInt1;
          paramInt1 = 0;
          if (paramInt1 < paramInt4)
          {
            View localView = getChildAt(paramInt1);
            if (((Integer)localView.getTag()).intValue() == -1)
            {
              localView.layout(i - getPaddingRight() - localView.getMeasuredWidth(), getPaddingTop(), i - getPaddingRight() + localView.getMeasuredWidth(), getPaddingTop() + localView.getMeasuredHeight());
              localObject = localView;
            }
            while (true)
            {
              paramInt1 += 1;
              break;
              if (((Integer)localView.getTag()).intValue() == -2)
              {
                paramInt3 = i - getPaddingRight() - localView.getMeasuredWidth();
                paramInt2 = paramInt3;
                if (localObject != null)
                  paramInt2 = paramInt3 - (localObject.getMeasuredWidth() + AndroidUtilities.dp(8.0F));
                localView.layout(paramInt2, getPaddingTop(), localView.getMeasuredWidth() + paramInt2, getPaddingTop() + localView.getMeasuredHeight());
                continue;
              }
              localView.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + localView.getMeasuredWidth(), getPaddingTop() + localView.getMeasuredHeight());
            }
          }
        }
      };
      this.buttonsLayout.setPadding(AndroidUtilities.dp(8.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(8.0F));
      paramBundle.addView(this.buttonsLayout, LayoutHelper.createLinear(-1, 52));
      if (this.positiveButtonText != null)
      {
        paramBundle = new TextView(getContext())
        {
          public void setEnabled(boolean paramBoolean)
          {
            super.setEnabled(paramBoolean);
            float f;
            if (paramBoolean)
              f = 1.0F;
            while (true)
            {
              setAlpha(f);
              return;
              f = 0.5F;
            }
          }
        };
        paramBundle.setMinWidth(AndroidUtilities.dp(64.0F));
        paramBundle.setTag(Integer.valueOf(-1));
        paramBundle.setTextSize(1, 14.0F);
        paramBundle.setTextColor(Theme.getColor("dialogButton"));
        paramBundle.setGravity(17);
        paramBundle.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        paramBundle.setText(this.positiveButtonText.toString().toUpperCase());
        paramBundle.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
        paramBundle.setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
        this.buttonsLayout.addView(paramBundle, LayoutHelper.createFrame(-2, 36, 53));
        paramBundle.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (AlertDialog.this.positiveButtonListener != null)
              AlertDialog.this.positiveButtonListener.onClick(AlertDialog.this, -1);
            AlertDialog.this.dismiss();
          }
        });
      }
      if (this.negativeButtonText != null)
      {
        paramBundle = new TextView(getContext())
        {
          public void setEnabled(boolean paramBoolean)
          {
            super.setEnabled(paramBoolean);
            float f;
            if (paramBoolean)
              f = 1.0F;
            while (true)
            {
              setAlpha(f);
              return;
              f = 0.5F;
            }
          }
        };
        paramBundle.setMinWidth(AndroidUtilities.dp(64.0F));
        paramBundle.setTag(Integer.valueOf(-2));
        paramBundle.setTextSize(1, 14.0F);
        paramBundle.setTextColor(Theme.getColor("dialogButton"));
        paramBundle.setGravity(17);
        paramBundle.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        paramBundle.setText(this.negativeButtonText.toString().toUpperCase());
        paramBundle.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
        paramBundle.setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
        this.buttonsLayout.addView(paramBundle, LayoutHelper.createFrame(-2, 36, 53));
        paramBundle.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (AlertDialog.this.negativeButtonListener != null)
              AlertDialog.this.negativeButtonListener.onClick(AlertDialog.this, -2);
            AlertDialog.this.cancel();
          }
        });
      }
      if (this.neutralButtonText != null)
      {
        paramBundle = new TextView(getContext())
        {
          public void setEnabled(boolean paramBoolean)
          {
            super.setEnabled(paramBoolean);
            float f;
            if (paramBoolean)
              f = 1.0F;
            while (true)
            {
              setAlpha(f);
              return;
              f = 0.5F;
            }
          }
        };
        paramBundle.setMinWidth(AndroidUtilities.dp(64.0F));
        paramBundle.setTag(Integer.valueOf(-3));
        paramBundle.setTextSize(1, 14.0F);
        paramBundle.setTextColor(Theme.getColor("dialogButton"));
        paramBundle.setGravity(17);
        paramBundle.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        paramBundle.setText(this.neutralButtonText.toString().toUpperCase());
        paramBundle.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
        paramBundle.setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
        this.buttonsLayout.addView(paramBundle, LayoutHelper.createFrame(-2, 36, 51));
        paramBundle.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (AlertDialog.this.neutralButtonListener != null)
              AlertDialog.this.neutralButtonListener.onClick(AlertDialog.this, -2);
            AlertDialog.this.dismiss();
          }
        });
      }
    }
    this.lastScreenWidth = AndroidUtilities.displaySize.x;
    int j = AndroidUtilities.displaySize.x;
    m = AndroidUtilities.dp(56.0F);
    if (AndroidUtilities.isTablet())
      if (AndroidUtilities.isSmallTablet())
        i = AndroidUtilities.dp(446.0F);
    while (true)
    {
      paramBundle = getWindow();
      localObject1 = new WindowManager.LayoutParams();
      ((WindowManager.LayoutParams)localObject1).copyFrom(paramBundle.getAttributes());
      ((WindowManager.LayoutParams)localObject1).dimAmount = 0.6F;
      ((WindowManager.LayoutParams)localObject1).width = (Math.min(i, j - m) + this.backgroundPaddings.left + this.backgroundPaddings.right);
      ((WindowManager.LayoutParams)localObject1).flags |= 2;
      if ((this.customView == null) || (!canTextInput(this.customView)))
        ((WindowManager.LayoutParams)localObject1).flags |= 131072;
      paramBundle.setAttributes((WindowManager.LayoutParams)localObject1);
      return;
      i = AndroidUtilities.dp(496.0F);
      continue;
      i = AndroidUtilities.dp(356.0F);
    }
  }

  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    if (this.contentScrollView != null)
      this.contentScrollView.postDelayed(paramRunnable, paramLong);
  }

  public void setButton(int paramInt, CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener)
  {
    switch (paramInt)
    {
    default:
      return;
    case -3:
      this.neutralButtonText = paramCharSequence;
      this.neutralButtonListener = paramOnClickListener;
      return;
    case -2:
      this.negativeButtonText = paramCharSequence;
      this.negativeButtonListener = paramOnClickListener;
      return;
    case -1:
    }
    this.positiveButtonText = paramCharSequence;
    this.positiveButtonListener = paramOnClickListener;
  }

  public void setCanceledOnTouchOutside(boolean paramBoolean)
  {
    super.setCanceledOnTouchOutside(paramBoolean);
  }

  public void setMessage(CharSequence paramCharSequence)
  {
    this.message = paramCharSequence;
    if (this.messageTextView != null)
    {
      if (!TextUtils.isEmpty(this.message))
      {
        this.messageTextView.setText(this.message);
        this.messageTextView.setVisibility(0);
      }
    }
    else
      return;
    this.messageTextView.setVisibility(8);
  }

  public void setProgress(int paramInt)
  {
    this.currentProgress = paramInt;
    if (this.lineProgressView != null)
    {
      this.lineProgressView.setProgress(paramInt / 100.0F, true);
      updateLineProgressTextView();
    }
  }

  public void setProgressStyle(int paramInt)
  {
    this.progressViewStyle = paramInt;
  }

  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    if (this.contentScrollView != null)
      this.contentScrollView.removeCallbacks(paramRunnable);
  }

  private class AlertDialogCell extends FrameLayout
  {
    private ImageView imageView;
    private TextView textView;

    public AlertDialogCell(Context arg2)
    {
      super();
      setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 2));
      setPadding(AndroidUtilities.dp(23.0F), 0, AndroidUtilities.dp(23.0F), 0);
      this.imageView = new ImageView(localContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
      this$1 = this.imageView;
      if (LocaleController.isRTL);
      for (int i = 5; ; i = 3)
      {
        addView(AlertDialog.this, LayoutHelper.createFrame(24, 24, i | 0x10));
        this.textView = new TextView(localContext);
        this.textView.setLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(1);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.textView.setTextSize(1, 16.0F);
        this$1 = this.textView;
        i = j;
        if (LocaleController.isRTL)
          i = 5;
        addView(AlertDialog.this, LayoutHelper.createFrame(-2, -2, i | 0x10));
        return;
      }
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

  public static class Builder
  {
    private AlertDialog alertDialog;

    public Builder(Context paramContext)
    {
      this.alertDialog = new AlertDialog(paramContext, 0);
    }

    public Builder(Context paramContext, int paramInt)
    {
      this.alertDialog = new AlertDialog(paramContext, paramInt);
    }

    public AlertDialog create()
    {
      return this.alertDialog;
    }

    public Context getContext()
    {
      return this.alertDialog.getContext();
    }

    public Builder setItems(CharSequence[] paramArrayOfCharSequence, DialogInterface.OnClickListener paramOnClickListener)
    {
      AlertDialog.access$602(this.alertDialog, paramArrayOfCharSequence);
      AlertDialog.access$1602(this.alertDialog, paramOnClickListener);
      return this;
    }

    public Builder setItems(CharSequence[] paramArrayOfCharSequence, int[] paramArrayOfInt, DialogInterface.OnClickListener paramOnClickListener)
    {
      AlertDialog.access$602(this.alertDialog, paramArrayOfCharSequence);
      AlertDialog.access$2102(this.alertDialog, paramArrayOfInt);
      AlertDialog.access$1602(this.alertDialog, paramOnClickListener);
      return this;
    }

    public Builder setMessage(CharSequence paramCharSequence)
    {
      AlertDialog.access$2302(this.alertDialog, paramCharSequence);
      return this;
    }

    public Builder setNegativeButton(CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener)
    {
      AlertDialog.access$2502(this.alertDialog, paramCharSequence);
      AlertDialog.access$1802(this.alertDialog, paramOnClickListener);
      return this;
    }

    public Builder setNeutralButton(CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener)
    {
      AlertDialog.access$2602(this.alertDialog, paramCharSequence);
      AlertDialog.access$1902(this.alertDialog, paramOnClickListener);
      return this;
    }

    public Builder setOnDismissListener(DialogInterface.OnDismissListener paramOnDismissListener)
    {
      this.alertDialog.setOnDismissListener(paramOnDismissListener);
      return this;
    }

    public Builder setPositiveButton(CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener)
    {
      AlertDialog.access$2402(this.alertDialog, paramCharSequence);
      AlertDialog.access$1702(this.alertDialog, paramOnClickListener);
      return this;
    }

    public Builder setTitle(CharSequence paramCharSequence)
    {
      AlertDialog.access$2202(this.alertDialog, paramCharSequence);
      return this;
    }

    public Builder setView(View paramView)
    {
      AlertDialog.access$402(this.alertDialog, paramView);
      return this;
    }

    public AlertDialog show()
    {
      this.alertDialog.show();
      return this.alertDialog;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ActionBar.AlertDialog
 * JD-Core Version:    0.6.0
 */