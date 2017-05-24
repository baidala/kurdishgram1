package org.vidogram.messenger.exoplayer2.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import org.vidogram.messenger.exoplayer2.text.CaptionStyleCompat;
import org.vidogram.messenger.exoplayer2.text.Cue;
import org.vidogram.messenger.exoplayer2.util.Util;

final class SubtitlePainter
{
  private static final float INNER_PADDING_RATIO = 0.125F;
  private static final String TAG = "SubtitlePainter";
  private boolean applyEmbeddedStyles;
  private int backgroundColor;
  private float bottomPaddingFraction;
  private final float cornerRadius;
  private float cueLine;
  private int cueLineAnchor;
  private int cueLineType;
  private float cuePosition;
  private int cuePositionAnchor;
  private float cueSize;
  private CharSequence cueText;
  private Layout.Alignment cueTextAlignment;
  private int edgeColor;
  private int edgeType;
  private int foregroundColor;
  private final RectF lineBounds = new RectF();
  private final float outlineWidth;
  private final Paint paint;
  private int parentBottom;
  private int parentLeft;
  private int parentRight;
  private int parentTop;
  private final float shadowOffset;
  private final float shadowRadius;
  private final float spacingAdd;
  private final float spacingMult;
  private StaticLayout textLayout;
  private int textLeft;
  private int textPaddingX;
  private final TextPaint textPaint;
  private float textSizePx;
  private int textTop;
  private int windowColor;

