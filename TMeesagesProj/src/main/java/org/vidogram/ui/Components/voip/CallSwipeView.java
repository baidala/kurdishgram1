package org.vidogram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;

public class CallSwipeView extends View
{
  private boolean animatingArrows = false;
  private Path arrow = new Path();
  private int[] arrowAlphas = { 64, 64, 64 };
  private AnimatorSet arrowAnim;
  private Paint arrowsPaint;
  private boolean dragFromRight;
  private float dragStartX;
  private boolean dragging = false;
  private Listener listener;
  private Paint pullBgPaint;
  private RectF tmpRect = new RectF();
  private View viewToDrag;

  public CallSwipeView(Context paramContext)
  {
    super(paramContext);
    init();
  }

  public CallSwipeView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }

  public CallSwipeView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init();
  }

  private int getDraggedViewWidth()
  {
    return getHeight();
  }

  private void init()
  {
    this.arrowsPaint = new Paint(1);
    this.arrowsPaint.setColor(-1);
    this.arrowsPaint.setStyle(Paint.Style.STROKE);
    this.arrowsPaint.setStrokeWidth(AndroidUtilities.dp(2.5F));
    this.pullBgPaint = new Paint(1);
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < this.arrowAlphas.length)
    {
      ObjectAnimator localObjectAnimator = ObjectAnimator.ofInt(new ArrowAnimWrapper(i), "arrowAlpha", new int[] { 64, 255, 64 });
      localObjectAnimator.setDuration(700L);
      localObjectAnimator.setStartDelay(i * 200);
      localArrayList.add(localObjectAnimator);
      i += 1;
    }
    this.arrowAnim = new AnimatorSet();
    this.arrowAnim.playTogether(localArrayList);
    this.arrowAnim.addListener(new AnimatorListenerAdapter()
    {
      private boolean canceled = false;
      private Runnable restarter = new Runnable()
      {
        public void run()
        {
          CallSwipeView.this.arrowAnim.start();
        }
      };
      private long startTime;

      public void onAnimationCancel(Animator paramAnimator)
      {
        this.canceled = true;
      }

      public void onAnimationEnd(Animator paramAnimator)
      {
        if (System.currentTimeMillis() - this.startTime < paramAnimator.getDuration() / 4L)
          FileLog.w("Not repeating animation because previous loop was too fast");
        do
          return;
        while ((this.canceled) || (!CallSwipeView.this.animatingArrows));
        CallSwipeView.this.post(this.restarter);
      }

      public void onAnimationStart(Animator paramAnimator)
      {
        this.startTime = System.currentTimeMillis();
      }
    });
  }

  private void updateArrowPath()
  {
    this.arrow.reset();
    int i = AndroidUtilities.dp(6.0F);
    if (this.dragFromRight)
    {
      this.arrow.moveTo(i, -i);
      this.arrow.lineTo(0.0F, 0.0F);
      this.arrow.lineTo(i, i);
      return;
    }
    this.arrow.moveTo(0.0F, -i);
    this.arrow.lineTo(i, 0.0F);
    this.arrow.lineTo(0.0F, i);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    label121: int i;
    label135: float f1;
    if (this.viewToDrag.getTranslationX() != 0.0F)
    {
      if (this.dragFromRight)
      {
        this.tmpRect.set(getWidth() + this.viewToDrag.getTranslationX() - getDraggedViewWidth(), 0.0F, getWidth(), getHeight());
        paramCanvas.drawRoundRect(this.tmpRect, getHeight() / 2, getHeight() / 2, this.pullBgPaint);
      }
    }
    else
    {
      paramCanvas.save();
      if (!this.dragFromRight)
        break label276;
      paramCanvas.translate(getWidth() - getHeight() - AndroidUtilities.dp(18.0F), getHeight() / 2);
      float f2 = Math.abs(this.viewToDrag.getTranslationX());
      i = 0;
      if (i >= 3)
        break label307;
      if (f2 <= AndroidUtilities.dp(i * 16))
        break label316;
      f1 = 1.0F - Math.min(1.0F, Math.max(0.0F, (f2 - AndroidUtilities.dp(16.0F) * i) / AndroidUtilities.dp(16.0F)));
    }
    while (true)
    {
      this.arrowsPaint.setAlpha(Math.round(f1 * this.arrowAlphas[i]));
      paramCanvas.drawPath(this.arrow, this.arrowsPaint);
      if (this.dragFromRight)
        f1 = -16.0F;
      while (true)
      {
        paramCanvas.translate(AndroidUtilities.dp(f1), 0.0F);
        i += 1;
        break label135;
        this.tmpRect.set(0.0F, 0.0F, this.viewToDrag.getTranslationX() + getDraggedViewWidth(), getHeight());
        break;
        label276: paramCanvas.translate(getHeight() + AndroidUtilities.dp(12.0F), getHeight() / 2);
        break label121;
        f1 = 16.0F;
      }
      label307: paramCanvas.restore();
      invalidate();
      return;
      label316: f1 = 1.0F;
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f2 = 0.0F;
    if (!isEnabled())
      return false;
    if (paramMotionEvent.getAction() == 0)
      if (((!this.dragFromRight) && (paramMotionEvent.getX() < getDraggedViewWidth())) || ((this.dragFromRight) && (paramMotionEvent.getX() > getWidth() - getDraggedViewWidth())))
      {
        this.dragging = true;
        this.dragStartX = paramMotionEvent.getX();
        getParent().requestDisallowInterceptTouchEvent(true);
        this.listener.onDragStart();
        stopAnimatingArrows();
      }
    while (true)
    {
      return this.dragging;
      if (paramMotionEvent.getAction() == 2)
      {
        View localView = this.viewToDrag;
        float f1;
        label137: float f3;
        float f4;
        if (this.dragFromRight)
        {
          f1 = -(getWidth() - getDraggedViewWidth());
          f3 = paramMotionEvent.getX();
          f4 = this.dragStartX;
          if (!this.dragFromRight)
            break label186;
        }
        while (true)
        {
          localView.setTranslationX(Math.max(f1, Math.min(f3 - f4, f2)));
          invalidate();
          break;
          f1 = 0.0F;
          break label137;
          label186: f2 = getWidth() - getDraggedViewWidth();
        }
      }
      if ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3))
        continue;
      if ((Math.abs(this.viewToDrag.getTranslationX()) >= getWidth() - getDraggedViewWidth()) && (paramMotionEvent.getAction() == 1))
      {
        this.listener.onDragComplete();
        continue;
      }
      this.listener.onDragCancel();
      this.viewToDrag.animate().translationX(0.0F).setDuration(200L).start();
      invalidate();
      startAnimatingArrows();
      this.dragging = false;
    }
  }

  public void reset()
  {
    this.listener.onDragCancel();
    this.viewToDrag.animate().translationX(0.0F).setDuration(200L).start();
    invalidate();
    startAnimatingArrows();
    this.dragging = false;
  }

  public void setColor(int paramInt)
  {
    this.pullBgPaint.setColor(paramInt);
    this.pullBgPaint.setAlpha(178);
  }

  public void setListener(Listener paramListener)
  {
    this.listener = paramListener;
  }

  public void setViewToDrag(View paramView, boolean paramBoolean)
  {
    this.viewToDrag = paramView;
    this.dragFromRight = paramBoolean;
    updateArrowPath();
  }

  public void startAnimatingArrows()
  {
    if (this.animatingArrows)
      return;
    this.animatingArrows = true;
    this.arrowAnim.start();
  }

  public void stopAnimatingArrows()
  {
    this.animatingArrows = false;
  }

  private class ArrowAnimWrapper
  {
    private int index;

    public ArrowAnimWrapper(int arg2)
    {
      int i;
      this.index = i;
    }

    public int getArrowAlpha()
    {
      return CallSwipeView.this.arrowAlphas[this.index];
    }

    public void setArrowAlpha(int paramInt)
    {
      CallSwipeView.this.arrowAlphas[this.index] = paramInt;
    }
  }

  public static abstract interface Listener
  {
    public abstract void onDragCancel();

    public abstract void onDragComplete();

    public abstract void onDragStart();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.voip.CallSwipeView
 * JD-Core Version:    0.6.0
 */