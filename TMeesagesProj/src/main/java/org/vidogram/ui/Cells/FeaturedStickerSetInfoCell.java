package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.query.StickersQuery;
import org.vidogram.tgnet.TLRPC.StickerSet;
import org.vidogram.tgnet.TLRPC.StickerSetCovered;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;

public class FeaturedStickerSetInfoCell extends FrameLayout
{
  private TextView addButton;
  private Drawable addDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0F), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
  private int angle;
  private Paint botProgressPaint = new Paint(1);
  private Drawable delDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0F), Theme.getColor("featuredStickers_delButton"), Theme.getColor("featuredStickers_delButtonPressed"));
  private boolean drawProgress;
  Drawable drawable = new Drawable()
  {
    Paint paint = new Paint(1);

    public void draw(Canvas paramCanvas)
    {
      this.paint.setColor(Theme.getColor("featuredStickers_unread"));
      paramCanvas.drawCircle(AndroidUtilities.dp(8.0F), 0.0F, AndroidUtilities.dp(4.0F), this.paint);
    }

    public int getIntrinsicHeight()
    {
      return AndroidUtilities.dp(26.0F);
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
  private boolean hasOnClick;
  private TextView infoTextView;
  private boolean isInstalled;
  private long lastUpdateTime;
  private TextView nameTextView;
  private float progressAlpha;
  private RectF rect = new RectF();
  private TLRPC.StickerSetCovered set;

  public FeaturedStickerSetInfoCell(Context paramContext, int paramInt)
  {
    super(paramContext);
    this.botProgressPaint.setColor(Theme.getColor("featuredStickers_buttonProgress"));
    this.botProgressPaint.setStrokeCap(Paint.Cap.ROUND);
    this.botProgressPaint.setStyle(Paint.Style.STROKE);
    this.botProgressPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(Theme.getColor("chat_emojiPanelTrendingTitle"));
    this.nameTextView.setTextSize(1, 17.0F);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.nameTextView.setSingleLine(true);
    addView(this.nameTextView, LayoutHelper.createFrame(-2, -1.0F, 51, paramInt, 8.0F, 100.0F, 0.0F));
    this.infoTextView = new TextView(paramContext);
    this.infoTextView.setTextColor(Theme.getColor("chat_emojiPanelTrendingDescription"));
    this.infoTextView.setTextSize(1, 13.0F);
    this.infoTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.infoTextView.setSingleLine(true);
    addView(this.infoTextView, LayoutHelper.createFrame(-2, -1.0F, 51, paramInt, 30.0F, 100.0F, 0.0F));
    this.addButton = new TextView(paramContext)
    {
      protected void onDraw(Canvas paramCanvas)
      {
        super.onDraw(paramCanvas);
        long l1;
        long l2;
        if ((FeaturedStickerSetInfoCell.this.drawProgress) || ((!FeaturedStickerSetInfoCell.this.drawProgress) && (FeaturedStickerSetInfoCell.this.progressAlpha != 0.0F)))
        {
          FeaturedStickerSetInfoCell.this.botProgressPaint.setAlpha(Math.min(255, (int)(FeaturedStickerSetInfoCell.this.progressAlpha * 255.0F)));
          int i = getMeasuredWidth() - AndroidUtilities.dp(11.0F);
          FeaturedStickerSetInfoCell.this.rect.set(i, AndroidUtilities.dp(3.0F), i + AndroidUtilities.dp(8.0F), AndroidUtilities.dp(11.0F));
          paramCanvas.drawArc(FeaturedStickerSetInfoCell.this.rect, FeaturedStickerSetInfoCell.this.angle, 220.0F, false, FeaturedStickerSetInfoCell.this.botProgressPaint);
          invalidate((int)FeaturedStickerSetInfoCell.this.rect.left - AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetInfoCell.this.rect.top - AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetInfoCell.this.rect.right + AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetInfoCell.this.rect.bottom + AndroidUtilities.dp(2.0F));
          l1 = System.currentTimeMillis();
          if (Math.abs(FeaturedStickerSetInfoCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000L)
          {
            l2 = l1 - FeaturedStickerSetInfoCell.this.lastUpdateTime;
            float f = (float)(360L * l2) / 2000.0F;
            FeaturedStickerSetInfoCell.access$402(FeaturedStickerSetInfoCell.this, (int)(f + FeaturedStickerSetInfoCell.this.angle));
            FeaturedStickerSetInfoCell.access$402(FeaturedStickerSetInfoCell.this, FeaturedStickerSetInfoCell.this.angle - FeaturedStickerSetInfoCell.this.angle / 360 * 360);
            if (!FeaturedStickerSetInfoCell.this.drawProgress)
              break label387;
            if (FeaturedStickerSetInfoCell.this.progressAlpha < 1.0F)
            {
              paramCanvas = FeaturedStickerSetInfoCell.this;
              f = FeaturedStickerSetInfoCell.this.progressAlpha;
              FeaturedStickerSetInfoCell.access$102(paramCanvas, (float)l2 / 200.0F + f);
              if (FeaturedStickerSetInfoCell.this.progressAlpha > 1.0F)
                FeaturedStickerSetInfoCell.access$102(FeaturedStickerSetInfoCell.this, 1.0F);
            }
          }
        }
        while (true)
        {
          FeaturedStickerSetInfoCell.access$502(FeaturedStickerSetInfoCell.this, l1);
          invalidate();
          return;
          label387: if (FeaturedStickerSetInfoCell.this.progressAlpha <= 0.0F)
            continue;
          FeaturedStickerSetInfoCell.access$102(FeaturedStickerSetInfoCell.this, FeaturedStickerSetInfoCell.this.progressAlpha - (float)l2 / 200.0F);
          if (FeaturedStickerSetInfoCell.this.progressAlpha >= 0.0F)
            continue;
          FeaturedStickerSetInfoCell.access$102(FeaturedStickerSetInfoCell.this, 0.0F);
        }
      }
    };
    this.addButton.setPadding(AndroidUtilities.dp(17.0F), 0, AndroidUtilities.dp(17.0F), 0);
    this.addButton.setGravity(17);
    this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
    this.addButton.setTextSize(1, 14.0F);
    this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    addView(this.addButton, LayoutHelper.createFrame(-2, 28.0F, 53, 0.0F, 16.0F, 14.0F, 0.0F));
  }

  public TLRPC.StickerSetCovered getStickerSet()
  {
    return this.set;
  }

  public boolean isInstalled()
  {
    return this.isInstalled;
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0F), 1073741824));
  }

  public void setAddOnClickListener(View.OnClickListener paramOnClickListener)
  {
    this.hasOnClick = true;
    this.addButton.setOnClickListener(paramOnClickListener);
  }

  public void setDrawProgress(boolean paramBoolean)
  {
    this.drawProgress = paramBoolean;
    this.lastUpdateTime = System.currentTimeMillis();
    this.addButton.invalidate();
  }

  public void setStickerSet(TLRPC.StickerSetCovered paramStickerSetCovered, boolean paramBoolean)
  {
    this.lastUpdateTime = System.currentTimeMillis();
    this.nameTextView.setText(paramStickerSetCovered.set.title);
    this.infoTextView.setText(LocaleController.formatPluralString("Stickers", paramStickerSetCovered.set.count));
    if (paramBoolean)
    {
      this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, this.drawable, null);
      if (!this.hasOnClick)
        break label177;
      this.addButton.setVisibility(0);
      paramBoolean = StickersQuery.isStickerPackInstalled(paramStickerSetCovered.set.id);
      this.isInstalled = paramBoolean;
      if (!paramBoolean)
        break label144;
      this.addButton.setBackgroundDrawable(this.delDrawable);
      this.addButton.setText(LocaleController.getString("StickersRemove", 2131166488).toUpperCase());
    }
    while (true)
    {
      this.set = paramStickerSetCovered;
      return;
      this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
      break;
      label144: this.addButton.setBackgroundDrawable(this.addDrawable);
      this.addButton.setText(LocaleController.getString("Add", 2131165273).toUpperCase());
      continue;
      label177: this.addButton.setVisibility(8);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.FeaturedStickerSetInfoCell
 * JD-Core Version:    0.6.0
 */