  public SubtitlePainter(Context paramContext)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(null, new int[] { 16843287, 16843288 }, 0, 0);
    this.spacingAdd = localTypedArray.getDimensionPixelSize(0, 0);
    this.spacingMult = localTypedArray.getFloat(1, 1.0F);
    localTypedArray.recycle();
    int i = Math.round(paramContext.getResources().getDisplayMetrics().densityDpi * 2.0F / 160.0F);
    this.cornerRadius = i;
    this.outlineWidth = i;
    this.shadowRadius = i;
    this.shadowOffset = i;
    this.textPaint = new TextPaint();
    this.textPaint.setAntiAlias(true);
    this.textPaint.setSubpixelText(true);
    this.paint = new Paint();
    this.paint.setAntiAlias(true);
    this.paint.setStyle(Paint.Style.FILL);
  }

  private static boolean areCharSequencesEqual(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    return (paramCharSequence1 == paramCharSequence2) || ((paramCharSequence1 != null) && (paramCharSequence1.equals(paramCharSequence2)));
  }

  private void drawLayout(Canvas paramCanvas)
  {
    StaticLayout localStaticLayout = this.textLayout;
    if (localStaticLayout == null)
      return;
    int k = paramCanvas.save();
    paramCanvas.translate(this.textLeft, this.textTop);
    if (Color.alpha(this.windowColor) > 0)
    {
      this.paint.setColor(this.windowColor);
      paramCanvas.drawRect(-this.textPaddingX, 0.0F, localStaticLayout.getWidth() + this.textPaddingX, localStaticLayout.getHeight(), this.paint);
    }
    float f;
    int i;
    if (Color.alpha(this.backgroundColor) > 0)
    {
      this.paint.setColor(this.backgroundColor);
      f = localStaticLayout.getLineTop(0);
      j = localStaticLayout.getLineCount();
      i = 0;
      while (i < j)
      {
        this.lineBounds.left = (localStaticLayout.getLineLeft(i) - this.textPaddingX);
        this.lineBounds.right = (localStaticLayout.getLineRight(i) + this.textPaddingX);
        this.lineBounds.top = f;
        this.lineBounds.bottom = localStaticLayout.getLineBottom(i);
        f = this.lineBounds.bottom;
        paramCanvas.drawRoundRect(this.lineBounds, this.cornerRadius, this.cornerRadius, this.paint);
        i += 1;
      }
    }
    if (this.edgeType == 1)
    {
      this.textPaint.setStrokeJoin(Paint.Join.ROUND);
      this.textPaint.setStrokeWidth(this.outlineWidth);
      this.textPaint.setColor(this.edgeColor);
      this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
      localStaticLayout.draw(paramCanvas);
    }
    do
      while (true)
      {
        this.textPaint.setColor(this.foregroundColor);
        this.textPaint.setStyle(Paint.Style.FILL);
        localStaticLayout.draw(paramCanvas);
        this.textPaint.setShadowLayer(0.0F, 0.0F, 0.0F, 0);
        paramCanvas.restoreToCount(k);
        return;
        if (this.edgeType != 2)
          break;
        this.textPaint.setShadowLayer(this.shadowRadius, this.shadowOffset, this.shadowOffset, this.edgeColor);
      }
    while ((this.edgeType != 3) && (this.edgeType != 4));
    if (this.edgeType == 3)
    {
      j = 1;
      label386: if (j == 0)
        break label478;
      i = -1;
      label393: if (j == 0)
        break label486;
    }
    label478: label486: for (int j = this.edgeColor; ; j = -1)
    {
      f = this.shadowRadius / 2.0F;
      this.textPaint.setColor(this.foregroundColor);
      this.textPaint.setStyle(Paint.Style.FILL);
      this.textPaint.setShadowLayer(this.shadowRadius, -f, -f, i);
      localStaticLayout.draw(paramCanvas);
      this.textPaint.setShadowLayer(this.shadowRadius, f, f, j);
      break;
      j = 0;
      break label386;
      i = this.edgeColor;
      break label393;
    }
  }

  public void draw(Cue paramCue, boolean paramBoolean, CaptionStyleCompat paramCaptionStyleCompat, float paramFloat1, float paramFloat2, Canvas paramCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    CharSequence localCharSequence = paramCue.text;
    if (TextUtils.isEmpty(localCharSequence))
      return;
    Object localObject = localCharSequence;
    if (!paramBoolean)
      localObject = localCharSequence.toString();
    if ((areCharSequencesEqual(this.cueText, (CharSequence)localObject)) && (Util.areEqual(this.cueTextAlignment, paramCue.textAlignment)) && (this.cueLine == paramCue.line) && (this.cueLineType == paramCue.lineType) && (Util.areEqual(Integer.valueOf(this.cueLineAnchor), Integer.valueOf(paramCue.lineAnchor))) && (this.cuePosition == paramCue.position) && (Util.areEqual(Integer.valueOf(this.cuePositionAnchor), Integer.valueOf(paramCue.positionAnchor))) && (this.cueSize == paramCue.size) && (this.applyEmbeddedStyles == paramBoolean) && (this.foregroundColor == paramCaptionStyleCompat.foregroundColor) && (this.backgroundColor == paramCaptionStyleCompat.backgroundColor) && (this.windowColor == paramCaptionStyleCompat.windowColor) && (this.edgeType == paramCaptionStyleCompat.edgeType) && (this.edgeColor == paramCaptionStyleCompat.edgeColor) && (Util.areEqual(this.textPaint.getTypeface(), paramCaptionStyleCompat.typeface)) && (this.textSizePx == paramFloat1) && (this.bottomPaddingFraction == paramFloat2) && (this.parentLeft == paramInt1) && (this.parentTop == paramInt2) && (this.parentRight == paramInt3) && (this.parentBottom == paramInt4))
    {
      drawLayout(paramCanvas);
      return;
    }
    this.cueText = ((CharSequence)localObject);
    this.cueTextAlignment = paramCue.textAlignment;
    this.cueLine = paramCue.line;
    this.cueLineType = paramCue.lineType;
    this.cueLineAnchor = paramCue.lineAnchor;
    this.cuePosition = paramCue.position;
    this.cuePositionAnchor = paramCue.positionAnchor;
    this.cueSize = paramCue.size;
    this.applyEmbeddedStyles = paramBoolean;
    this.foregroundColor = paramCaptionStyleCompat.foregroundColor;
    this.backgroundColor = paramCaptionStyleCompat.backgroundColor;
    this.windowColor = paramCaptionStyleCompat.windowColor;
    this.edgeType = paramCaptionStyleCompat.edgeType;
    this.edgeColor = paramCaptionStyleCompat.edgeColor;
    this.textPaint.setTypeface(paramCaptionStyleCompat.typeface);
    this.textSizePx = paramFloat1;
    this.bottomPaddingFraction = paramFloat2;
    this.parentLeft = paramInt1;
    this.parentTop = paramInt2;
    this.parentRight = paramInt3;
    this.parentBottom = paramInt4;
    paramInt4 = this.parentRight - this.parentLeft;
    int k = this.parentBottom - this.parentTop;
    this.textPaint.setTextSize(paramFloat1);
    int i = (int)(0.125F * paramFloat1 + 0.5F);
    paramInt2 = paramInt4 - i * 2;
    paramInt1 = paramInt2;
    if (this.cueSize != 1.4E-45F)
      paramInt1 = (int)(paramInt2 * this.cueSize);
    if (paramInt1 <= 0)
    {
      Log.w("SubtitlePainter", "Skipped drawing subtitle cue (insufficient space)");
      return;
    }
    if (this.cueTextAlignment == null);
    int j;
    for (paramCue = Layout.Alignment.ALIGN_CENTER; ; paramCue = this.cueTextAlignment)
    {
      this.textLayout = new StaticLayout((CharSequence)localObject, this.textPaint, paramInt1, paramCue, this.spacingMult, this.spacingAdd, true);
      j = this.textLayout.getHeight();
      int m = this.textLayout.getLineCount();
      paramInt2 = 0;
      paramInt3 = 0;
      while (paramInt3 < m)
      {
        paramInt2 = Math.max((int)Math.ceil(this.textLayout.getLineWidth(paramInt3)), paramInt2);
        paramInt3 += 1;
      }
    }
    if ((this.cueSize != 1.4E-45F) && (paramInt2 < paramInt1));
    while (true)
    {
      paramInt3 = paramInt1 + i * 2;
      if (this.cuePosition != 1.4E-45F)
      {
        paramInt2 = Math.round(paramInt4 * this.cuePosition) + this.parentLeft;
        if (this.cuePositionAnchor == 2)
        {
          paramInt1 = paramInt2 - paramInt3;
          paramInt4 = Math.max(paramInt1, this.parentLeft);
          paramInt3 = Math.min(paramInt4 + paramInt3, this.parentRight);
          label741: if (this.cueLine == 1.4E-45F)
            break label1041;
          if (this.cueLineType != 0)
            break label915;
          paramInt2 = Math.round(k * this.cueLine) + this.parentTop;
          label777: if (this.cueLineAnchor != 2)
            break label993;
          paramInt1 = paramInt2 - j;
          label792: if (paramInt1 + j <= this.parentBottom)
            break label1019;
          paramInt2 = this.parentBottom - j;
        }
      }
      while (true)
      {
        this.textLayout = new StaticLayout((CharSequence)localObject, this.textPaint, paramInt3 - paramInt4, paramCue, this.spacingMult, this.spacingAdd, true);
        this.textLeft = paramInt4;
        this.textTop = paramInt2;
        this.textPaddingX = i;
        drawLayout(paramCanvas);
        return;
        paramInt1 = paramInt2;
        if (this.cuePositionAnchor != 1)
          break;
        paramInt1 = (paramInt2 * 2 - paramInt3) / 2;
        break;
        paramInt4 = (paramInt4 - paramInt3) / 2;
        paramInt3 = paramInt4 + paramInt3;
        break label741;
        label915: paramInt1 = this.textLayout.getLineBottom(0) - this.textLayout.getLineTop(0);
        if (this.cueLine >= 0.0F)
        {
          paramFloat1 = this.cueLine;
          paramInt2 = Math.round(paramInt1 * paramFloat1) + this.parentTop;
          break label777;
        }
        paramFloat1 = this.cueLine;
        paramInt2 = Math.round(paramInt1 * paramFloat1) + this.parentBottom;
        break label777;
        label993: paramInt1 = paramInt2;
        if (this.cueLineAnchor != 1)
          break label792;
        paramInt1 = (paramInt2 * 2 - j) / 2;
        break label792;
        label1019: paramInt2 = paramInt1;
        if (paramInt1 >= this.parentTop)
          continue;
        paramInt2 = this.parentTop;
        continue;
        label1041: paramInt2 = this.parentBottom - j - (int)(k * paramFloat2);
      }
      paramInt1 = paramInt2;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.ui.SubtitlePainter
 * JD-Core Version:    0.6.0
 */