package org.vidogram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.query.StickersQuery;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.StickerSet;
import org.vidogram.tgnet.TLRPC.StickerSetCovered;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.LayoutHelper;

public class FeaturedStickerSetCell extends FrameLayout
{
  private TextView addButton;
  private int angle;
  private ImageView checkImage;
  private AnimatorSet currentAnimation;
  private boolean drawProgress;
  private BackupImageView imageView;
  private boolean isInstalled;
  private long lastUpdateTime;
  private boolean needDivider;
  private float progressAlpha;
  private Paint progressPaint = new Paint(1);
  private RectF progressRect = new RectF();
  private Rect rect = new Rect();
  private TLRPC.StickerSetCovered stickersSet;
  private TextView textView;
  private TextView valueTextView;
  private boolean wasLayout;

  public FeaturedStickerSetCell(Context paramContext)
  {
    super(paramContext);
    this.progressPaint.setColor(Theme.getColor("featuredStickers_buttonProgress"));
    this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
    this.progressPaint.setStyle(Paint.Style.STROKE);
    this.progressPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    Object localObject = this.textView;
    int i;
    label191: float f1;
    label200: float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      ((TextView)localObject).setGravity(i);
      localObject = this.textView;
      if (!LocaleController.isRTL)
        break label686;
      i = 5;
      if (!LocaleController.isRTL)
        break label692;
      f1 = 100.0F;
      if (!LocaleController.isRTL)
        break label698;
      f2 = 71.0F;
      label209: addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i, f1, 10.0F, f2, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.valueTextView.setTextSize(1, 13.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL)
        break label704;
      i = 5;
      label312: ((TextView)localObject).setGravity(i);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL)
        break label710;
      i = 5;
      label334: if (!LocaleController.isRTL)
        break label716;
      f1 = 100.0F;
      label343: if (!LocaleController.isRTL)
        break label722;
      f2 = 71.0F;
      label352: addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i, f1, 35.0F, f2, 0.0F));
      this.imageView = new BackupImageView(paramContext);
      this.imageView.setAspectFit(true);
      localObject = this.imageView;
      if (!LocaleController.isRTL)
        break label728;
      i = 5;
      label407: if (!LocaleController.isRTL)
        break label734;
      f1 = 0.0F;
      label415: if (!LocaleController.isRTL)
        break label740;
      f2 = 12.0F;
      label424: addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.addButton = new TextView(paramContext)
      {
        protected void onDraw(Canvas paramCanvas)
        {
          super.onDraw(paramCanvas);
          long l1;
          long l2;
          if ((FeaturedStickerSetCell.this.drawProgress) || ((!FeaturedStickerSetCell.this.drawProgress) && (FeaturedStickerSetCell.this.progressAlpha != 0.0F)))
          {
            FeaturedStickerSetCell.this.progressPaint.setAlpha(Math.min(255, (int)(FeaturedStickerSetCell.this.progressAlpha * 255.0F)));
            int i = getMeasuredWidth() - AndroidUtilities.dp(11.0F);
            FeaturedStickerSetCell.this.progressRect.set(i, AndroidUtilities.dp(3.0F), i + AndroidUtilities.dp(8.0F), AndroidUtilities.dp(11.0F));
            paramCanvas.drawArc(FeaturedStickerSetCell.this.progressRect, FeaturedStickerSetCell.this.angle, 220.0F, false, FeaturedStickerSetCell.this.progressPaint);
            invalidate((int)FeaturedStickerSetCell.this.progressRect.left - AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetCell.this.progressRect.top - AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetCell.this.progressRect.right + AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetCell.this.progressRect.bottom + AndroidUtilities.dp(2.0F));
            l1 = System.currentTimeMillis();
            if (Math.abs(FeaturedStickerSetCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000L)
            {
              l2 = l1 - FeaturedStickerSetCell.this.lastUpdateTime;
              float f = (float)(360L * l2) / 2000.0F;
              FeaturedStickerSetCell.access$402(FeaturedStickerSetCell.this, (int)(f + FeaturedStickerSetCell.this.angle));
              FeaturedStickerSetCell.access$402(FeaturedStickerSetCell.this, FeaturedStickerSetCell.this.angle - FeaturedStickerSetCell.this.angle / 360 * 360);
              if (!FeaturedStickerSetCell.this.drawProgress)
                break label387;
              if (FeaturedStickerSetCell.this.progressAlpha < 1.0F)
              {
                paramCanvas = FeaturedStickerSetCell.this;
                f = FeaturedStickerSetCell.this.progressAlpha;
                FeaturedStickerSetCell.access$102(paramCanvas, (float)l2 / 200.0F + f);
                if (FeaturedStickerSetCell.this.progressAlpha > 1.0F)
                  FeaturedStickerSetCell.access$102(FeaturedStickerSetCell.this, 1.0F);
              }
            }
          }
          while (true)
          {
            FeaturedStickerSetCell.access$502(FeaturedStickerSetCell.this, l1);
            invalidate();
            return;
            label387: if (FeaturedStickerSetCell.this.progressAlpha <= 0.0F)
              continue;
            FeaturedStickerSetCell.access$102(FeaturedStickerSetCell.this, FeaturedStickerSetCell.this.progressAlpha - (float)l2 / 200.0F);
            if (FeaturedStickerSetCell.this.progressAlpha >= 0.0F)
              continue;
            FeaturedStickerSetCell.access$102(FeaturedStickerSetCell.this, 0.0F);
          }
        }
      };
      this.addButton.setPadding(AndroidUtilities.dp(17.0F), 0, AndroidUtilities.dp(17.0F), 0);
      this.addButton.setGravity(17);
      this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
      this.addButton.setTextSize(1, 14.0F);
      this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.addButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0F), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
      this.addButton.setText(LocaleController.getString("Add", 2131165273).toUpperCase());
      localObject = this.addButton;
      if (!LocaleController.isRTL)
        break label745;
      i = j;
      label580: if (!LocaleController.isRTL)
        break label751;
      f1 = 14.0F;
      label589: if (!LocaleController.isRTL)
        break label756;
      f2 = 0.0F;
    }
    while (true)
    {
      addView((View)localObject, LayoutHelper.createFrame(-2, 28.0F, i | 0x30, f1, 18.0F, f2, 0.0F));
      this.checkImage = new ImageView(paramContext);
      this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
      this.checkImage.setImageResource(2130838070);
      addView(this.checkImage, LayoutHelper.createFrame(19, 14.0F));
      return;
      i = 3;
      break;
      label686: i = 3;
      break label191;
      label692: f1 = 71.0F;
      break label200;
      label698: f2 = 100.0F;
      break label209;
      label704: i = 3;
      break label312;
      label710: i = 3;
      break label334;
      label716: f1 = 71.0F;
      break label343;
      label722: f2 = 100.0F;
      break label352;
      label728: i = 3;
      break label407;
      label734: f1 = 12.0F;
      break label415;
      label740: f2 = 0.0F;
      break label424;
      label745: i = 5;
      break label580;
      label751: f1 = 0.0F;
      break label589;
      label756: f2 = 14.0F;
    }
  }

  public TLRPC.StickerSetCovered getStickerSet()
  {
    return this.stickersSet;
  }

  public boolean isInstalled()
  {
    return this.isInstalled;
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.wasLayout = false;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider)
      paramCanvas.drawLine(0.0F, getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    paramInt1 = this.addButton.getLeft() + this.addButton.getMeasuredWidth() / 2 - this.checkImage.getMeasuredWidth() / 2;
    paramInt2 = this.addButton.getTop() + this.addButton.getMeasuredHeight() / 2 - this.checkImage.getMeasuredHeight() / 2;
    this.checkImage.layout(paramInt1, paramInt2, this.checkImage.getMeasuredWidth() + paramInt1, this.checkImage.getMeasuredHeight() + paramInt2);
    this.wasLayout = true;
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824);
    int i = AndroidUtilities.dp(64.0F);
    if (this.needDivider);
    for (paramInt1 = 1; ; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, 1073741824));
      return;
    }
  }

  public void setAddOnClickListener(View.OnClickListener paramOnClickListener)
  {
    this.addButton.setOnClickListener(paramOnClickListener);
  }

  public void setDrawProgress(boolean paramBoolean)
  {
    this.drawProgress = paramBoolean;
    this.lastUpdateTime = System.currentTimeMillis();
    this.addButton.invalidate();
  }

  public void setStickersSet(TLRPC.StickerSetCovered paramStickerSetCovered, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i;
    label44: 2 local2;
    Object localObject;
    if ((paramStickerSetCovered == this.stickersSet) && (this.wasLayout))
    {
      i = 1;
      this.needDivider = paramBoolean1;
      this.stickersSet = paramStickerSetCovered;
      this.lastUpdateTime = System.currentTimeMillis();
      if (this.needDivider)
        break label468;
      paramBoolean1 = true;
      setWillNotDraw(paramBoolean1);
      if (this.currentAnimation != null)
      {
        this.currentAnimation.cancel();
        this.currentAnimation = null;
      }
      this.textView.setText(this.stickersSet.set.title);
      if (!paramBoolean2)
        break label486;
      local2 = new Drawable()
      {
        Paint paint = new Paint(1);

        public void draw(Canvas paramCanvas)
        {
          this.paint.setColor(-12277526);
          paramCanvas.drawCircle(AndroidUtilities.dp(4.0F), AndroidUtilities.dp(5.0F), AndroidUtilities.dp(3.0F), this.paint);
        }

        public int getIntrinsicHeight()
        {
          return AndroidUtilities.dp(8.0F);
        }

        public int getIntrinsicWidth()
        {
          return AndroidUtilities.dp(12.0F);
        }

        public int getOpacity()
        {
          return 0;
        }

        public void setAlpha(int paramInt)
        {
        }

        public void setColorFilter(ColorFilter paramColorFilter)
        {
        }
      };
      TextView localTextView = this.textView;
      if (!LocaleController.isRTL)
        break label473;
      localObject = null;
      label114: if (!LocaleController.isRTL)
        break label480;
      label120: localTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable)localObject, null, local2, null);
      label131: this.valueTextView.setText(LocaleController.formatPluralString("Stickers", paramStickerSetCovered.set.count));
      if ((paramStickerSetCovered.cover == null) || (paramStickerSetCovered.cover.thumb == null) || (paramStickerSetCovered.cover.thumb.location == null))
        break label500;
      this.imageView.setImage(paramStickerSetCovered.cover.thumb.location, null, "webp", null);
      label203: if (i == 0)
        break label771;
      paramBoolean1 = this.isInstalled;
      paramBoolean2 = StickersQuery.isStickerPackInstalled(paramStickerSetCovered.set.id);
      this.isInstalled = paramBoolean2;
      if (!paramBoolean2)
        break label542;
      if (!paramBoolean1)
      {
        this.checkImage.setVisibility(0);
        this.addButton.setClickable(false);
        this.currentAnimation = new AnimatorSet();
        this.currentAnimation.setDuration(200L);
        this.currentAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.addButton, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.addButton, "scaleX", new float[] { 1.0F, 0.01F }), ObjectAnimator.ofFloat(this.addButton, "scaleY", new float[] { 1.0F, 0.01F }), ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[] { 0.01F, 1.0F }), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[] { 0.01F, 1.0F }) });
        this.currentAnimation.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationCancel(Animator paramAnimator)
          {
            if ((FeaturedStickerSetCell.this.currentAnimation != null) && (FeaturedStickerSetCell.this.currentAnimation.equals(paramAnimator)))
              FeaturedStickerSetCell.access$602(FeaturedStickerSetCell.this, null);
          }

          public void onAnimationEnd(Animator paramAnimator)
          {
            if ((FeaturedStickerSetCell.this.currentAnimation != null) && (FeaturedStickerSetCell.this.currentAnimation.equals(paramAnimator)))
              FeaturedStickerSetCell.this.addButton.setVisibility(4);
          }
        });
        this.currentAnimation.start();
      }
    }
    label468: label473: label480: label486: label500: 
    do
    {
      return;
      i = 0;
      break;
      paramBoolean1 = false;
      break label44;
      localObject = local2;
      break label114;
      local2 = null;
      break label120;
      this.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
      break label131;
      if (paramStickerSetCovered.covers.isEmpty())
        break label203;
      this.imageView.setImage(((TLRPC.Document)paramStickerSetCovered.covers.get(0)).thumb.location, null, "webp", null);
      break label203;
    }
    while (!paramBoolean1);
    label542: this.addButton.setVisibility(0);
    this.addButton.setClickable(true);
    this.currentAnimation = new AnimatorSet();
    this.currentAnimation.setDuration(200L);
    this.currentAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[] { 1.0F, 0.01F }), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[] { 1.0F, 0.01F }), ObjectAnimator.ofFloat(this.addButton, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.addButton, "scaleX", new float[] { 0.01F, 1.0F }), ObjectAnimator.ofFloat(this.addButton, "scaleY", new float[] { 0.01F, 1.0F }) });
    this.currentAnimation.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationCancel(Animator paramAnimator)
      {
        if ((FeaturedStickerSetCell.this.currentAnimation != null) && (FeaturedStickerSetCell.this.currentAnimation.equals(paramAnimator)))
          FeaturedStickerSetCell.access$602(FeaturedStickerSetCell.this, null);
      }

      public void onAnimationEnd(Animator paramAnimator)
      {
        if ((FeaturedStickerSetCell.this.currentAnimation != null) && (FeaturedStickerSetCell.this.currentAnimation.equals(paramAnimator)))
          FeaturedStickerSetCell.this.checkImage.setVisibility(4);
      }
    });
    this.currentAnimation.start();
    return;
    label771: paramBoolean1 = StickersQuery.isStickerPackInstalled(paramStickerSetCovered.set.id);
    this.isInstalled = paramBoolean1;
    if (paramBoolean1)
    {
      this.addButton.setVisibility(4);
      this.addButton.setClickable(false);
      this.checkImage.setVisibility(0);
      this.checkImage.setScaleX(1.0F);
      this.checkImage.setScaleY(1.0F);
      this.checkImage.setAlpha(1.0F);
      return;
    }
    this.addButton.setVisibility(0);
    this.addButton.setClickable(true);
    this.checkImage.setVisibility(4);
    this.addButton.setScaleX(1.0F);
    this.addButton.setScaleY(1.0F);
    this.addButton.setAlpha(1.0F);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.FeaturedStickerSetCell
 * JD-Core Version:    0.6.0
 */