package org.vidogram.messenger.exoplayer2.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import java.util.ArrayList;
import java.util.List;
import org.vidogram.messenger.exoplayer2.text.CaptionStyleCompat;
import org.vidogram.messenger.exoplayer2.text.Cue;
import org.vidogram.messenger.exoplayer2.text.TextRenderer.Output;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class SubtitleView extends View
  implements TextRenderer.Output
{
  private static final int ABSOLUTE = 2;
  public static final float DEFAULT_BOTTOM_PADDING_FRACTION = 0.08F;
  public static final float DEFAULT_TEXT_SIZE_FRACTION = 0.0533F;
  private static final int FRACTIONAL = 0;
  private static final int FRACTIONAL_IGNORE_PADDING = 1;
  private boolean applyEmbeddedStyles = true;
  private float bottomPaddingFraction = 0.08F;
  private List<Cue> cues;
  private final List<SubtitlePainter> painters = new ArrayList();
  private CaptionStyleCompat style = CaptionStyleCompat.DEFAULT;
  private float textSize = 0.0533F;
  private int textSizeType = 0;

  public SubtitleView(Context paramContext)
  {
    this(paramContext, null);
  }

  public SubtitleView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  @TargetApi(19)
  private float getUserCaptionFontScaleV19()
  {
    return ((CaptioningManager)getContext().getSystemService("captioning")).getFontScale();
  }

  @TargetApi(19)
  private CaptionStyleCompat getUserCaptionStyleV19()
  {
    return CaptionStyleCompat.createFromCaptionStyle(((CaptioningManager)getContext().getSystemService("captioning")).getUserStyle());
  }

  private void setTextSize(int paramInt, float paramFloat)
  {
    if ((this.textSizeType == paramInt) && (this.textSize == paramFloat))
      return;
    this.textSizeType = paramInt;
    this.textSize = paramFloat;
    invalidate();
  }

  public void dispatchDraw(Canvas paramCanvas)
  {
    int i;
    int i2;
    int k;
    int m;
    int n;
    int i1;
    if (this.cues == null)
    {
      i = 0;
      j = getTop();
      i2 = getBottom();
      k = getLeft() + getPaddingLeft();
      m = j + getPaddingTop();
      n = getRight() + getPaddingRight();
      i1 = i2 - getPaddingBottom();
      if ((i1 > m) && (n > k))
        break label89;
    }
    while (true)
    {
      return;
      i = this.cues.size();
      break;
      label89: if (this.textSizeType != 2)
        break label179;
      f = this.textSize;
      label102: if (f <= 0.0F)
        break label205;
      j = 0;
      while (j < i)
      {
        ((SubtitlePainter)this.painters.get(j)).draw((Cue)this.cues.get(j), this.applyEmbeddedStyles, this.style, f, this.bottomPaddingFraction, paramCanvas, k, m, n, i1);
        j += 1;
      }
    }
    label179: float f = this.textSize;
    if (this.textSizeType == 0);
    for (int j = i1 - m; ; j = i2 - j)
    {
      f *= j;
      break label102;
      label205: break;
    }
  }

  public void onCues(List<Cue> paramList)
  {
    setCues(paramList);
  }

  public void setApplyEmbeddedStyles(boolean paramBoolean)
  {
    if (this.applyEmbeddedStyles == paramBoolean)
      return;
    this.applyEmbeddedStyles = paramBoolean;
    invalidate();
  }

  public void setBottomPaddingFraction(float paramFloat)
  {
    if (this.bottomPaddingFraction == paramFloat)
      return;
    this.bottomPaddingFraction = paramFloat;
    invalidate();
  }

  public void setCues(List<Cue> paramList)
  {
    if (this.cues == paramList)
      return;
    this.cues = paramList;
    int i;
    if (paramList == null)
      i = 0;
    while (this.painters.size() < i)
    {
      this.painters.add(new SubtitlePainter(getContext()));
      continue;
      i = paramList.size();
    }
    invalidate();
  }

  public void setFixedTextSize(int paramInt, float paramFloat)
  {
    Object localObject = getContext();
    if (localObject == null);
    for (localObject = Resources.getSystem(); ; localObject = ((Context)localObject).getResources())
    {
      setTextSize(2, TypedValue.applyDimension(paramInt, paramFloat, ((Resources)localObject).getDisplayMetrics()));
      return;
    }
  }

  public void setFractionalTextSize(float paramFloat)
  {
    setFractionalTextSize(paramFloat, false);
  }

  public void setFractionalTextSize(float paramFloat, boolean paramBoolean)
  {
    if (paramBoolean);
    for (int i = 1; ; i = 0)
    {
      setTextSize(i, paramFloat);
      return;
    }
  }

  public void setStyle(CaptionStyleCompat paramCaptionStyleCompat)
  {
    if (this.style == paramCaptionStyleCompat)
      return;
    this.style = paramCaptionStyleCompat;
    invalidate();
  }

  public void setUserDefaultStyle()
  {
    if ((Util.SDK_INT >= 19) && (!isInEditMode()));
    for (CaptionStyleCompat localCaptionStyleCompat = getUserCaptionStyleV19(); ; localCaptionStyleCompat = CaptionStyleCompat.DEFAULT)
    {
      setStyle(localCaptionStyleCompat);
      return;
    }
  }

  public void setUserDefaultTextSize()
  {
    float f;
    if ((Util.SDK_INT >= 19) && (!isInEditMode()))
      f = getUserCaptionFontScaleV19();
    while (true)
    {
      setFractionalTextSize(f * 0.0533F);
      return;
      f = 1.0F;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.ui.SubtitleView
 * JD-Core Version:    0.6.0
 */