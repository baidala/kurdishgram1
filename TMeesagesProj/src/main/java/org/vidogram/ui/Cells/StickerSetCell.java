package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.StickerSet;
import org.vidogram.tgnet.TLRPC.TL_messages_stickerSet;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.LayoutHelper;

public class StickerSetCell extends FrameLayout
{
  private BackupImageView imageView;
  private boolean needDivider;
  private ImageView optionsButton;
  private Rect rect = new Rect();
  private TLRPC.TL_messages_stickerSet stickersSet;
  private TextView textView;
  private TextView valueTextView;

  public StickerSetCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    Object localObject = this.textView;
    label124: float f1;
    label133: float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      ((TextView)localObject).setGravity(i);
      localObject = this.textView;
      if (!LocaleController.isRTL)
        break label485;
      i = 5;
      if (!LocaleController.isRTL)
        break label491;
      f1 = 40.0F;
      if (!LocaleController.isRTL)
        break label497;
      f2 = 71.0F;
      label142: addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i, f1, 10.0F, f2, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.valueTextView.setTextSize(1, 13.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL)
        break label503;
      i = 5;
      label235: ((TextView)localObject).setGravity(i);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL)
        break label509;
      i = 5;
      label257: if (!LocaleController.isRTL)
        break label515;
      f1 = 40.0F;
      label266: if (!LocaleController.isRTL)
        break label521;
      f2 = 71.0F;
      label275: addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i, f1, 35.0F, f2, 0.0F));
      this.imageView = new BackupImageView(paramContext);
      this.imageView.setAspectFit(true);
      localObject = this.imageView;
      if (!LocaleController.isRTL)
        break label527;
      i = 5;
      label330: if (!LocaleController.isRTL)
        break label533;
      f1 = 0.0F;
      label338: if (!LocaleController.isRTL)
        break label539;
      f2 = 12.0F;
      label347: addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.optionsButton = new ImageView(paramContext);
      this.optionsButton.setFocusable(false);
      this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
      this.optionsButton.setImageResource(2130837926);
      this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
      this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
      paramContext = this.optionsButton;
      if (!LocaleController.isRTL)
        break label544;
    }
    label515: label521: label527: label533: label539: label544: for (int i = j; ; i = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(40, 40, i | 0x30));
      return;
      i = 3;
      break;
      label485: i = 3;
      break label124;
      label491: f1 = 71.0F;
      break label133;
      label497: f2 = 40.0F;
      break label142;
      label503: i = 3;
      break label235;
      label509: i = 3;
      break label257;
      f1 = 71.0F;
      break label266;
      f2 = 40.0F;
      break label275;
      i = 3;
      break label330;
      f1 = 12.0F;
      break label338;
      f2 = 0.0F;
      break label347;
    }
  }

  public TLRPC.TL_messages_stickerSet getStickersSet()
  {
    return this.stickersSet;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider)
      paramCanvas.drawLine(0.0F, getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
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

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null))
    {
      this.optionsButton.getHitRect(this.rect);
      if (this.rect.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY()))
        return true;
    }
    return super.onTouchEvent(paramMotionEvent);
  }

  public void setOnOptionsClick(View.OnClickListener paramOnClickListener)
  {
    this.optionsButton.setOnClickListener(paramOnClickListener);
  }

  public void setStickersSet(TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet, boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
    this.stickersSet = paramTL_messages_stickerSet;
    this.textView.setText(this.stickersSet.set.title);
    if (this.stickersSet.set.archived)
    {
      this.textView.setAlpha(0.5F);
      this.valueTextView.setAlpha(0.5F);
      this.imageView.setAlpha(0.5F);
    }
    while (true)
    {
      paramTL_messages_stickerSet = paramTL_messages_stickerSet.documents;
      if ((paramTL_messages_stickerSet == null) || (paramTL_messages_stickerSet.isEmpty()))
        break;
      this.valueTextView.setText(LocaleController.formatPluralString("Stickers", paramTL_messages_stickerSet.size()));
      paramTL_messages_stickerSet = (TLRPC.Document)paramTL_messages_stickerSet.get(0);
      if ((paramTL_messages_stickerSet.thumb != null) && (paramTL_messages_stickerSet.thumb.location != null))
        this.imageView.setImage(paramTL_messages_stickerSet.thumb.location, null, "webp", null);
      return;
      this.textView.setAlpha(1.0F);
      this.valueTextView.setAlpha(1.0F);
      this.imageView.setAlpha(1.0F);
    }
    this.valueTextView.setText(LocaleController.formatPluralString("Stickers", 0));
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.StickerSetCell
 * JD-Core Version:    0.6.0
 */